package br.dogs.com.database.dao;

import java.util.List;

import br.dogs.com.model.entities.Ticket;

public interface TicketDao {
	
	public List<Ticket> buscarTodos(Long usuarioId);
	
	public Ticket buscarPorId(Long id);
	
	public Ticket cadastrar(Ticket entity);
	
	public boolean deletarPorId(Long id);
	
	public boolean alterar(Ticket entity);
	
	public Ticket buscarPorFaturaId(String faturaId);
	
	public boolean setarComoPendente(Long id);
	
	public boolean setarComoPago(Long id);
	
	public boolean setarComoCancelado(Long id);
	
	public boolean creditarComprador(Long id);
	
	public boolean debitarComprador(Long passeioId);
	
}
