package com.example.m3_desenvolvimento_mobile_extensao.adapter;

// Adapter responsável por exibir uma lista de atividades já realizadas em um RecyclerView.

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m3_desenvolvimento_mobile_extensao.R;
import com.example.m3_desenvolvimento_mobile_extensao.banco_de_dados.AtividadeRealizada;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    // Lista de atividades a serem exibidas no RecyclerView
    private List<AtividadeRealizada> atividades = new ArrayList<>();

    // Formato de data usado para exibir a data de confirmação da atividade
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm", Locale.getDefault());

    /**
     * ViewHolder: Representa cada item individual da lista e mantém referências
     * para as Views que serão preenchidas com os dados.
     */
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDescricao;
        public TextView textViewData;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDescricao = itemView.findViewById(R.id.textViewDescricaoHistorico);
            textViewData = itemView.findViewById(R.id.textViewDataHistorico);
        }
    }

    /**
     * Cria uma nova View para um item da lista, inflando o layout XML correspondente.
     */
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_historico, parent, false);
        return new HistoryViewHolder(view);
    }

    /**
     * Associa os dados de uma atividade ao item de lista (ViewHolder) correspondente.
     */
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        AtividadeRealizada atividadeAtual = atividades.get(position);
        holder.textViewDescricao.setText(atividadeAtual.descricao);
        holder.textViewData.setText(sdf.format(atividadeAtual.dataConfirmacao));
    }

    /**
     * Retorna o total de itens a serem exibidos.
     */
    @Override
    public int getItemCount() {
        return atividades.size();
    }

    /**
     * Atualiza a lista de atividades exibidas e avisa o RecyclerView para redesenhar os itens.
     */
    public void setAtividades(List<AtividadeRealizada> novasAtividades) {
        this.atividades = novasAtividades;
        notifyDataSetChanged();
    }
}
