package com.example.clienterest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verificaRede();
    }
    public void clientes(View view) {
        Intent it = new Intent(this, TelaClientesLista.class);
        startActivity(it);
    }
    public void pedidos(View view) {
        Intent it = new Intent(this, TelaPedidos.class);
        startActivity(it);
    }
    public void setores(View view) {
        Intent it = new Intent(this, TelaSetores.class);
        startActivity(it);
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
            Toast.makeText(getApplicationContext(), "Sem conexão com a Internet", Toast.LENGTH_LONG).show();
        }
    }
}