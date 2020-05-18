package com.luxoft.quote.web.validation;

import com.luxoft.quote.domain.Quote;
import org.springframework.stereotype.Component;

@Component
public class QuoteValidator {

    public boolean validate(Quote quote) {
        if (quote == null) {
            return false;
        }
        if (quote.getIsin() == null || quote.getIsin().length() != 12) {
            return false;
        }
        if (quote.getAsk() == null && quote.getBid() == null) {
            return false;
        }
        if (quote.getAsk() == null || quote.getBid() == null) {
            return true;
        }
        return quote.getAsk().compareTo(quote.getBid()) > 0;
    }
}
