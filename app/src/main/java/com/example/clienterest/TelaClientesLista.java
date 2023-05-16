package com.example.clienterest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.clienterest.model.Cliente;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class TelaClientesLista extends AppCompatActivity {
    ArrayAdapter<Cliente> adapter;
    ListView lista;
    ArrayList<Cliente> clientes;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_clientes_lista);
        gson = new GsonBuilder().create();
        listar();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clientes, menu);
        return true;
    }
    public void listar(){
        BuscadorCliente buscadorCliente = new BuscadorCliente();
        buscadorCliente.execute();
    }



    public void addCliente(MenuItem mi) {
        Intent it = new Intent(TelaClientesLista.this, TelaClientes.class);
        startActivity(it);
    }

    public void removerCliente(MenuItem mi) {
        Removedor removedor = new Removedor();
        String cpf = String.valueOf(clientes.get(lista.getCheckedItemPosition()).getCpf());
        removedor.execute(cpf);
    }

    public void editarCliente(MenuItem mi) {
        Cliente cliente = clientes.get(lista.getCheckedItemPosition());
        Intent it = new Intent(TelaClientesLista.this, TelaClientes.class);
        it.putExtra("cliente", cliente);
        startActivity(it);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listar();
    }
    public class BuscadorCliente extends AsyncTask<String, Void, Cliente[]> {
        @Override
        protected Cliente[] doInBackground(String... strings) {
            try {
                URL url = new URL("http://argo.td.utfpr.edu.br/clients/ws/cliente/");
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
                    Cliente[] clientes = gson.fromJson(json, Cliente[].class);

                    return clientes;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        public void onPostExecute(Cliente[] c) {

            clientes = new ArrayList<>(Arrays.asList(c));
            lista = ((ListView) findViewById(R.id.lista_clientes));
            adapter = new ArrayAdapter<Cliente>(TelaClientesLista.this, android.R.layout.simple_list_item_single_choice, clientes);
            lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lista.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }

    }
    private  class Removedor extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                String cpf  = strings[0];
                System.out.println(cpf);
                URL url = new URL("http://argo.td.utfpr.edu.br/clients/ws/cliente/"+cpf);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    return "Cliente deletado com sucesso!";
                } else if (responseCode > 499){
                    return "O cliente tem pedidos e não pode ser apagado. ";
                }else {
                    return "Falha ao deletar o cliente: " + responseCode;
                }
            } catch (IOException e) {
                return "Erro ao conectar ao servidor: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            listar();
        }
}
}