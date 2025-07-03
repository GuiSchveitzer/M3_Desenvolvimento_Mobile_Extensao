# M3_Desenvolvimento_Mobile_Extensao

Aplicativo Android desenvolvido em Java que propõe desafios diários ao usuário, permitindo que eles registrem sua realização e acompanhem seu progresso ao longo do tempo.

---

## ✨ Funcionalidades

- ✅ Sorteia e exibe uma nova **atividade do dia** a cada novo dia.
- 📅 Permite **confirmar a atividade** e registrar no histórico.
- 📊 Exibe **categoria de engajamento** do usuário (Bronze, Prata, Ouro, Platina).
- 🕓 Notifica o usuário caso ainda não tenha feito a atividade do dia (8h, 12h, 16h, 20h).
- 📜 Tela de histórico com todas as atividades realizadas.
- 💾 Funciona **offline** com cache local e banco de dados.

---

## 🔧 Tecnologias Utilizadas

| Tecnologia           | Função                                          |
|----------------------|--------------------------------------------------|
| Java                 | Linguagem principal                              |
| Android Jetpack      | Lifecycle, ViewModel, LiveData, Room             |
| Retrofit             | Requisições HTTP (JSON via GitHub)               |
| Gson                 | Conversão JSON ↔ Objetos Java                    |
| WorkManager          | Tarefas periódicas para notificações             |
| RecyclerView         | Exibição da lista de histórico                   |
| SharedPreferences    | Cache simples (lista e atividade do dia)         |
| NotificationCompat   | Envio de notificações modernas e compatíveis     |

---

## 🗂️ Estrutura do Projeto

\`\`\`
📁 app/
├── java/com/example/m3_desenvolvimento_mobile_extensao/
│   ├── banco_de_dados/
│   ├── network/
│   ├── network_model/
│   ├── repositorio/
│   ├── viewmodel/
│   ├── worker/
│   ├── MainActivity.java
│   ├── HistoryActivity.java
│   └── MainApplication.java
\`\`\`

---

## 🧠 Arquitetura (MVVM + Repository)

```
[View] MainActivity, HistoryActivity
    ⬇ Observa
[ViewModel] MainViewModel
    ⬇
[Repository] RepositorioAtividades
    ⬇                 ⬇
[Retrofit API]     [Room DB + SharedPreferences]
```

---

## 🔄 Fluxo da Aplicação

1. Ao iniciar, o app agenda o `NotificationWorker`.
2. A tela principal (`MainActivity`) solicita a **atividade do dia**.
3. O `RepositorioAtividades`:
   - Busca a atividade em cache (`SharedPreferences`).
   - Se não houver, consome da API (`Retrofit`).
   - Sorteia uma e salva no cache.
4. O usuário pode confirmar a atividade → salva no `Room`.
5. `MainViewModel` calcula e exibe a **categoria do usuário**.
6. O histórico pode ser visualizado em `HistoryActivity`.
7. Se ainda não houver atividade confirmada, o `NotificationWorker` notifica o usuário nos horários definidos.

---

## 🏅 Categorias do Usuário

| Categoria | Critério                                                    |
|-----------|-------------------------------------------------------------|
| Bronze    | Menos de 3 atividades                                       |
| Prata     | 3 a 6 atividades                                            |
| Ouro      | 7 a 9 atividades                                            |
| Platina   | 10+ atividades com pelo menos **10 dias consecutivos**     |

---

## 📦 Instalação e Execução

1. Clone o repositório:
   \`\`\`bash
   git clone https://github.com/seu-usuario/nome-do-repositorio.git
   \`\`\`

2. Abra no **Android Studio**.

3. Instale em um dispositivo ou emulador:
   - Certifique-se de estar conectado à internet para baixar as atividades da API.
   - Permita notificações (API 33+ solicita permissão).

---

## 📄 Exemplo de Item do Histórico (layout)

\`\`\`xml
<LinearLayout>
   <TextView android:id="@+id/textViewDescricaoHistorico" />
   <TextView android:id="@+id/textViewDataHistorico" />
</LinearLayout>
\`\`\`
