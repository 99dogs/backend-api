package br.dogs.com.database.dao;

import java.util.List;

import br.dogs.com.model.entities.Deposito;

public interface DepositoDao {
	
	public Deposito solicitar(Long dogwalkerId);
	
	public Deposito buscarPorId(Long id);
	
	public List<Deposito> buscarTodos(Long usuarioId);
	
}
