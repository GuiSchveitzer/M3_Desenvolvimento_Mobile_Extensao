package com.example.m3_desenvolvimento_mobile_extensao.banco_de_dados;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

/**
 * Classe que representa uma tabela no banco de dados chamada "atividades_realizadas".
 * Cada instância desta classe corresponde a uma linha da tabela.
 */

@Entity(tableName = "atividades_realizadas")
public class AtividadeRealizada {

    /**
     * Identificador único da atividade.
     * A anotação @PrimaryKey com autoGenerate = true permite que o Room
     * gere esse ID automaticamente ao inserir a atividade no banco.
     */
    @PrimaryKey(autoGenerate = true)
    public int id;

    /**
     * Descrição da atividade realizada pelo usuário.
     * Ex: "Caminhada", "Leitura", "Meditação", etc.
     */
    public String descricao;

    /**
     * Data e hora em que a atividade foi marcada como concluída.
     * Como o tipo Date não é suportado diretamente pelo SQLite, usamos um TypeConverter.
     */
    @TypeConverters({Converters.class})
    public Date dataConfirmacao;

    /**
     * Construtor usado para criar objetos antes de salvá-los no banco.
     */
    public AtividadeRealizada(String descricao, Date dataConfirmacao) {
        this.descricao = descricao;
        this.dataConfirmacao = dataConfirmacao;
    }
}
