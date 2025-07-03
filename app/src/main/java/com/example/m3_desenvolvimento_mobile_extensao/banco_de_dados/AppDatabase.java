package com.example.m3_desenvolvimento_mobile_extensao.banco_de_dados;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Classe principal de configuração do banco de dados usando Room.
 * Ela representa o banco em si, gerencia instâncias e fornece acesso às DAOs.
 */

// Declara o banco de dados e informa ao Room quais entidades ele deve incluir.
// Também define a versão do banco (necessária para migrações futuras).
@Database(entities = {AtividadeRealizada.class}, version = 1, exportSchema = false)

// Define conversores de tipo personalizados, como Date <-> Long.
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    /**
     * DAO (Data Access Object): fornece os métodos para interagir com a tabela de atividades.
     * O Room irá gerar automaticamente uma implementação desse método abstrato.
     */
    public abstract AtividadeDAO atividadeDAO();

    // Instância única do banco de dados (Singleton) — garante que só exista uma no app inteiro.
    private static volatile AppDatabase INSTANCE;

    /**
     * Fornece acesso à instância do banco de dados.
     * Se ainda não existir, ela será criada com base no contexto da aplicação.
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Cria o banco com o nome "app_idoso_database"
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_idoso_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
