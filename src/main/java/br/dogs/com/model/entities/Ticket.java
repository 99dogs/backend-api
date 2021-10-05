package br.dogs.com.model.entities;

public class Ticket extends BaseEntity {

	private int quantidade;
	private double unitario;
	private double total;
	private boolean pendente;
	private boolean cancelado;
	private boolean pago;
	private String faturaId;
	private String faturaUrl;
	private Long formaDePagamentoId;
	private Long usuarioId;
	private FormaDePagamento formaDePagamento;
	private String cpfPagador;

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public double getUnitario() {
		return unitario;
	}

	public void setUnitario(double unitario) {
		this.unitario = unitario;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public boolean isPendente() {
		return pendente;
	}

	public void setPendente(boolean pendente) {
		this.pendente = pendente;
	}

	public boolean isCancelado() {
		return cancelado;
	}

	public void setCancelado(boolean cancelado) {
		this.cancelado = cancelado;
	}

	public boolean isPago() {
		return pago;
	}

	public void setPago(boolean pago) {
		this.pago = pago;
	}

	public String getFaturaId() {
		return faturaId;
	}

	public void setFaturaId(String faturaId) {
		this.faturaId = faturaId;
	}

	public String getFaturaUrl() {
		return faturaUrl;
	}

	public void setFaturaUrl(String faturaUrl) {
		this.faturaUrl = faturaUrl;
	}

	public Long getFormaDePagamentoId() {
		return formaDePagamentoId;
	}

	public void setFormaDePagamentoId(Long formaDePagamentoId) {
		this.formaDePagamentoId = formaDePagamentoId;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public FormaDePagamento getFormaDePagamento() {
		return formaDePagamento;
	}

	public void setFormaDePagamento(FormaDePagamento formaDePagamento) {
		this.formaDePagamento = formaDePagamento;
	}

	public String getCpfPagador() {
		return cpfPagador;
	}

	public void setCpfPagador(String cpfPagador) {
		this.cpfPagador = cpfPagador;
	}

}
