package com.example.clienterest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.clienterest.model.Cliente;
import com.example.clienterest.model.Pedido;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TelaCriarPedido extends AppCompatActivity {
    Date data;
    Spinner spinnerClientes;
    EditText edVendedor, percentualDesconto, dataPedido;

    ArrayAdapter<Cliente> adapterSpinner;

    Cliente clienteSelecionado;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_criar_pedido);
        edVendedor = (EditText) findViewById(R.id.vendedor);
        percentualDesconto = (EditText) findViewById(R.id.percent_desc);
        spinnerClientes = (Spinner) findViewById(R.id.spinner_cliente_pedido);
        BuscadorCliente buscadorCliente = new BuscadorCliente();
        buscadorCliente.execute();

        spinnerClientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cliente clienteSelecionado = (Cliente) parent.getItemAtPosition(position);
                cpfCliente = clienteSelecionado.getCpf();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selecionarData(View v){
        DatePickerDialog dlgData = new DatePickerDialog(this);
        dlgData.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                data = calendar.getTime();
                ((EditText) findViewById(R.id.data_pedido)).setText(sdf.format(data));
            }
        });
        dlgData.show();
    }
    String cpfCliente;
    public void continuar(View v){
        edVendedor = (EditText) findViewById(R.id.vendedor);
        percentualDesconto = (EditText) findViewById(R.id.percent_desc);
        try {
            Pedido pedido = new Pedido();
            pedido.setData(data);
            pedido.setIdVendedor(Long.parseLong(edVendedor.getText().toString()));
            pedido.setCpfCliente(cpfCliente);
            pedido.setPercentualDesconto(Double.parseDouble(percentualDesconto.getText().toString()));
            Intent it = new Intent(TelaCriarPedido.this, TelaPedidosEditar.class);
            it.putExtra("pedidoNovo", pedido);
            startActivity(it);
        }catch (Exception e){
            Toast.makeText(this, "Preencha todos os campos...", Toast.LENGTH_SHORT).show();
        }
    }
    public void cancelar(View v){
        finish();
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
                spinnerClientes = findViewById(R.id.spinner_cliente_pedido);
                adapterSpinner = new ArrayAdapter<>(TelaCriarPedido.this, android.R.layout.simple_spinner_item, c);
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClientes.setAdapter(adapterSpinner);
            }
        }

    }
}