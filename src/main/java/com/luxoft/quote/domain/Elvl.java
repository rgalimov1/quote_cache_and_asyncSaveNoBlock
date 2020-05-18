package com.luxoft.quote.domain;

import java.math.BigDecimal;

public class Elvl {
    private String isin;
    private BigDecimal elvl;

    public Elvl() {
    }

    public Elvl(String isin, BigDecimal elvl) {
        this.isin = isin;
        this.elvl = elvl;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public BigDecimal getElvl() {
        return elvl;
    }

    public void setElvl(BigDecimal elvl) {
        this.elvl = elvl;
    }
}
