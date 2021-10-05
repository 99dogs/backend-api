package br.dogs.com.database.dao;

import java.util.List;

import br.dogs.com.model.entities.Cidade;

public interface CidadeDao {
	
	public List<Cidade> buscarPorEstado(Long estadoId);
	
	public Cidade buscarPorId(Long id);
	
}
