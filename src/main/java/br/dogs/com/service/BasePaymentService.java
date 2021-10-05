package br.dogs.com.service;

import br.dogs.com.model.dto.ResponseFatura;
import br.dogs.com.model.dto.TicketFatura;
import br.dogs.com.model.entities.Ticket;
import br.dogs.com.model.entities.Usuario;

public interface BasePaymentService {
	
	public ResponseFatura gerarFatura(TicketFatura entity, Ticket ticket, Usuario usuario);
	
	public boolean cancelarFatura(Ticket ticket);
	
}
