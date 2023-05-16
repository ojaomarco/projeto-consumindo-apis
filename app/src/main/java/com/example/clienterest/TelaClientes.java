package com.example.clienterest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clienterest.model.Cliente;
import com.example.clienterest.model.Setor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class TelaClientes extends AppCompatActivity {
    EditText edNome, edCpf, edTelefone, edCep, edEndereco, edNumero, edBairro, edCidade, edEstado;
    boolean editando = false;
    Cliente clienteEditar, clienteNovo;
    Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_clientes);
        this.Eds();
        gson = new GsonBuilder().create();
        if(getIntent().getSerializableExtra("cliente")!=null){
            clienteEditar = (Cliente) getIntent().getSerializableExtra("cliente");
            System.out.println("editando..."+clienteEditar.toString());
            editando = true;
            setEds(clienteEditar);
        }else {
            System.out.println("nao editando...");
            clienteNovo = new Cliente();
        }
        carregarDadosCep();
    }
    public void alterarOuCadastrarCliente(View view) {
        if(editando){
            alterarCliente();
        }else {
            cadastrarCliente();
        }
    }
    public void cadastrarCliente() {
        Cliente cliente = edsToCliente();
        final String json = gson.toJson( cliente );
        new Thread() {
            public void run(){
                Looper.prepare();
                try {
                    URL url = new URL("http://argo.td.utfpr.edu.br/clients/ws/cliente");
                    HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
                    cnx.setRequestMethod("POST");
                    cnx.setRequestProperty("Content-Type","application/json");
                    PrintWriter saida = new PrintWriter(cnx.getOutputStream() );
                    saida.println(json);
                    saida.flush();
                    cnx.connect();
                    if (cnx.getResponseCode() == 201){
                        Toast.makeText(getApplicationContext(),
                                "Cliente cadastrado com sucesso!",
                                Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Falha no cadastro do Cliente.",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                Looper.loop();
            }
        }.start();
        setEdsVazio();
    }
    public void alterarCliente() {
        Cliente clienteEditado = edsToCliente();
        String cpf = clienteEditado.getCpf();
        final String json = gson.toJson( clienteEditado );
        StringBuilder sb = new StringBuilder();
        new Thread() {
            public void run(){
                Looper.prepare();
                try {
                    URL url = new URL("http://argo.td.utfpr.edu.br/clients/ws/cliente/"+cpf);
                    HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
                    cnx.setRequestMethod("PUT");
                    cnx.setRequestProperty("Content-Type","application/json");
                    PrintWriter saida = new PrintWriter(cnx.getOutputStream() );
                    saida.println(json);
                    saida.flush();
                    cnx.connect();
                    if (cnx.getResponseCode() == 204){
                        Toast.makeText(getApplicationContext(),
                                "Cliente alterado com sucesso!",
                                Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Falha no cadastro do Cliente.",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                Looper.loop();
            }
        }.start();
    }

    void carregarDadosCep(){
        EditText teste = findViewById(R.id.cep);
        teste.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cep = s.toString();
                if (cep.length() == 8) {
                    TaskCarregaCep taskCarregaCep = new TaskCarregaCep();
                    taskCarregaCep.execute(cep);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    void Eds(){
        edNome = (EditText) findViewById(R.id.nome);
        edCpf = (EditText) findViewById(R.id.cpf);
        edTelefone = (EditText) findViewById(R.id.telefone);
        edCep = (EditText) findViewById(R.id.cep);
        edEndereco = (EditText) findViewById(R.id.endereco);
        edNumero = (EditText) findViewById(R.id.num);
        edBairro = (EditText) findViewById(R.id.bairro);
        edCidade = (EditText) findViewById(R.id.cidade);
        edEstado = (EditText) findViewById(R.id.estado);
    }
    void setEds(Cliente c){
        edNome.setText(c.getNome());
        edCpf.setText(c.getCpf());
        edTelefone.setText(c.getTelefone());
        edCep.setText(c.getCep());
        edEndereco.setText(c.getLogradouro());
        edNumero.setText(c.getNumero());
        edBairro.setText(c.getBairro());
        edCidade.setText(c.getLocalidade());
        edEstado.setText(c.getUf());
    }
    void setEdsVazio(){
        edNome.setText("");
        edCpf.setText("");
        edTelefone.setText("");
        edCep.setText("");
        edEndereco.setText("");
        edNumero.setText("");
        edBairro.setText("");
        edCidade.setText("");
        edEstado.setText("");
    }
    Cliente edsToCliente(){
        this.Eds();
        Cliente c = new Cliente();
        c.setNome(edNome.getText().toString());
        c.setCpf(edCpf.getText().toString());
        c.setTelefone(edTelefone.getText().toString());
        c.setCep(edCep.getText().toString());
        c.setLogradouro(edEndereco.getText().toString());
        c.setNumero(edNumero.getText().toString());
        c.setBairro(edBairro.getText().toString());
        c.setLocalidade(edCidade.getText().toString());
        c.setUf(edEstado.getText().toString());
        return c;
    }

    private  class TaskCarregaCep extends AsyncTask<String, Void, Map> {
        @Override
        protected Map doInBackground(String... strings) {
            try {
                String cep  = strings[0];
                String urls = "https://viacep.com.br/ws/" + cep + "/json/";
                System.out.println(cep);
                URL url = new URL(urls);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
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
                    Gson gson = new GsonBuilder().create();
                    Map m = gson.fromJson( sb.toString(), Map.class);
                    return m;
                }
            } catch (IOException e) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Map m) {
            if (m.containsKey("erro")) {
                Toast.makeText(TelaClientes.this, "Erro ao buscar cep", Toast.LENGTH_SHORT).show();
            } else {
                Eds();
                edCidade.setText(m.get("localidade").toString());
                edEndereco.setText(m.get("logradouro").toString());
                edBairro.setText(m.get("bairro").toString());
                edEstado.setText(m.get("uf").toString());

            }
        }
    }
}