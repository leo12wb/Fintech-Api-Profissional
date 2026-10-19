package com.nexbank.domain.shared;

public enum Currency {
    BRL, USD;

    public int decimalPlaces() {
        return 2;
    }
}
