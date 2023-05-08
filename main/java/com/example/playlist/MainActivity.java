package com.example.playlist;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static class Playlist {

        private int likespl;
        private String nomeCompleto;
        private String matricula;
        private List<String> filmeIds;

        public Playlist() {}

        public Playlist(String nomeCompleto, String matricula) {
            this.nomeCompleto = nomeCompleto;
            this.matricula = matricula;
            this.likespl = likespl;
            this.filmeIds = new ArrayList<>();
        }

        public List<String> getFilmeIds() {
            return filmeIds;
        }

        public void setFilmeIds(List<String> filmeIds){
            this.filmeIds = filmeIds;
        }

        public void addFilmeId(String filmeId) {
            this.filmeIds.add(filmeId);
        }

        public int getLikespl(){
            return likespl;
        }

        public void setLikespl(int likespl){
            this.likespl = likespl;
        }

        public String getNomeCompleto() {
            return nomeCompleto;
        }

        public void setNomeCompleto(String nomeCompleto) {
            this.nomeCompleto = nomeCompleto;
        }

        public String getMatricula() {
            return matricula;
        }

        public void setMatricula(String matricula) {
            this.matricula = matricula;
        }

        public String toString() {
            return ("PlayList...." + nomeCompleto.toUpperCase() + "\nLikes: " + likespl);
        }

    }


    public static class Filme {
        private String nome;
        private String ano;
        private String id;

        public Filme() {
            // Construtor vazio necessário para o Firebase Database
        }
        public Filme(String id, String nome, String ano) {
            this.id = id;
            this.nome = nome;
            this.ano = ano;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getAno() {
            return ano;
        }

        public void setAno(String ano) {
            this.ano = ano;
        }

        public String toString() {
            return ("Filme:\n" + nome.toUpperCase() + "........Ano..." + ano);
        }
    }

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa o banco de dados Firebase
        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Obtém as referências para os elementos da interface
        final EditText nomeCompleto = findViewById(R.id.idnome_completo);
        final EditText matricula = findViewById(R.id.idmatricula);
        final EditText filme1 = findViewById(R.id.idfilme1);
        final EditText ano1 = findViewById(R.id.idano1);
        final EditText filme2 = findViewById(R.id.idfilme2);
        final EditText ano2 = findViewById(R.id.idano2);
        final EditText filme3 = findViewById(R.id.idfilme3);
        final EditText ano3 = findViewById(R.id.idano3);
        final EditText filme4 = findViewById(R.id.idfilme4);
        final EditText ano4 = findViewById(R.id.idano4);
        Button cadastrar = findViewById(R.id.bt_cadastrar);
        Button playlistsButton = findViewById(R.id.bt_playlist);

        playlistsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Playlists.class);
                startActivity(intent);
            }
        });

        // Define o evento de clique no botão "Cadastrar"
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nome = nomeCompleto.getText().toString().toLowerCase();
                final String mat = matricula.getText().toString().toLowerCase();
                String f1 = filme1.getText().toString().toLowerCase();
                String a1 = ano1.getText().toString().toLowerCase();
                String f2 = filme2.getText().toString().toLowerCase();
                String a2 = ano2.getText().toString().toLowerCase();
                String f3 = filme3.getText().toString().toLowerCase();
                String a3 = ano3.getText().toString().toLowerCase();
                String f4 = filme4.getText().toString().toLowerCase();
                String a4 = ano4.getText().toString().toLowerCase();
                final String[] id1 = {(f1 + "-" + a1)};
                final String[] id2 = {(f2 + "-" + a2)};
                final String[] id3 = {(f3 + "-" + a3)};
                final String[] id4 = {(f4 + "-" + a4)};

                if (f1.isEmpty() || a1.isEmpty() || f2.isEmpty() || a2.isEmpty() || f3.isEmpty() || a3.isEmpty() || f4.isEmpty() || a4.isEmpty() || nome.isEmpty() || mat.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Preencha todos os campos antes de cadastrar.", Toast.LENGTH_SHORT).show();
                } else if ((f1.equals(f2) && a1.equals(a2)) || (f1.equals(f3) && a1.equals(a3)) || (f1.equals(f4) && a1.equals(a4)) || (f2.equals(f3) && a2.equals(a3)) || (f2.equals(f4) && a2.equals(a4)) || (f3.equals(f4) && a3.equals(a4))) {
                    Toast.makeText(MainActivity.this, "Não repita os filmes!", Toast.LENGTH_SHORT).show();
                } else {
                    // Verifica se a matricula já existe no banco de dados
                    Query qmatricula = mDatabase.child("Playlist").orderByChild("matricula").equalTo(mat);
                    qmatricula.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Matrícula já existe, exibe mensagem de erro
                                Toast.makeText(MainActivity.this, "Matrícula já cadastrada!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Matrícula não existe, pode cadastrar a playlist

                                Playlist playlist = new Playlist(nome, mat);

                                playlist.addFilmeId(id1[0]);

                                playlist.addFilmeId(id2[0]);

                                playlist.addFilmeId(id3[0]);

                                playlist.addFilmeId(id4[0]);

                                mDatabase.child("Playlist").child(mat).setValue(playlist);

                                mDatabase.child("Filmes").child(id1[0]).setValue( new Filme(id1[0], f1, a1));

                                mDatabase.child("Filmes").child(id2[0]).setValue( new Filme(id2[0], f2, a2));

                                mDatabase.child("Filmes").child(id3[0]).setValue( new Filme(id3[0], f3, a3));

                                mDatabase.child("Filmes").child(id4[0]).setValue( new Filme(id4[0], f4, a4));

                                nomeCompleto.setText("");
                                matricula.setText("");
                                filme1.setText("");
                                ano1.setText("");
                                filme2.setText("");
                                ano2.setText("");
                                filme3.setText("");
                                ano3.setText("");
                                filme4.setText("");
                                ano4.setText("");

                                // Exibe mensagem de sucesso
                                Toast.makeText(MainActivity.this, "Playlist cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Exibe mensagem de erro em caso de falha na busca
                            Toast.makeText(MainActivity.this, "Erro ao buscar matrícula no banco de dados.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}