package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.entities.Qualificacao;

public interface QualificacaoService {
	
	public List<Qualificacao> buscarTodos(Long usuarioId);
	
	public Qualificacao cadastrar(Qualificacao entity);
	
	public Qualificacao buscarPorId(Long id);
	
	public boolean alterar(Qualificacao entity);
	
	public boolean deletarPorId(Long id);
	
}
