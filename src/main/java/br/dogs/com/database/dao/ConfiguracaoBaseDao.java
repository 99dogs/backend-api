package br.dogs.com.database.dao;

import java.util.List;

import br.dogs.com.model.entities.ConfiguracaoBase;

public interface ConfiguracaoBaseDao {
	
	public List<ConfiguracaoBase> buscarTodos();
	
	public ConfiguracaoBase cadastrar(ConfiguracaoBase entity);
	
	public ConfiguracaoBase buscarPorId(Long id);
	
	public boolean alterar(ConfiguracaoBase entity);
	
	public boolean deletarPorId(Long id);
	
}
