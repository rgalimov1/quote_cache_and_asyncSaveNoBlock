package com.luxoft.quote;

import com.luxoft.quote.businesslogic.Calculator;
import com.luxoft.quote.domain.Elvl;
import com.luxoft.quote.domain.Quote;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.Assert;

import java.math.BigDecimal;

@SpringJUnitConfig
public class CalculatorTest {

    private static final String isin = "RU000A0JX0J0";
    private static final BigDecimal ask = BigDecimal.valueOf(1100);
    private static final BigDecimal bid = BigDecimal.valueOf(1001);
    private static final BigDecimal smallElvl = BigDecimal.valueOf(500);
    private static final BigDecimal betweenElvl = BigDecimal.valueOf(1050);
    private static final BigDecimal bigElvl = BigDecimal.valueOf(5000);
    private Calculator calculator = new Calculator();

    @Test
    void testElvlNull() {
        Quote quote = new Quote(isin, ask, bid);
        Elvl elvl = calculator.calculateElvl(quote, null);
        Assert.isTrue(elvl.getElvl().compareTo(bid) == 0,"");
    }

    @Test
    void testElvlNullBidNull() {
        Quote quote = new Quote(isin, ask, null);
        Elvl elvl = calculator.calculateElvl(quote, null);
        Assert.isTrue(elvl.getElvl().compareTo(ask) == 0,"");
    }

    @Test
    void testElvlSmall() {
        Quote quote = new Quote(isin, ask, bid);
        Elvl elvl = calculator.calculateElvl(quote, new Elvl(isin, smallElvl));
        Assert.isTrue(elvl.getElvl().compareTo(bid) == 0,"");
    }

    @Test
    void testElvlSmallAskNull() {
        Quote quote = new Quote(isin, null, bid);
        Elvl elvl = calculator.calculateElvl(quote, new Elvl(isin, smallElvl));
        Assert.isTrue(elvl.getElvl().compareTo(bid) == 0,"");
    }

    @Test
    void testElvlBetween() {
        Quote quote = new Quote(isin, ask, bid);
        Elvl elvl = calculator.calculateElvl(quote, new Elvl(isin, betweenElvl));
        Assert.isTrue(elvl.getElvl().compareTo(betweenElvl) == 0,"");
    }

    @Test
    void testElvlBig() {
        Quote quote = new Quote(isin, ask, bid);
        Elvl elvl = calculator.calculateElvl(quote, new Elvl(isin, bigElvl));
        Assert.isTrue(elvl.getElvl().compareTo(ask) == 0,"");
    }

    @Test
    void testElvlBigBidNull() {
        Quote quote = new Quote(isin, ask, null);
        Elvl elvl = calculator.calculateElvl(quote, new Elvl(isin, bigElvl));
        Assert.isTrue(elvl.getElvl().compareTo(ask) == 0,"");
    }
}