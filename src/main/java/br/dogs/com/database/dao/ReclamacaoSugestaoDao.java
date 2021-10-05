package br.dogs.com.database.dao;

import java.util.List;

import br.dogs.com.model.entities.ReclamacaoSugestao;

public interface ReclamacaoSugestaoDao {
	
	public List<ReclamacaoSugestao> bsucarTodos(Long usuarioId);
	
	public ReclamacaoSugestao cadastrar(ReclamacaoSugestao entity);
	
	public ReclamacaoSugestao buscarPorId(Long id);
	
	public boolean deletarPorId(Long id);
	
}
