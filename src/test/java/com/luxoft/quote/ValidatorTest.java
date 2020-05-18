package com.luxoft.quote;

import com.luxoft.quote.domain.Quote;
import com.luxoft.quote.web.validation.QuoteValidator;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.Assert;

import java.math.BigDecimal;

@SpringJUnitConfig
public class ValidatorTest {

    private static final String isin = "RU000A0JX0J0";
    private static final BigDecimal ask = BigDecimal.valueOf(1100);
    private static final BigDecimal bid = BigDecimal.valueOf(1001);
    private QuoteValidator validator = new QuoteValidator();

    @Test
    void testQuoteNotNull() {
        Quote quote = null;
        Assert.isTrue(!validator.validate(quote),"'quote != null' rule not validated.");
    }

    @Test
    void testIsinNotNull() {
        Quote quote = new Quote(null, ask, bid);
        Assert.isTrue(!validator.validate(quote),"'quote.getIsin() != null' rule not validated.");
    }

    @Test
    void testIsinLengthIs12() {
        Quote quote = new Quote(isin.substring(1), ask, bid);
        Assert.isTrue(!validator.validate(quote),"'quote.getIsin().length() == 12' rule not validated.");
    }

    @Test
    void testAskNullAndBidNull_ThenTrue() {
        Quote quote = new Quote(isin, null, null);
        Assert.isTrue(!validator.validate(quote),"'!(quote.getAsk() == null && quote.getBid() == null)' rule not validated.");
    }

    @Test
    void testAskNullOrBidNull_ThenTrue() {
        Quote quote = new Quote(isin, null, bid);
        Assert.isTrue(validator.validate(quote),"'(quote.getAsk() == null || quote.getBid() == null) then true' rule not validated.");
    }

    @Test
    void testAskGreaterThanBid_ThenTrue() {
        Quote quote = new Quote(isin, ask, bid);
        Assert.isTrue(validator.validate(quote),"'(quote.getAsk().compareTo(quote.getBid()) > 0) then true' rule not validated.");
    }
}