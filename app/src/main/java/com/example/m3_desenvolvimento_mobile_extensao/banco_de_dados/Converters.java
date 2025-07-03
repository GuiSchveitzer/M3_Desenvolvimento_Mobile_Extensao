package com.example.m3_desenvolvimento_mobile_extensao.banco_de_dados;

import androidx.room.TypeConverter;
import java.util.Date;

/**
 * Classe que fornece métodos para converter entre tipos de dados incompatíveis com o SQLite.
 * No caso, convertemos entre 'Date' (objeto do Java) e 'Long' (timestamp em milissegundos),
 * que é um formato que o SQLite consegue armazenar.
 */
public class Converters {

    /**
     * Converte um valor Long (timestamp) vindo do banco em um objeto Date do Java.
     * Se o valor for null, retorna null.
     */
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    /**
     * Converte um objeto Date do Java em um valor Long (timestamp em milissegundos),
     * para que o Room possa armazená-lo no banco de dados.
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
