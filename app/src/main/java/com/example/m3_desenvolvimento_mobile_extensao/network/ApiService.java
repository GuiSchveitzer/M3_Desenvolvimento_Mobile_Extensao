package com.example.m3_desenvolvimento_mobile_extensao.network;

import com.example.m3_desenvolvimento_mobile_extensao.network_model.AtividadeProposta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface que define os endpoints da API que o app irá consumir usando Retrofit.
 * Aqui configuramos o caminho da requisição e o tipo de resposta esperada.
 */
public interface ApiService {

    /**
     * Realiza uma requisição HTTP GET no endpoint "Atividades.json".
     * O Retrofit irá concatenar isso com a BASE_URL definida em ApiClient.
     *
     * Exemplo completo de URL acessada:
     * https://raw.githubusercontent.com/GuiSchveitzer/M3_Data/main/Atividades.json
     *
     * O método retorna um Call que, quando executado, busca e converte o JSON
     * em uma lista de objetos do tipo AtividadeProposta.
     */
    @GET("Atividades.json")
    Call<List<AtividadeProposta>> getAtividadesPropostas();
}
