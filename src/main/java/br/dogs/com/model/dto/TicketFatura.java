package br.dogs.com.model.dto;

public class TicketFatura {

	private Long ticketId;
	private String cpfPagador;

	public Long getTicketId() {
		return ticketId;
	}

	public void setTicketId(Long ticketId) {
		this.ticketId = ticketId;
	}

	public String getCpfPagador() {
		return cpfPagador;
	}

	public void setCpfPagador(String cpfPagador) {
		this.cpfPagador = cpfPagador;
	}

}
