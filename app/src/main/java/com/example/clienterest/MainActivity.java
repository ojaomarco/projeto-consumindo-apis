package com.example.clienterest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.GsonBuilder;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}