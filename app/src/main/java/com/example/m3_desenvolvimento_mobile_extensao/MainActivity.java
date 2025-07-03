package com.example.m3_desenvolvimento_mobile_extensao;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.m3_desenvolvimento_mobile_extensao.banco_de_dados.AtividadeRealizada;
import com.example.m3_desenvolvimento_mobile_extensao.repositorio.RepositorioAtividades;
import com.example.m3_desenvolvimento_mobile_extensao.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Tela principal do aplicativo: exibe a atividade do dia,
 * categoria do usuário, e permite confirmar a atividade realizada.
 */
public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private TextView textViewCategoria;
    private TextView textViewAtividadeDia;
    private Button buttonConfirmar;
    private Button buttonVerHistorico;

    // Armazena a data da última atividade exibida, para controle de mudanças de dia
    private String diaDaAtividadeExibida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // A partir do Android 13 (TIRAMISU), é preciso pedir permissão para notificações
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Inicializa o ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Referencia os elementos de interface
        textViewCategoria = findViewById(R.id.textViewCategoria);
        textViewAtividadeDia = findViewById(R.id.textViewAtividadeDia);
        buttonConfirmar = findViewById(R.id.buttonConfirmar);
        buttonVerHistorico = findViewById(R.id.buttonVerHistorico);

        // Observa mudanças do ViewModel
        setupObservers();

        // Define ações dos botões
        setupClickListeners();

        // Solicita uma nova atividade (caso ainda não tenha sido definida para hoje)
        viewModel.buscarNovaAtividade();
    }

    // Verifica se mudou o dia quando a activity volta ao foco
    @Override
    protected void onResume() {
        super.onResume();

        if (this.diaDaAtividadeExibida != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String hoje = sdf.format(new Date());

            if (!hoje.equals(this.diaDaAtividadeExibida)) {
                Toast.makeText(this, "Um novo dia, uma nova atividade!", Toast.LENGTH_SHORT).show();
                viewModel.buscarNovaAtividade(); // Nova atividade para novo dia
                buttonConfirmar.setEnabled(true); // Reativa botão
                buttonConfirmar.setText("Confirmar Atividade Realizada");
            }
        }
    }

    /**
     * Conecta os LiveData do ViewModel à interface
     */
    private void setupObservers() {
        // Atualiza o texto da atividade do dia
        viewModel.getAtividadeDoDia().observe(this, atividade -> {
            textViewAtividadeDia.setText(atividade);
            // Atualiza o controle interno de dia
            this.diaDaAtividadeExibida = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        });

        // Atualiza o texto da categoria do usuário
        viewModel.getCategoriaDoUsuario().observe(this, categoria -> {
            textViewCategoria.setText(categoria);
        });

        // Observa o histórico para saber se o botão deve estar ativado
        viewModel.getHistoricoDeAtividades().observe(this, historico -> {
            if (atividadeDeHojeJaFoiFeita(historico)) {
                buttonConfirmar.setEnabled(false);
                buttonConfirmar.setText("Atividade de hoje concluída!");
            } else {
                buttonConfirmar.setEnabled(true);
                buttonConfirmar.setText("Confirmar Atividade Realizada");
            }
        });
    }

    /**
     * Define ações dos botões da tela
     */
    private void setupClickListeners() {
        buttonConfirmar.setOnClickListener(view -> {
            viewModel.confirmarAtividadeRealizada();
            Toast.makeText(this, "Parabéns! Atividade registrada.", Toast.LENGTH_SHORT).show();
        });

        buttonVerHistorico.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Verifica se já existe uma atividade realizada no dia atual
     */
    private boolean atividadeDeHojeJaFoiFeita(List<AtividadeRealizada> historico) {
        if (historico == null || historico.isEmpty()) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String hoje = sdf.format(new Date());

        for (AtividadeRealizada atividade : historico) {
            String dataAtividade = sdf.format(atividade.dataConfirmacao);
            if (dataAtividade.equals(hoje)) {
                return true;
            }
        }
        return false;
    }
}
