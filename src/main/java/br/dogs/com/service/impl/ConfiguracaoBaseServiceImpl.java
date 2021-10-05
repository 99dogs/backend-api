package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.ConfiguracaoBaseDao;
import br.dogs.com.model.entities.ConfiguracaoBase;
import br.dogs.com.service.ConfiguracaoBaseService;

@Service
public class ConfiguracaoBaseServiceImpl implements ConfiguracaoBaseService {
	
	@Autowired
	private ConfiguracaoBaseDao configuracaoBaseDao;
	
	@Override
	public List<ConfiguracaoBase> buscarTodos() {
		return configuracaoBaseDao.buscarTodos();
	}

	@Override
	public ConfiguracaoBase cadastrar(ConfiguracaoBase entity) {
		return configuracaoBaseDao.cadastrar(entity);
	}

	@Override
	public ConfiguracaoBase buscarPorId(Long id) {
		return configuracaoBaseDao.buscarPorId(id);
	}

	@Override
	public boolean alterar(ConfiguracaoBase entity) {
		return configuracaoBaseDao.alterar(entity);
	}

	@Override
	public boolean deletarPorId(Long id) {
		return configuracaoBaseDao.deletarPorId(id);
	}

}
