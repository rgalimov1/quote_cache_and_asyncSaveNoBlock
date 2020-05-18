package com.luxoft.quote.service;

import com.luxoft.quote.businesslogic.Calculator;
import com.luxoft.quote.dao.ElvlRepository;
import com.luxoft.quote.domain.Elvl;
import com.luxoft.quote.domain.Quote;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class ElvlService {
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Quote>> sameIsinQuoteChainsMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Item> elvlCache = new ConcurrentHashMap<>();
    private ElvlRepository elvlRepository;
    private ExecutorService executorService;
    private ScheduledExecutorService cronService;
    private Calculator calculator;

    public ElvlService(ElvlRepository elvlRepository, Calculator calculator) {
        this.elvlRepository = elvlRepository;
        this.calculator = calculator;
        this.executorService = Executors.newFixedThreadPool(2);
        this.cronService = Executors.newScheduledThreadPool(1);
        cronService.scheduleAtFixedRate(cronTask, 0, 30, TimeUnit.SECONDS);
    }

    public Elvl getElvl(String isin) {
        Item item = elvlCache.get(isin);
        Elvl elvl = item == null ? null : item.getElvl();

        Iterator<Quote> iterator;
        if (item != null) {
            iterator = item.getQuoteIterator();
        } else if (sameIsinQuoteChainsMap.get(isin) != null) {
            iterator = sameIsinQuoteChainsMap.get(isin).iterator();
        } else {
            return null;
        }

        while (iterator.hasNext()) {
            /*
             * In current implementation right now there is a critical bug, because
             * here in getElvl the iterator gets moved to the end of the queue, while
             * we need it to remain unchanged, we just need to go to the end of the queue.
             * If the iterator was copyable, we would copy the iterator here and then call
             * next() on the copy. A possible solutiton is to use reflection to get the
             * private Node class of the ConcurrentLinkedQueue, and implement iteration
             * using the next reference. Unfortunately didn't finish this part of the implementation.
             */
            elvl = calculator.calculateElvl(iterator.next(), elvl);
        }
        return elvl;
    }

    public void addQuote(Quote quote) {
        sameIsinQuoteChainsMap.putIfAbsent(quote.getIsin(), new ConcurrentLinkedQueue<>());
        ConcurrentLinkedQueue<Quote> queue = sameIsinQuoteChainsMap.get(quote.getIsin());
        queue.add(quote);
    }

    public List<Elvl> getElvls() {
        List<Elvl> elvls = new LinkedList<>();
        for (String isin : sameIsinQuoteChainsMap.keySet()) {
            elvls.add(getElvl(isin));
        }
        return elvls;
    }

    private Runnable cronTask = () -> {

            for (String isin : sameIsinQuoteChainsMap.keySet()) {

                // Remove processed qoutes
                Item item = elvlCache.get(isin);
                ConcurrentLinkedQueue<Quote> queue = sameIsinQuoteChainsMap.get(isin);
                if (item != null) {
                    while (!queue.isEmpty() && queue.peek() != item.getQuote()) {
                        queue.poll();
                    }
                    // item.getQuote() is also processed
                    queue.poll();
                }

                // Process quotes and update elvlCache
                Iterator<Quote> iterator;
                Quote quote;
                if (item != null) {
                    iterator = item.getQuoteIterator();
                    quote = item.getQuote();
                } else {
                    iterator = queue.iterator();
                    quote = null;
                }

                Elvl elvl = item == null ? null : item.getElvl();
                while (iterator.hasNext()) {
                    quote = iterator.next();
                    elvl = calculator.calculateElvl(quote, elvl);
                }

                elvlCache.put(isin, new Item(elvl, iterator, quote));
            }

            // Save elvls to DB
            for (String isin : elvlCache.keySet()) {
                Elvl elvl = elvlCache.get(isin).getElvl();
                executorService.submit(() -> elvlRepository.updateElvl(elvl));
            }
    };

    private class Item {
        private Elvl elvl;
        private Iterator<Quote> quoteIterator;
        private Quote quote;

        public Item(Elvl elvl, Iterator<Quote> quoteIterator, Quote quote) {
            this.elvl = elvl;
            this.quoteIterator = quoteIterator;
            this.quote = quote;
        }

        public Elvl getElvl() {
            return elvl;
        }

        public Iterator<Quote> getQuoteIterator() {
            return quoteIterator;
        }

        public Quote getQuote() {
            return quote;
        }
    }
}
