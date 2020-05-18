package com.luxoft.quote.web.controller;

import com.luxoft.quote.domain.Quote;
import com.luxoft.quote.service.ElvlService;
import com.luxoft.quote.service.QuoteService;
import com.luxoft.quote.web.validation.QuoteValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuoteController {

    private QuoteValidator quoteValidator;
    private QuoteService quoteService;
    private ElvlService elvlService;

    public QuoteController(QuoteValidator quoteValidator, QuoteService quoteService, ElvlService elvlService) {
        this.quoteValidator = quoteValidator;
        this.quoteService = quoteService;
        this.elvlService = elvlService;
    }

    @PutMapping("/quote")
    @ResponseStatus(HttpStatus.CREATED)
    public boolean addQuote(@RequestBody Quote quote) {
        if (quoteValidator.validate(quote)) {
            elvlService.addQuote(quote);
            quoteService.asyncSaveQuote(quote);
            return true;
        }
        return false;
    }
}
