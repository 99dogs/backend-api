package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.entities.Cachorro;

public interface CachorroService {
	
	public Cachorro cadastrar(Cachorro entity);
	
	public Cachorro buscarPorId(Long id);
	
	public List<Cachorro> buscarTodos(Long usuarioId);
	
	public boolean alterar(Cachorro entity);
	
	public boolean deletarPorId(Long id);
	
}
