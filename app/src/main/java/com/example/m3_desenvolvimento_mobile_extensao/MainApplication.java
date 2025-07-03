package com.example.m3_desenvolvimento_mobile_extensao;

import android.app.Application;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.m3_desenvolvimento_mobile_extensao.worker.NotificationWorker;

import java.util.concurrent.TimeUnit;

/**
 * Classe que representa o Application do app.
 * É usada para iniciar tarefas globais assim que o app é aberto.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Log para rastrear se o Worker está sendo agendado corretamente
        Log.d("MainApplication", "Agendando notificações periódicas...");

        // Inicia o agendamento do Worker de notificação
        scheduleNotificationWorker();
    }

    /**
     * Agenda o NotificationWorker para executar a cada 15 minutos.
     * O Android limita o intervalo mínimo para WorkManager em 15 minutos.
     */
    private void scheduleNotificationWorker() {
        // Cria uma solicitação de trabalho periódica (worker recorrente)
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                NotificationWorker.class,       // Worker que será executado
                15, TimeUnit.MINUTES
        ).build();

        // Agenda o trabalho usando uma chave única (para evitar agendamento duplicado)
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
                NotificationWorker.UNIQUE_WORK_NAME,    // Nome único para esse tipo de trabalho
                ExistingPeriodicWorkPolicy.KEEP,        // Se já existir, mantém o existente
                workRequest                             // O trabalho a ser executado
        );
    }
}
