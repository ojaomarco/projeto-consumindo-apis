package com.example.clienterest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clienterest.model.Cliente;
import com.example.clienterest.model.Pedido;
import com.example.clienterest.model.Setor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TelaPedidos extends AppCompatActivity {
    ArrayAdapter<Pedido> adapter;
    ListView lista;
    ArrayList<Pedido> pedidos;
    ArrayAdapter<Cliente> adapterSpinner;
    Spinner spinnerClientes;
    String cpfCliente;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_pedidos);
        gson = new GsonBuilder().create();
        verificaRede();
        BuscadorCliente buscadorCliente = new BuscadorCliente();
        buscadorCliente.execute();
        listarClientes();
    }

    private void listarClientes() {
        spinnerClientes = findViewById(R.id.spinnerClientes);
        spinnerClientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cliente clienteSelecionado = (Cliente) parent.getItemAtPosition(position);
                cpfCliente = clienteSelecionado.getCpf();
                listar();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(TelaPedidos.this, "Selecione um cliente para mostrar os pedidos...", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_pedidos, menu);
            return true;

    }
    public void adicionarPedido(MenuItem  v){
        Intent it = new Intent(TelaPedidos.this, TelaCriarPedido.class);
        startActivity(it);
    }
    public void editarPedido(MenuItem v){
        if(lista.getCheckedItemPosition() >= 0) {
            Pedido pedido = pedidos.get(lista.getCheckedItemPosition());
            Intent it = new Intent(TelaPedidos.this, TelaPedidosEditar.class);
            it.putExtra("pedido", pedido);
            startActivity(it);
        }else{
            Toast.makeText(this, "Selecione um pedido para editar...", Toast.LENGTH_SHORT).show();
            }
    }
    public void removerPedido(MenuItem v){
        Removedor removedor = new Removedor();
        String id = String.valueOf(pedidos.get(lista.getCheckedItemPosition()).getId());
        removedor.execute(id);
    }
    public void listar(){
        BuscadorPedidos buscador = new BuscadorPedidos();
        buscador.execute(cpfCliente);
    }
    private class BuscadorPedidos extends AsyncTask<String, Void, Pedido[]> {
        @Override
        protected Pedido[] doInBackground(String... strings) {
            try {
                String cpf = strings[0];
                URL url = new URL("http://argo.td.utfpr.edu.br/clients/ws/pedido?cpf="+cpf);
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
                    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                    Pedido[] p = gson.fromJson(sb.toString(), Pedido[].class);
                    System.out.println(p);
                    return p;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        public void onPostExecute(Pedido[] p) {
            pedidos = new ArrayList<>(Arrays.asList(p));
            lista = ((ListView) findViewById(R.id.lista_pedidos));
            adapter = new PedidoAdapter(TelaPedidos.this, R.layout.itens_pedidos, pedidos);;
            lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            lista.setAdapter(adapter);
            lista.setSelector(R.color.purple_200);
            adapter.notifyDataSetChanged();
        }
        public class PedidoAdapter extends ArrayAdapter<Pedido> {
            private LayoutInflater inflater;
            private int resourceId;

            public PedidoAdapter(Context context, int resourceId, List<Pedido> pedidos) {
                super(context, resourceId, pedidos);
                inflater = LayoutInflater.from(context);
                this.resourceId = resourceId;
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = inflater.inflate(resourceId, parent, false);
                }

                Pedido pedido = getItem(position);

                TextView campo1 = view.findViewById(R.id.name);
                campo1.setText("CPF Cliente: "+ pedido.getCpfCliente());

                TextView campo2 = view.findViewById(R.id.type);
                campo2.setText("Id Vendedor: "+ pedido.getIdVendedor());


                TextView campo3 = view.findViewById(R.id.idPedido);
                campo3.setText("Id Pedido: "+ pedido.getId());

                boolean isItemSelected = lista.isItemChecked(position);
                view.setActivated(isItemSelected);

                return view;
            }
        }


    }
    private  class Removedor extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                String id  = strings[0];
                System.out.println(id);
                URL url = new URL("http://argo.td.utfpr.edu.br/clients/ws/pedido/"+id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    return "Pedido deletado com sucesso!";
                } else if (responseCode > 499){
                    return "O pedido tem itens e não pode ser apagado. ";
                }else {
                    return "Falha ao deletar o pedido: " + responseCode;
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
    private class BuscadorCliente extends AsyncTask<String, Void, Cliente[]> {
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
            if (c != null) {
                spinnerClientes = findViewById(R.id.spinnerClientes);
                adapterSpinner = new ArrayAdapter<>(TelaPedidos.this, android.R.layout.simple_spinner_item, c);
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClientes.setAdapter(adapterSpinner);
            }
        }

    }
    public void verificaRede(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connectivityManager.getAllNetworks();
        boolean isConnected = false;
        for (Network network : networks) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                isConnected = true;
                break;
            }
        }
        if (!isConnected) {
            Toast.makeText(getApplicationContext(), "Sem conexão com a Internet", Toast.LENGTH_SHORT).show();
        }
    }
}