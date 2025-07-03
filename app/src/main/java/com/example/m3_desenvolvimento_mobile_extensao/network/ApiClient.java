package com.example.m3_desenvolvimento_mobile_extensao.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classe responsável por configurar e fornecer uma instância do Retrofit,
 * que será usada para fazer chamadas HTTP (GET, POST, etc.) para APIs REST.
 */
public class ApiClient {

    // URL base onde está localizado o arquivo JSON remoto no GitHub.
    private static final String BASE_URL = "https://raw.githubusercontent.com/GuiSchveitzer/M3_Data/main/";

    // Instância única de Retrofit (Singleton). Reutilizável por toda a aplicação.
    private static Retrofit retrofit = null;

    /**
     * Retorna a instância configurada do Retrofit.
     * Se ainda não existir, cria uma nova com a BASE_URL e um conversor JSON (Gson).
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // Define a URL base para as requisições
                    .addConverterFactory(GsonConverterFactory.create()) // Usa o Gson para converter JSON em objetos Java
                    .build(); // Constrói o objeto Retrofit
        }
        return retrofit;
    }
}
