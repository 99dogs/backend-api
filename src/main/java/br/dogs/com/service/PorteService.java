package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.entities.Porte;

public interface PorteService {
	
	public List<Porte> buscarTodos();
	
	public Porte buscarPorId(Long id);
	
	public Porte cadastrar(Porte entity);
	
	public boolean alterar(Porte entity);
	
	public boolean deletarPorId(Long id);
	
}
