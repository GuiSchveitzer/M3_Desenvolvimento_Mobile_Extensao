package com.example.m3_desenvolvimento_mobile_extensao;

// Local: app/src/main/java/com/example/m3_desenvolvimento_mobile_extensao/HistoryActivity.java

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m3_desenvolvimento_mobile_extensao.adapter.HistoryAdapter;
import com.example.m3_desenvolvimento_mobile_extensao.viewmodel.MainViewModel;

/**
 * Tela que exibe o histórico de atividades já realizadas pelo usuário.
 * Usa RecyclerView com Adapter + ViewModel (padrão MVVM).
 */
public class HistoryActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Obtém o ViewModel. Reutilizamos o mesmo da Main pois ele já tem acesso ao histórico.
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Configuração básica do RecyclerView
        recyclerView = findViewById(R.id.recyclerViewHistorico);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Inicializa o Adapter e conecta ao RecyclerView
        adapter = new HistoryAdapter();
        recyclerView.setAdapter(adapter);

        // Observa mudanças no histórico de atividades (LiveData)
        viewModel.getHistoricoDeAtividades().observe(this, atividadesRealizadas -> {
            // Quando a lista for atualizada, atualizamos a RecyclerView
            adapter.setAtividades(atividadesRealizadas);
        });
    }
}
