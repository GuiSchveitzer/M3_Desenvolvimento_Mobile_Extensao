package com.example.m3_desenvolvimento_mobile_extensao.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.m3_desenvolvimento_mobile_extensao.banco_de_dados.AtividadeRealizada;
import com.example.m3_desenvolvimento_mobile_extensao.repositorio.RepositorioAtividades;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * ViewModel principal que conecta a interface com o repositório de dados.
 * Responsável por:
 * - Expor o LiveData da atividade do dia
 * - Expor o histórico de atividades realizadas
 * - Calcular a "categoria" do usuário (Bronze, Prata, Ouro, Platina)
 * - Lidar com eventos como confirmar uma atividade ou buscar uma nova
 */
public class MainViewModel extends AndroidViewModel {

    private final RepositorioAtividades repository;
    private final LiveData<List<AtividadeRealizada>> historicoDeAtividades;
    private final LiveData<String> atividadeDoDia;
    private final MutableLiveData<String> categoriaDoUsuario = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new RepositorioAtividades(application);
        this.historicoDeAtividades = repository.getTodasAtividades();
        this.atividadeDoDia = repository.getAtividadeDoDia();

        // Observa mudanças no histórico para recalcular a categoria automaticamente
        this.historicoDeAtividades.observeForever(this::calcularCategoria);
    }

    public LiveData<String> getAtividadeDoDia() {
        return atividadeDoDia;
    }

    public LiveData<String> getCategoriaDoUsuario() {
        return categoriaDoUsuario;
    }

    public LiveData<List<AtividadeRealizada>> getHistoricoDeAtividades() {
        return historicoDeAtividades;
    }

    /**
     * Confirma que o usuário realizou a atividade do dia atual
     * e salva no banco com a data/hora atual.
     */
    public void confirmarAtividadeRealizada() {
        String descricao = atividadeDoDia.getValue();
        if (descricao != null && !descricao.isEmpty()) {
            AtividadeRealizada novaAtividade = new AtividadeRealizada(descricao, new Date());
            repository.inserir(novaAtividade);
        }
    }

    /**
     * Força a busca de uma nova atividade do dia (da API ou do cache)
     */
    public void buscarNovaAtividade() {
        repository.buscarNovaAtividadeDoDia();
    }

    // ------------------------------------------------------------------------
    // LÓGICA DE CATEGORIZAÇÃO DO USUÁRIO (BRONZE / PRATA / OURO / PLATINA)
    // ------------------------------------------------------------------------

    /**
     * Atualiza a categoria do usuário com base no histórico de atividades.
     * Regras:
     * - Platina: pelo menos 10 dias consecutivos com atividades
     * - Ouro: 7 ou mais atividades
     * - Prata: 3 a 6 atividades
     * - Bronze: menos de 3 atividades
     */
    private void calcularCategoria(List<AtividadeRealizada> historico) {
        if (historico == null || historico.isEmpty()) {
            categoriaDoUsuario.setValue("Bronze");
            return;
        }

        int totalAtividades = historico.size();

        if (totalAtividades >= 10 && temDezDiasConsecutivos(historico)) {
            categoriaDoUsuario.setValue("Platina");
        } else if (totalAtividades >= 7) {
            categoriaDoUsuario.setValue("Ouro");
        } else if (totalAtividades >= 3) {
            categoriaDoUsuario.setValue("Prata");
        } else {
            categoriaDoUsuario.setValue("Bronze");
        }
    }

    /**
     * Verifica se o usuário realizou atividades em 10 dias seguidos.
     * Usa apenas a data (ignorando hora) para garantir comparações corretas.
     */
    private boolean temDezDiasConsecutivos(List<AtividadeRealizada> historico) {
        if (historico.size() < 10) return false;

        // Usa Set para garantir que datas duplicadas no mesmo dia não influenciem
        Set<Long> diasUnicos = new HashSet<>();
        for (AtividadeRealizada atividade : historico) {
            diasUnicos.add(getDiaDoAno(atividade.dataConfirmacao));
        }

        if (diasUnicos.size() < 10) return false;

        List<Long> diasOrdenados = new ArrayList<>(diasUnicos);
        Collections.sort(diasOrdenados, Collections.reverseOrder());

        for (int i = 0; i < 9; i++) {
            long diaAtual = diasOrdenados.get(i);
            long diaAnterior = diasOrdenados.get(i + 1);
            if (diaAtual - diaAnterior != 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Converte uma data em um número de dias.
     * Ignora hora/minuto/segundo.
     */
    private long getDiaDoAno(Date date) {
        return TimeUnit.MILLISECONDS.toDays(date.getTime());
    }
}
