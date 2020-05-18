package com.luxoft.quote.service;

import com.luxoft.quote.dao.QuoteRepository;
import com.luxoft.quote.domain.Quote;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class QuoteService {

    private QuoteRepository quoteRepository;
    private ExecutorService executorService;

    public QuoteService(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
        this.executorService = Executors.newFixedThreadPool(2);
    }

    public void asyncSaveQuote(Quote quote) {
        executorService.submit(() -> quoteRepository.addQuote(quote) > 0);
    }
}
