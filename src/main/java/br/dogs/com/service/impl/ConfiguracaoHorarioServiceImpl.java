package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.ConfiguracaoHorarioDao;
import br.dogs.com.model.entities.ConfiguracaoHorario;
import br.dogs.com.service.ConfiguracaoHorarioService;

@Service
public class ConfiguracaoHorarioServiceImpl implements ConfiguracaoHorarioService {
	
	@Autowired
	private ConfiguracaoHorarioDao configuracaoHorarioDao;
	
	@Override
	public List<ConfiguracaoHorario> buscarTodos(Long usuarioId) {
		return configuracaoHorarioDao.buscarTodos(usuarioId);
	}

	@Override
	public ConfiguracaoHorario buscarPorId(Long id) {
		return configuracaoHorarioDao.buscarPorId(id);
	}

	@Override
	public ConfiguracaoHorario cadastrar(ConfiguracaoHorario entity) {
		return configuracaoHorarioDao.cadastrar(entity);
	}

	@Override
	public ConfiguracaoHorario buscarPorDiaSemana(int diaSemana, Long usuarioId) {
		return configuracaoHorarioDao.buscarPorDiaSemana(diaSemana, usuarioId);
	}

	@Override
	public boolean alterar(ConfiguracaoHorario entity) {
		return configuracaoHorarioDao.alterar(entity);
	}

	@Override
	public boolean deletarPorId(Long id) {
		return configuracaoHorarioDao.deletarPorId(id);
	}

}
