package com.erp.transportadora.validators;

public final class NormalizadorUtils {

    public static String trimUpper(String valor) {
        return valor == null ? null : valor.trim().toUpperCase();
    }

    public static String trimLower(String valor) {
        return valor == null ? null : valor.trim().toLowerCase();
    }

    public static String apenasNumeros(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }
}
