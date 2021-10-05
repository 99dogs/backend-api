package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.entities.Cidade;

public interface CidadeService {
	
	public List<Cidade> buscarPorEstado(Long estadoId);
	
	public Cidade buscarPorId(Long id);
	
}
