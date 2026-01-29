package com.erp.transportadora.validators;

public final class Validador {

    private Validador() {
    }

    /* ================= CPF ================= */

    public static boolean isCPFValido(String cpf) {
        if (cpf == null) return false;

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}"))
            return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++)
                soma += (cpf.charAt(i) - '0') * (10 - i);

            int dig1 = 11 - (soma % 11);
            dig1 = (dig1 >= 10) ? 0 : dig1;

            soma = 0;
            for (int i = 0; i < 10; i++)
                soma += (cpf.charAt(i) - '0') * (11 - i);

            int dig2 = 11 - (soma % 11);
            dig2 = (dig2 >= 10) ? 0 : dig2;

            return dig1 == (cpf.charAt(9) - '0')
                    && dig2 == (cpf.charAt(10) - '0');

        } catch (Exception e) {
            return false;
        }
    }

    /* ================= CNPJ ================= */

    public static boolean isCNPJValido(String cnpj) {
        if (cnpj == null) return false;

        cnpj = cnpj.replaceAll("\\D", "");

        if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}"))
            return false;

        try {
            int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            int soma = 0;
            for (int i = 0; i < 12; i++)
                soma += (cnpj.charAt(i) - '0') * peso1[i];

            int dig1 = soma % 11;
            dig1 = (dig1 < 2) ? 0 : 11 - dig1;

            soma = 0;
            for (int i = 0; i < 13; i++)
                soma += (cnpj.charAt(i) - '0') * peso2[i];

            int dig2 = soma % 11;
            dig2 = (dig2 < 2) ? 0 : 11 - dig2;

            return dig1 == (cnpj.charAt(12) - '0')
                    && dig2 == (cnpj.charAt(13) - '0');

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isTelefoneValido(String telefone) {
        if (telefone == null) return false;

        telefone = telefone.replaceAll("\\D", "");


        if (telefone.length() != 11)
            return false;

        // DDD válido (11 a 99)
        int ddd = Integer.parseInt(telefone.substring(0, 2));
        if (ddd < 11 || ddd > 99)
            return false;

        // Celular precisa começar com 9
        if (telefone.length() == 11 && telefone.charAt(2) != '9')
            return false;

        return true;
    }


}
