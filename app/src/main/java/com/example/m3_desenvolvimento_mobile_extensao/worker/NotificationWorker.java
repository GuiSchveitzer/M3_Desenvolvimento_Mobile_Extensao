package com.example.m3_desenvolvimento_mobile_extensao.worker;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.m3_desenvolvimento_mobile_extensao.R;
import com.example.m3_desenvolvimento_mobile_extensao.repositorio.RepositorioAtividades;

import java.util.Calendar;

/**
 * NotificationWorker é uma classe que herda de Worker, usada para
 * executar tarefas em segundo plano de forma agendada usando o WorkManager.
 * Neste caso, ele verifica em horários específicos se o usuário tem atividades do dia
 * e envia uma notificação se não houver nenhuma registrada ainda.
 */
public class NotificationWorker extends Worker {

    public static final String UNIQUE_WORK_NAME = "lembreteAtividadeUnico";

    private static final String CHANNEL_ID = "lembrete_atividades_channel";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * Método principal chamado quando a tarefa agendada é executada.
     * Aqui é feita a verificação do horário e da quantidade de atividades registradas para o dia.
     * Se nenhuma atividade estiver registrada, uma notificação é enviada.
     */
    @NonNull
    @Override
    public Result doWork() {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        int[] horasAlvo = {8, 12, 16, 20};
        boolean horaCorreta = false;

        for (int h : horasAlvo) {
            if (h == hour && minute <= 16) {
                horaCorreta = true;
                break;
            }
        }

        if (!horaCorreta) {
            Log.d("NotificationWorker", "Não é hora da notificação (" + hour + "h)");
            return Result.success();
        }

        // Instancia o repositório de atividades e conta as atividades do dia atual
        RepositorioAtividades repo = new RepositorioAtividades((Application) getApplicationContext());
        int atividadesHoje = repo.contarAtividadesDeHojeSync();
        Log.d("NotificationWorker", "Verificando atividades: " + atividadesHoje);

        if (atividadesHoje == 0) {
            Log.d("NotificationWorker", "Sem atividades hoje. Enviando notificação." + hour + minute);
            sendNotification(getApplicationContext());
        } else {
            Log.d("NotificationWorker", "Atividades já registradas. Não enviaremos notificação.");
        }

        return Result.success();
    }

    /**
     * Cria e envia a notificação para o usuário.
     * É feita uma verificação da permissão de notificação antes de prosseguir.
     */
    private void sendNotification(Context context) {
        createNotificationChannel(context); // Garante que o canal de notificação foi criado

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Hora da Atividade!")
                .setContentText("Você tem uma nova atividade para fazer hoje. Vamos lá?")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Verifica se a permissão foi concedida (Android 13+ exige permissão explícita)
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w("NotificationWorker", "Permissão de notificação não concedida.");
            return;
        }

        NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());
        Log.d("NotificationWorker", "Notificação enviada com sucesso.");
    }

    /**
     * Cria um canal de notificação (necessário a partir do Android 8.0)
     * para que o sistema saiba como tratar notificações desse tipo.
     */
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Lembretes de Atividades",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificações para lembrar das atividades diárias");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
