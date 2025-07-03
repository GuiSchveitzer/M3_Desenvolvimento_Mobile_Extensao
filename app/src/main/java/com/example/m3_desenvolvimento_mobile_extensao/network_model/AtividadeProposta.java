package com.example.m3_desenvolvimento_mobile_extensao.network_model;

import com.google.gson.annotations.SerializedName;

/**
 * Classe modelo que representa uma única atividade proposta vinda da API.
 * O Retrofit, usando Gson, vai converter automaticamente o JSON em objetos desta classe.
 */
public class AtividadeProposta {

    /**
     * Campo que representa o texto da atividade, vindo do JSON com a chave "atividade".
     * A anotação @SerializedName garante que, mesmo que o nome da variável Java seja diferente,
     * o mapeamento com o JSON será feito corretamente.
     *
     * Exemplo de JSON esperado:
     * {
     *   "atividade": "Ler um livro por 15 minutos"
     * }
     */
    @SerializedName("atividade")
    private String textoAtividade;

    // Getter — permite que outras partes do código acessem o texto da atividade.
    public String getTextoAtividade() {
        return textoAtividade;
    }

    // Setter — permite modificar o valor da atividade, se necessário.
    public void setTextoAtividade(String textoAtividade) {
        this.textoAtividade = textoAtividade;
    }
}
