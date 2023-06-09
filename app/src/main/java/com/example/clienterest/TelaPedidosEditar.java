package com.example.clienterest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clienterest.model.Cliente;
import com.example.clienterest.model.ItemPedido;
import com.example.clienterest.model.Pedido;
import com.example.clienterest.model.Produto;
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
import java.util.List;

public class TelaPedidosEditar extends AppCompatActivity {
    Spinner spinnerSetores, spinnerProdutos;
    ListView lista;
    ArrayAdapter<ItemPedido> adapter;
    ArrayAdapter<Setor> adapterSetores;
    ArrayAdapter<Produto> adapterProdutos;
    EditText qtd, precoUnit;
    Long idProdutoSelecionado;
    List<ItemPedido> itensPedido;
    Pedido pedidoEditar, pedidoNovo;
    Gson gson;
    boolean editando = false;
    // TODO: 16/05/2023 continuar a fazer os metodos http para inserir e alterar os registros
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_pedidos_editar);
        gson = new GsonBuilder().create();
        if(getIntent().getSerializableExtra("pedido")!=null){
            pedidoEditar = (Pedido) getIntent().getSerializableExtra("pedido");
            System.out.println("editando..."+pedidoEditar.toString());
            editando = true;
            itensPedido = pedidoEditar.getItens();

            listarSetores();
        }else {
            System.out.println("nao editando...");
            pedidoNovo = (Pedido) getIntent().getSerializableExtra("pedidoNovo");
            itensPedido = new ArrayList<>();
            listarSetores();

        }
        lista = ((ListView) findViewById(R.id.lista_itens_pedido));
        adapter = new ArrayAdapter<ItemPedido>(TelaPedidosEditar.this, android.R.layout.simple_list_item_single_choice, itensPedido);
        lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lista.setAdapter(adapter);

        spinnerProdutos = findViewById(R.id.spinner_itens);
        spinnerProdutos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Produto prodSelecionado = (Produto) parent.getItemAtPosition(position);
                 idProdutoSelecionado = prodSelecionado.getId();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(TelaPedidosEditar.this, "Selecione um setor para mostrar os itens...", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void listarSetores(){
        BuscadorSetores buscador = new BuscadorSetores();
        buscador.execute();
        spinnerSetores = findViewById(R.id.spinner_setores);
        spinnerSetores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Setor setorSelecionado = (Setor) parent.getItemAtPosition(position);
                    String idSetor = String.valueOf(setorSelecionado.getId());
                    listarItens(idSetor);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(TelaPedidosEditar.this, "Selecione um setor para mostrar os itens...", Toast.LENGTH_SHORT).show();
                }
            });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_pedido, menu);
        return true;

    }
    public void listarItens(String idSetor){
        BuscadorItens buscador = new BuscadorItens();
        buscador.execute(idSetor);
    }
    public void adicionarItem(View v){
        try {
            qtd = (EditText) findViewById(R.id.qtd);
            precoUnit = (EditText) findViewById(R.id.preco_unit);
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setIdProduto(idProdutoSelecionado);
            itemPedido.setQuantidade(Double.parseDouble(qtd.getText().toString()));
            itemPedido.setPrecoUnitario(Double.parseDouble(precoUnit.getText().toString()));
            itensPedido.add(itemPedido);
            
            if(editando)
                pedidoEditar.setItens(itensPedido);
            else
                pedidoNovo.setItens(itensPedido);
            
            Toast.makeText(this, "Item adicionado ao pedido...", Toast.LENGTH_SHORT).show();
            spinnerProdutos.setSelected(false);
            spinnerSetores.setSelected(false
            );
            qtd.setText("");
            precoUnit.setText("");
            adapter.notifyDataSetChanged();


        }catch (Exception e){
            Toast.makeText(this, "Preencha todos os campos antes de adicionar..", Toast.LENGTH_SHORT).show();
        }
    }
    public void voltar(View v){
        if(editando)
            putPedidoEditado();
        else
            postPedidoNovo();
        Intent it = new Intent(this , TelaPedidos.class);
        startActivity(it);
        finish();
    }

    private void postPedidoNovo() {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        String json = gson.toJson( pedidoNovo );
        new Thread() {
            public void run(){
                Looper.prepare();
                try {
                    URL url = new URL("http://argo.td.utfpr.edu.br/clients/ws/pedido");
                    HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
                    cnx.setRequestMethod("POST");
                    cnx.setRequestProperty("Content-Type","application/json");
                    PrintWriter saida = new PrintWriter(cnx.getOutputStream() );
                    saida.println(json);
                    saida.flush();
                    cnx.connect();
                    if (cnx.getResponseCode() == 201){
                        Toast.makeText(getApplicationContext(),
                                "Pedido cadastrado com sucesso!",
                                Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Falha no cadastro do Pedido...",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                Looper.loop();
            }
        }.start();
    }
    private void putPedidoEditado() {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        String json = gson.toJson( pedidoEditar );
        long idPedidoEditar = pedidoEditar.getId();
        new Thread() {
            public void run(){
                Looper.prepare();
                try {
                    URL url = new URL("http://argo.td.utfpr.edu.br/clients/ws/pedido/"+idPedidoEditar);
                    HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
                    cnx.setRequestMethod("PUT");
                    cnx.setRequestProperty("Content-Type","application/json");
                    PrintWriter saida = new PrintWriter(cnx.getOutputStream() );
                    saida.println(json);
                    saida.flush();
                    cnx.connect();
                    if (cnx.getResponseCode() == 204){
                        Toast.makeText(getApplicationContext(),
                                "Pedido Alterado com sucesso!",
                                Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Falha ao alterar Pedido.",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                Looper.loop();
            }
        }.start();
        
    }
    public void removerItemPedido(MenuItem mi){
        int pos = lista.getCheckedItemPosition();
        itensPedido.remove(pos);
        adapter.notifyDataSetChanged();
    }

    private class BuscadorSetores extends AsyncTask<String, Void, Setor[]> {
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
            if (setores != null) {
                spinnerSetores = findViewById(R.id.spinner_setores);
                adapterSetores = new SetorSpinnerAdapter(TelaPedidosEditar.this, Arrays.asList(setores));
                adapterSetores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSetores.setAdapter(adapterSetores);
            }

        }
        public class SetorSpinnerAdapter extends ArrayAdapter<Setor> {
            public SetorSpinnerAdapter(Context context, List<Setor> setoresL) {
                super(context, 0, setoresL);
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) convertView;
                if (textView == null) {
                    textView = (TextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
                }
                Setor setor = getItem(position);
                if (setor != null) {
                    textView.setText(setor.getDescricao());
                }
                return textView;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return getView(position, convertView, parent);
            }
        }

    }
    private class BuscadorItens extends AsyncTask<String, Void, Produto[]> {
        @Override
        protected Produto[] doInBackground(String... strings) {
            String id = strings[0];
            try {
                URL url = new URL("http://argo.td.utfpr.edu.br/clients/ws/produto?setor="+id);
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
                    Produto[] produtos = gson.fromJson(json, Produto[].class);

                    return produtos;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        public void onPostExecute(Produto[] produtos) {
            if (produtos != null) {
                spinnerProdutos = findViewById(R.id.spinner_itens);
                adapterProdutos = new ProdutoSpinnerAdapter(TelaPedidosEditar.this, Arrays.asList(produtos));
                adapterProdutos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProdutos.setAdapter(adapterProdutos);
                spinnerProdutos.setSelected(false);
            }
        }
        public class ProdutoSpinnerAdapter extends ArrayAdapter<Produto> {
            public ProdutoSpinnerAdapter(Context context, List<Produto> produtosL) {
                super(context, 0, produtosL);
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) convertView;
                if (textView == null) {
                    textView = (TextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
                }
                Produto produto = getItem(position);
                if (produto != null) {
                    textView.setText(produto.getDescricao());
                }
                return textView;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return getView(position, convertView, parent);
            }
        }
    }
}