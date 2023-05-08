package com.example.playlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {
    private List<MainActivity.Filme> playlistFilmes;
    private ArrayAdapter<MainActivity.Filme> playlistFilmesAdapter;
    private DatabaseReference mDatabase;
    private boolean liked = false;
    private SharedPreferences sharedPreferences;
    private ToggleButton toggleButton;
    private DatabaseReference playlistRef;
    private TextView nomecompletoTextView;
    private int likes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);

        // Encontre a instância SharedPreferences
        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);

        // Obtenha o id e o nome da lista de reprodução do Intent que iniciou esta atividade
        Intent intent = getIntent();
        ArrayList<String> filmesIdList = intent.getStringArrayListExtra("filmesId");
        String nome = intent.getStringExtra("nome");
        String matricula = intent.getStringExtra("matricula");
        likes = intent.getIntExtra("likes", 0);
        TextView textView2 = findViewById(R.id.textView2);
        textView2.setText(nome);

        // Encontre o TextView para o nome da lista de reprodução e defina seu texto
        nomecompletoTextView = findViewById(R.id.nomecompleto);
        updateLikesText();

        // Obtenha uma referência ao nó "Lista de reprodução" no banco de dados Firebase
        playlistRef = FirebaseDatabase.getInstance().getReference("Playlist").child(matricula);

        // Obtenha a contagem atual de likespl do banco de dados Firebase e atualize a interface do usuário
        playlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long likespl = snapshot.child("likespl").getValue(Long.class);
                // Atualize o toggleButton com base na contagem atual de likespl
                if (likespl != null && likespl > 0) {
                    liked = true;
                } else {
                    liked = false;
                }
                toggleButton.setChecked(liked);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Registre o erro
                Log.e("PlaylistActivity", "Erro ao obter dados da lista de reprodução", error.toException());
                // Mostra uma mensagem de erro ao usuário
                Toast.makeText(getApplicationContext(), "Falha ao carregar dados da lista de reprodução", Toast.LENGTH_SHORT).show();
            }
        });

        // Encontre o botão de alternância e defina seu estado verificado
        toggleButton = findViewById(R.id.likebutton);
        toggleButton.setChecked(sharedPreferences.getBoolean(matricula, false));

        // Defina o OnClickListener para o toggleButton para incrementar ou decrementar a contagem de likespl
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtenha a contagem atual de likespl do banco de dados Firebase
                playlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long likespl = snapshot.child("likespl").getValue(Long.class);

                        boolean liked = toggleButton.isChecked();
                        if (liked) {
                            // Incrementa a contagem de likespl
                            final Long updatedLikespl = likespl + 1;
                            // Salve a contagem de likespl atualizada no SharedPreferences e no banco de dados Firebase
                            sharedPreferences.edit().putBoolean(matricula, true).apply();
                            playlistRef.child("likespl").setValue(updatedLikespl);
                            likes = updatedLikespl.intValue();
                        } else {
                            // Decrementa a contagem de likespl
                            final Long updatedLikespl = likespl - 1;
                            // Salve a contagem de likespl atualizada no SharedPreferences e no banco de dados Firebase
                            sharedPreferences.edit().putBoolean(matricula, false).apply();
                            playlistRef.child("likespl").setValue(updatedLikespl);
                            likes = updatedLikespl.intValue();
                        }
                        updateLikesText();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Registre o erro
                        Log.e("PlaylistActivity", "Erro ao obter dados da lista de reprodução", error.toException());
                        // Mostra uma mensagem de erro ao usuário
                        Toast.makeText(getApplicationContext(), "Falha ao carregar dados da lista de reprodução", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        // Encontre a ListView dentro da view raiz
        ListView listViewPlaylistFilmes = findViewById(R.id.listViewPlaylistFilmes);

        // Inicialize a fonte de dados e o adaptador da lista de reprodução
        playlistFilmes = new ArrayList<>();
        playlistFilmesAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                R.layout.playlist_item,
                android.R.id.text1,
                playlistFilmes
        );

        // Defina o adaptador para a ListView
        listViewPlaylistFilmes.setAdapter(playlistFilmesAdapter);

        // Obtenha uma referência ao nó "Filme" no banco de dados Firebase
        DatabaseReference filmeRef = FirebaseDatabase.getInstance().getReference("Filmes");

        // Adicione um ValueEventListener para atualizar a lista de reprodução de filmes quando os dados Firebase mudarem
        filmeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Limpa os dados de lista de reprodução atuais e adiciona novos dados do Firebase snapshot
                playlistFilmes.clear();
                for (DataSnapshot filmeSnapshot : snapshot.getChildren()) {
                    MainActivity.Filme filme = filmeSnapshot.getValue(MainActivity.Filme.class);
                    if (filmesIdList.contains(filme.getId())) {
                        playlistFilmes.add(filme);
                    }
                }
                // Notifique o adaptador de que os dados mudaraam
                playlistFilmesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Registre o erro
                Log.e("PlaylistActivity", "Erro ao obter dados da lista de reprodução", error.toException());
                // Mostra uma mensagem de erro ao usuário
                Toast.makeText(getApplicationContext(), "Falha ao carregar dados da lista de reprodução", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveButtonState(boolean state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("buttonState", state);
        editor.apply();
    }

    private boolean getButtonState() {
        return sharedPreferences.getBoolean("buttonState", false);
    }

    private void updateLikesText() {
        nomecompletoTextView.setText("Likes: " + likes);
    }
}




