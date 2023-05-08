package com.example.playlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.playlist.MainActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Playlists extends AppCompatActivity {
    private ListView listViewPlaylist;
    private ArrayList<MainActivity.Playlist> listPlaylists;
    private ArrayAdapter<MainActivity.Playlist> playlistAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlists);

        // Encontre a ListView dentro da visualização raiz
        listViewPlaylist = findViewById(R.id.ListViewPlaylist);

        // Inicialize a fonte de dados da playlist e o adaptador
        listPlaylists = new ArrayList<>();
        playlistAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                R.layout.playlist_item,
                android.R.id.text1,
                listPlaylists
        );

        // Defina o adaptador para a ListView
        listViewPlaylist.setAdapter(playlistAdapter);

        // Obtenha uma referência ao nó "Playlist" no banco de dados do Firebase
        FirebaseDatabase.getInstance().getReference("Playlist")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Limpe os dados da playlist atual e adicione novos dados do snapshot do Firebase
                        listPlaylists.clear();
                        for (DataSnapshot playlistSnapshot : snapshot.getChildren()) {
                            MainActivity.Playlist playlist = playlistSnapshot.getValue(MainActivity.Playlist.class);
                            listPlaylists.add(playlist);
                        }
                        // Notifique o adaptador que os dados foram alterados
                        playlistAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Registre o erro
                        Log.e("Playlists", "Erro ao obter os dados da playlist", error.toException());
                        // Mostre uma mensagem de erro ao usuário
                        Toast.makeText(getApplicationContext(), "Falha ao carregar os dados da playlist", Toast.LENGTH_SHORT).show();
                    }
                });

        listViewPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtenha a playlist que foi clicada
                MainActivity.Playlist clickedPlaylist = listPlaylists.get(position);

                // Crie um Intent para lançar a PlaylistActivity
                Intent intent = new Intent(Playlists.this, PlaylistActivity.class);
                intent.putStringArrayListExtra("filmesId", (ArrayList<String>) clickedPlaylist.getFilmeIds());
                intent.putExtra("nome", clickedPlaylist.getNomeCompleto());
                intent.putExtra("matricula", clickedPlaylist.getMatricula());
                intent.putExtra("likes", clickedPlaylist.getLikespl());

                // Inicie a PlaylistActivity
                startActivity(intent);
            }
        });

    }
}

