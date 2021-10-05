package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.PorteDao;
import br.dogs.com.model.entities.Porte;
import br.dogs.com.service.PorteService;

@Service
public class PorteServiceImpl implements PorteService {
	
	@Autowired
	private PorteDao porteDao;
	
	@Override
	public List<Porte> buscarTodos() {
		return porteDao.buscarTodos();
	}
	
	@Override
	public Porte buscarPorId(Long id) {
		return porteDao.buscarPorId(id);
	}
	
	@Override
	public Porte cadastrar(Porte entity) {
		return porteDao.cadastrar(entity);
	}

	@Override
	public boolean alterar(Porte entity) {
		return porteDao.alterar(entity);
	}

	@Override
	public boolean deletarPorId(Long id) {
		return porteDao.deletarPorId(id);
	}

}
