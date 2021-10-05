package br.dogs.com.database.dao;

import java.util.List;

import br.dogs.com.model.entities.Estado;

public interface EstadoDao {
	
	public List<Estado> buscarTodos();
	
	public Estado buscarPorId(Long id);
	
}
