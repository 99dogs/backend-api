package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.entities.Avaliacao;

public interface AvaliacaoService {
	
	public Avaliacao cadastrar(Avaliacao entity);
	
	public Avaliacao buscarPorId(Long id);
	
	public List<Avaliacao> buscarTodosPorDogwalker(Long id);
	
	public boolean deletar(Long id);
	
	public boolean atualizarMediaAvaliacao(Long dogwalkerId);
	
	public Avaliacao buscarPorPasseioId(Long id);
	
}
