package com.example.m3_desenvolvimento_mobile_extensao.banco_de_dados;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Interface que define como a aplicação vai interagir com a tabela de atividades realizadas.
 * O Room gera automaticamente o código necessário com base nestes métodos e anotações.
 */

@Dao
public interface AtividadeDAO {

    /**
     * Insere uma nova atividade realizada no banco de dados.
     * O Room se encarrega de montar a instrução SQL por trás dessa chamada.
     */
    @Insert
    void inserir(AtividadeRealizada atividade);

    /**
     * Busca todas as atividades já registradas, ordenadas da mais recente para a mais antiga.
     * O uso de LiveData permite que a interface (UI) observe essa lista e atualize automaticamente
     * quando novos dados forem inseridos.
     */
    @Query("SELECT * FROM atividades_realizadas ORDER BY dataConfirmacao DESC")
    LiveData<List<AtividadeRealizada>> buscarTodas();

    /**
     * Retorna uma lista com as atividades realizadas nos últimos 10 dias.
     * Utiliza a função nativa do SQLite para manipulação de datas.
     */
    @Query("SELECT * FROM atividades_realizadas WHERE dataConfirmacao >= date('now', '-10 days')")
    List<AtividadeRealizada> buscarAtividadesNosUltimos10Dias();

    /**
     * Conta quantas atividades foram realizadas **no dia atual**.
     * Esse método é usado, neste caso para evitar notificar o usuário se ele já realizou alguma atividade hoje.
     * A função `strftime` converte o timestamp salvo em milissegundos para data legível.
     */
    @Query("SELECT COUNT(id) FROM atividades_realizadas WHERE date(dataConfirmacao / 1000, 'unixepoch') = date('now')")
    int contarAtividadesDeHoje();
}
