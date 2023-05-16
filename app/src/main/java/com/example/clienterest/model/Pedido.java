package com.example.clienterest.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Pedido implements Serializable {
    private Long id;
    private Long idVendedor;
    private String cpfCliente;
    private Date data;
    private Double percentualDesconto;
    List<ItemPedido> itens;

    @Override
    public String toString() {
        return  "id: " + id +
                " idVendedor: " + idVendedor +
                " cpfCliente:" + cpfCliente + '\'' +
                ", itens=" + itens +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Long idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getCpfCliente() {
        return cpfCliente;
    }

    public void setCpfCliente(String cpfCliente) {
        this.cpfCliente = cpfCliente;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Double getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(Double percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }
}

