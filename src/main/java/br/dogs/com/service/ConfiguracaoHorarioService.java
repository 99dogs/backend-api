package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.entities.ConfiguracaoHorario;

public interface ConfiguracaoHorarioService {
	
	public List<ConfiguracaoHorario> buscarTodos(Long usuarioId);
	
	public ConfiguracaoHorario buscarPorId(Long id);
	
	public ConfiguracaoHorario cadastrar(ConfiguracaoHorario entity);
	
	public ConfiguracaoHorario buscarPorDiaSemana(int diaSemana, Long usuarioId);
	
	public boolean alterar(ConfiguracaoHorario entity);
	
	public boolean deletarPorId(Long id);
	
}
