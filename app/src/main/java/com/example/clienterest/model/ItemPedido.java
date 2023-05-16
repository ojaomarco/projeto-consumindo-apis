package com.example.clienterest.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ItemPedido implements Serializable {
    private Long id;
    private Long idPedido;
    private Long idProduto;
    private Double quantidade;
    private Double precoUnitario;

    @Override
    public String toString() {
        return String.format("idProd: %s, Valor R$%.2f, Qtd %.2f", idProduto.toString(), precoUnitario, quantidade);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}
