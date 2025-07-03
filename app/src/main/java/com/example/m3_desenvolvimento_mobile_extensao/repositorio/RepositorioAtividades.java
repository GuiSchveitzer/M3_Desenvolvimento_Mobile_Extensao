package com.example.m3_desenvolvimento_mobile_extensao.repositorio;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.m3_desenvolvimento_mobile_extensao.banco_de_dados.AppDatabase;
import com.example.m3_desenvolvimento_mobile_extensao.banco_de_dados.AtividadeDAO;
import com.example.m3_desenvolvimento_mobile_extensao.banco_de_dados.AtividadeRealizada;
import com.example.m3_desenvolvimento_mobile_extensao.network_model.AtividadeProposta;
import com.example.m3_desenvolvimento_mobile_extensao.network.ApiClient;
import com.example.m3_desenvolvimento_mobile_extensao.network.ApiService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Classe que centraliza o acesso a dados (API + banco de dados + cache).
 * Aqui estão implementadas as regras de negócio para:
 * - Buscar uma nova atividade do dia
 * - Lidar com falhas de rede e uso de cache
 * - Inserir e listar atividades realizadas (Room)
 */
public class RepositorioAtividades {

    private final AtividadeDAO atividadeDAO;
    private final ApiService apiService;
    private final LiveData<List<AtividadeRealizada>> todasAtividades;
    private final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<String> atividadeDoDia = new MutableLiveData<>();

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    private static final String CACHE_KEY = "cache_atividades_json";
    private static final String PREFS_NAME = "app_cache_prefs";

    private static final String ATIVIDADE_DO_DIA_KEY = "atividade_do_dia";
    private static final String DATA_ATIVIDADE_DO_DIA_KEY = "data_atividade_do_dia";

    public RepositorioAtividades(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.atividadeDAO = db.atividadeDAO();
        this.todasAtividades = atividadeDAO.buscarTodas();

        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public LiveData<String> getAtividadeDoDia() {
        return atividadeDoDia;
    }

    /**
     * Método principal para obter a atividade do dia.
     * Verifica se já existe uma atividade salva para hoje. Se não, busca uma nova da API.
     */
    public void buscarNovaAtividadeDoDia() {
        String hoje = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String dataSalva = sharedPreferences.getString(DATA_ATIVIDADE_DO_DIA_KEY, null);

        if (hoje.equals(dataSalva)) {
            String atividadeSalva = sharedPreferences.getString(ATIVIDADE_DO_DIA_KEY, null);
            if (atividadeSalva != null) {
                atividadeDoDia.postValue(atividadeSalva);
                Log.i("Repository", "Atividade do dia carregada da memória.");
                return;
            }
        }

        // Se for um novo dia ou não tiver atividade salva, busca na API
        Log.i("Repository", "Buscando nova atividade do dia via API...");
        apiService.getAtividadesPropostas().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<AtividadeProposta>> call, Response<List<AtividadeProposta>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    salvarListaNoCache(response.body());
                    sortearSalvarEPostarAtividade(response.body());
                } else {
                    Log.e("Repository", "Erro na API. Tentando usar cache.");
                    usarCacheParaDefinirAtividade();
                }
            }

            @Override
            public void onFailure(Call<List<AtividadeProposta>> call, Throwable t) {
                Log.e("Repository", "Falha na conexão com a API. Tentando usar cache.", t);
                usarCacheParaDefinirAtividade();
            }
        });
    }

    /** Salva a lista completa de atividades como JSON em cache (SharedPreferences) */
    private void salvarListaNoCache(List<AtividadeProposta> lista) {
        sharedPreferences.edit()
                .putString(CACHE_KEY, gson.toJson(lista))
                .apply();
        Log.i("Repository", "Cache de atividades salvo.");
    }

    /** Lê a lista de atividades salvas em cache */
    private List<AtividadeProposta> carregarListaDoCache() {
        String jsonLista = sharedPreferences.getString(CACHE_KEY, null);
        if (jsonLista == null) return Collections.emptyList();

        Type listType = new TypeToken<List<AtividadeProposta>>() {}.getType();
        return gson.fromJson(jsonLista, listType);
    }

    /** Usa o cache local para sortear a atividade do dia, caso não consiga acessar a API */
    private void usarCacheParaDefinirAtividade() {
        List<AtividadeProposta> lista = carregarListaDoCache();
        if (!lista.isEmpty()) {
            sortearSalvarEPostarAtividade(lista);
        } else {
            atividadeDoDia.postValue("Não foi possível buscar atividades. Verifique sua conexão.");
        }
    }

    /**
     * Sorteia aleatoriamente uma atividade da lista,
     * salva no SharedPreferences como "atividade do dia",
     * e atualiza o LiveData para que a UI exiba.
     */
    private void sortearSalvarEPostarAtividade(List<AtividadeProposta> lista) {
        String atividade = lista.get(new Random().nextInt(lista.size())).getTextoAtividade();
        String hoje = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        sharedPreferences.edit()
                .putString(ATIVIDADE_DO_DIA_KEY, atividade)
                .putString(DATA_ATIVIDADE_DO_DIA_KEY, hoje)
                .apply();

        Log.i("Repository", "Atividade do dia definida: " + atividade);
        atividadeDoDia.postValue(atividade);
    }

    // Métodos auxiliares para acesso ao banco de dados local (Room)
    public LiveData<List<AtividadeRealizada>> getTodasAtividades() {
        return todasAtividades;
    }

    public void inserir(AtividadeRealizada atividade) {
        databaseWriteExecutor.execute(() -> atividadeDAO.inserir(atividade));
    }

    public int contarAtividadesDeHojeSync() {
        return atividadeDAO.contarAtividadesDeHoje();
    }
}
