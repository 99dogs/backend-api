package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.DepositoDao;
import br.dogs.com.model.entities.Deposito;
import br.dogs.com.service.DepositoService;

@Service
public class DepositoServiceImpl implements DepositoService {
	
	@Autowired
	private DepositoDao depositoDao;
	
	@Override
	public Deposito solicitar(Long dogwalkerId) {
		return depositoDao.solicitar(dogwalkerId);
	}

	@Override
	public Deposito buscarPorId(Long id) {
		return depositoDao.buscarPorId(id);
	}

	@Override
	public List<Deposito> buscarTodos(Long usuarioId) {
		return depositoDao.buscarTodos(usuarioId);
	}

}
