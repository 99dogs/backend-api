package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.entities.ConfiguracaoBase;

public interface ConfiguracaoBaseService {
	
	public List<ConfiguracaoBase> buscarTodos();
	
	public ConfiguracaoBase cadastrar(ConfiguracaoBase entity);
	
	public ConfiguracaoBase buscarPorId(Long id);
	
	public boolean alterar(ConfiguracaoBase entity);
	
	public boolean deletarPorId(Long id);
	
}
