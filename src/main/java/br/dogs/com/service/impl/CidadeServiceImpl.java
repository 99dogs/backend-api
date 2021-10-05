package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.CidadeDao;
import br.dogs.com.model.entities.Cidade;
import br.dogs.com.service.CidadeService;

@Service
public class CidadeServiceImpl implements CidadeService {
	
	@Autowired
	private CidadeDao cidadeDao;
	
	@Override
	public List<Cidade> buscarPorEstado(Long estadoId) {
		return cidadeDao.buscarPorEstado(estadoId);
	}

	@Override
	public Cidade buscarPorId(Long id) {
		return cidadeDao.buscarPorId(id);
	}

}
