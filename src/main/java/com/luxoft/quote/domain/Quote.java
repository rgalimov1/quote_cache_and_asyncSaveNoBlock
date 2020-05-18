package com.luxoft.quote.domain;

import java.math.BigDecimal;

public class Quote {
    private String isin;
    private BigDecimal ask;
    private BigDecimal bid;

    public Quote() {
    }

    public Quote(String isin, BigDecimal ask, BigDecimal bid) {
        this.isin = isin;
        this.ask = ask;
        this.bid = bid;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }
}
