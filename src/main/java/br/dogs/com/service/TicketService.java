package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.dto.TicketFatura;
import br.dogs.com.model.entities.Ticket;

public interface TicketService {
	
	public List<Ticket> buscarTodos(Long usuarioId);
	
	public Ticket buscarPorId(Long id);
	
	public Ticket cadastrar(Ticket entity);
	
	public boolean deletarPorId(Long id);
	
	public ResponseData faturar(TicketFatura entity);
	
	public Ticket buscarPorFaturaId(String faturaId);
	
	public boolean setarComoPendente(Long id);
	
	public boolean setarComoPago(Long id);
	
	public boolean setarComoCancelado(Long id);
	
	public boolean creditarComprador(Long id);
	
	public boolean debitarComprador(Long tutorId);
	
}
