package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.PasseioDao;
import br.dogs.com.model.dto.PasseioLatLong;
import br.dogs.com.model.entities.Passeio;
import br.dogs.com.service.PasseioService;

@Service
public class PasseioServiceImpl implements PasseioService {
	
	@Autowired
	private PasseioDao passeioDao;
	
	@Override
	public Passeio solicitar(Passeio passeio) {
		return passeioDao.solicitar(passeio);
	}

	@Override
	public Passeio buscarPorId(Long id) {
		return passeioDao.buscarPorId(id);
	}

	@Override
	public boolean alterarStatus(Long id, String status) {
		return passeioDao.alterarStatus(id, status);
	}

	@Override
	public List<Passeio> buscarTodos(Long usuarioId) {
		return passeioDao.buscarTodos(usuarioId);
	}

	@Override
	public boolean registrarLatLong(PasseioLatLong entity) {
		return passeioDao.registrarLatLong(entity);
	}

	@Override
	public PasseioLatLong posicaoAtual(Long id) {
		return passeioDao.posicaoAtual(id);
	}

	@Override
	public List<PasseioLatLong> posicaoCompleta(Long id) {
		return passeioDao.posicaoCompleta(id);
	}
	
}
