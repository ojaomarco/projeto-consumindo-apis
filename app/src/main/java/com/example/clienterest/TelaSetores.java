package com.example.clienterest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.clienterest.model.Setor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class TelaSetores extends AppCompatActivity {

    ArrayAdapter<Setor> adapter;
    ListView lista;
    ArrayList<Setor> sets;
    Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_setores);
        gson = new GsonBuilder().create();
        listar();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setores, menu);
        return true;
    }
    public void inserir(View v) {
        String descricao = ((EditText) findViewById(R.id.edSetor)).getText().toString();
        double margem = Double.parseDouble(((EditText) findViewById(R.id.edMargem)).getText().toString());
        Setor s = new Setor(0, descricao, margem);
        final String json = gson.toJson( s );
        new Thread() {
            public void run(){
                Looper.prepare();
                try {
                    URL url = new URL("http://argo.td.utfpr.edu.br/clients/ws/setor");
                    HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
                    cnx.setRequestMethod("POST");
                    cnx.setRequestProperty("Content-Type","application/json");
                    PrintWriter saida = new PrintWriter(cnx.getOutputStream() );
                    saida.println(json);
                    saida.flush();
                    cnx.connect();
                    if (cnx.getResponseCode() == 201){
                        Toast.makeText(getApplicationContext(),
                                "Setor cadastrado com sucesso!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Falha no cadastro do setor.",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                Looper.loop();
            }
        }.start();
        listar();
        ((EditText) findViewById(R.id.edSetor)).setText("");
        ((EditText) findViewById(R.id.edMargem)).setText("");
    }
    public void excluir(MenuItem mi){
        if(lista.getCheckedItemPosition() != -1) {
            int pos = lista.getCheckedItemPosition();
            Setor setorSel = sets.get(pos);
            long idSetor = setorSel.getId();
            new Thread() {
                public void run() {
                    Looper.prepare();
                    try {
                        URL url = new URL("http://argo.td.utfpr.edu.br/clients/ws/setor/"+idSetor);
                        HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
                        cnx.setRequestMethod("DELETE");
                        cnx.connect();

                        if(cnx.getResponseCode() > 399){
                            Toast.makeText(getApplicationContext(),
                                    "Este setor tem produtos cadastrados, apague-os antes de remover a categoria.",
                                    Toast.LENGTH_LONG).show();
                        } else if (cnx.getResponseCode() == 204) {

                            Toast.makeText(getApplicationContext(),
                                    "Setor Deletado!",
                                    Toast.LENGTH_LONG).show();


                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Falha.",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    Looper.loop();
                }
            }.start();
            adapter.remove(setorSel);
            sets.remove(pos);
            adapter.notifyDataSetChanged();

        }else{
            Toast.makeText(getApplicationContext(),
                    "Selecione um setor para deletar...",
                    Toast.LENGTH_LONG).show();
        }
        listar();
    }
    public void listar(){
        Buscador buscador = new Buscador();
        buscador.execute();
    }
    private class Buscador extends AsyncTask<String, Void, Setor[]> {
        @Override
        protected Setor[] doInBackground(String... strings) {
            try {
                URL url = new URL("http://argo.td.utfpr.edu.br/clients/ws/setor/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String linha;
                    do {
                        linha = reader.readLine();
                        if (linha != null) {
                            sb.append(linha);
                        }
                    } while ( linha != null);
                    String json = sb.toString();
                    Gson gson = new Gson();
                    Setor[] setores = gson.fromJson(json, Setor[].class);

                    return setores;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Setor[] setores) {
            sets = new ArrayList<>(Arrays.asList(setores));
            lista = ((ListView) findViewById(R.id.listaSetores));
            adapter = new ArrayAdapter<Setor>(TelaSetores.this, android.R.layout.simple_list_item_single_choice, sets);
            lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lista.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }

    }
}