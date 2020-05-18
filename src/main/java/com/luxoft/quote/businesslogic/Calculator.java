package com.luxoft.quote.businesslogic;

import com.luxoft.quote.domain.Elvl;
import com.luxoft.quote.domain.Quote;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class Calculator {

    public Elvl calculateElvl(Quote quote, Elvl elvlObj) {

        BigDecimal ask = quote.getAsk();
        BigDecimal bid = quote.getBid();
        BigDecimal elvl = elvlObj != null ? elvlObj.getElvl() : null;

        if (elvl != null) {
            if (bid != null && bid.compareTo(elvl) > 0) {
                elvl = bid;
            }
            if (ask != null && ask.compareTo(elvl) < 0) {
                elvl = ask;
            }
        } else if (bid != null) {
                elvl = bid;
        } else {
            elvl = ask;
        }

        return new Elvl(quote.getIsin(), elvl);
    }
}
