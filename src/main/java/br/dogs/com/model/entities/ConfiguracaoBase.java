package br.dogs.com.model.entities;

public class ConfiguracaoBase extends BaseEntity {

	private double valorTicket;
	private double taxaPlataforma;
	private double valorMinimoDeposito;
	private String tokenGateway;
	private int tempoPasseio;

	public double getValorTicket() {
		return valorTicket;
	}

	public void setValorTicket(double valorTicket) {
		this.valorTicket = valorTicket;
	}

	public double getTaxaPlataforma() {
		return taxaPlataforma;
	}

	public void setTaxaPlataforma(double taxaPlataforma) {
		this.taxaPlataforma = taxaPlataforma;
	}

	public double getValorMinimoDeposito() {
		return valorMinimoDeposito;
	}

	public void setValorMinimoDeposito(double valorMinimoDeposito) {
		this.valorMinimoDeposito = valorMinimoDeposito;
	}

	public String getTokenGateway() {
		return tokenGateway;
	}

	public void setTokenGateway(String tokenGateway) {
		this.tokenGateway = tokenGateway;
	}

	public int getTempoPasseio() {
		return tempoPasseio;
	}

	public void setTempoPasseio(int tempoPasseio) {
		this.tempoPasseio = tempoPasseio;
	}

}
