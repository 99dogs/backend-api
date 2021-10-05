package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.entities.Estado;

public interface EstadoService {
	
	public List<Estado> buscarTodos();
	
	public Estado buscarPorId(Long id);
	
}
