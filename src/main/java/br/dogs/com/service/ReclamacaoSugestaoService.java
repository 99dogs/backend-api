package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.entities.ReclamacaoSugestao;

public interface ReclamacaoSugestaoService {
	
	public List<ReclamacaoSugestao> buscarTodos(Long usuarioId);
	
	public ReclamacaoSugestao cadastrar(ReclamacaoSugestao entity);
	
	public ReclamacaoSugestao buscarPorId(Long id);
	
	public boolean deletarPorId(Long id);
	
}
