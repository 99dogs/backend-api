package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.entities.Deposito;

public interface DepositoService {
	
	public Deposito solicitar(Long dogwalkerId);
	
	public Deposito buscarPorId(Long id);
	
	public List<Deposito> buscarTodos(Long usuarioId);
	
}
