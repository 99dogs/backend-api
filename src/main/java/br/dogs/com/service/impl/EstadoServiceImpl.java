package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.EstadoDao;
import br.dogs.com.model.entities.Estado;
import br.dogs.com.service.EstadoService;

@Service
public class EstadoServiceImpl implements EstadoService {
	
	@Autowired
	private EstadoDao estadoDao;
	
	@Override
	public List<Estado> buscarTodos() {
		return estadoDao.buscarTodos();
	}

	@Override
	public Estado buscarPorId(Long id) {
		return estadoDao.buscarPorId(id);
	}

}
