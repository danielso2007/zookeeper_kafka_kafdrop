package br.com.ingressos.model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class Venda {

    private Long operacao;
    private String cliente;
    private Integer quantidadeIngressos;
    private BigDecimal valorTotal;
    private String status;

    public Venda() {
        // Construtor padrão.
    }

    public Venda(Long operacao, String cliente, Integer quantidadeIngressos, BigDecimal valorTotal, String status) {
        this.operacao = operacao;
        this.cliente = cliente;
        this.quantidadeIngressos = quantidadeIngressos;
        this.valorTotal = valorTotal;
        this.status = status;
    }

    public Long getOperacao() {
        return operacao;
    }

    public void setOperacao(Long operacao) {
        this.operacao = operacao;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Integer getQuantidadeIngressos() {
        return quantidadeIngressos;
    }

    public void setQuantidadeIngressos(Integer quantidadeIngressos) {
        this.quantidadeIngressos = quantidadeIngressos;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cliente == null) ? 0 : cliente.hashCode());
        result = prime * result + ((valorTotal == null) ? 0 : valorTotal.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Venda other = (Venda) obj;
        if (cliente == null) {
            if (other.cliente != null)
                return false;
        } else if (!cliente.equals(other.cliente))
            return false;
        if (valorTotal == null) {
            if (other.valorTotal != null)
                return false;
        } else if (!valorTotal.equals(other.valorTotal))
            return false;
        return true;
    }

    @Override
    public String toString() {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "br"));
        return "Venda [operacao=" + operacao + ", cliente=" + cliente + ", quantidadeIngressos=" + quantidadeIngressos
                + ", valorTotal=" + format.format(valorTotal) + ", status=" + status + "]";
    }

}
