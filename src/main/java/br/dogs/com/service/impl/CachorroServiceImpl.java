package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.CachorroDao;
import br.dogs.com.model.entities.Cachorro;
import br.dogs.com.service.CachorroService;

@Service
public class CachorroServiceImpl implements CachorroService {
	
	@Autowired
	private CachorroDao cachorroDao;
	
	@Override
	public Cachorro cadastrar(Cachorro entity) {
		return cachorroDao.cadastrar(entity);
	}
	
	@Override
	public Cachorro buscarPorId(Long id) {
		return cachorroDao.buscarPorId(id);
	}
	
	@Override
	public List<Cachorro> buscarTodos(Long usuarioId) {
		return cachorroDao.buscarTodos(usuarioId);
	}

	@Override
	public boolean alterar(Cachorro entity) {
		return cachorroDao.alterar(entity);
	}

	@Override
	public boolean deletarPorId(Long id) {
		return cachorroDao.deletarPorId(id);
	}

}
