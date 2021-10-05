package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.AvaliacaoDao;
import br.dogs.com.model.entities.Avaliacao;
import br.dogs.com.service.AvaliacaoService;

@Service
public class AvaliacaoServiceImpl implements AvaliacaoService {

	@Autowired
	private AvaliacaoDao avaliacaoDao;

	@Override
	public Avaliacao cadastrar(Avaliacao entity) {
		return avaliacaoDao.cadastrar(entity);
	}

	@Override
	public Avaliacao buscarPorId(Long id) {
		return avaliacaoDao.buscarPorId(id);
	}

	@Override
	public List<Avaliacao> buscarTodosPorDogwalker(Long id) {
		return avaliacaoDao.buscarTodosPorDogwalker(id);
	}

	@Override
	public boolean deletar(Long id) {
		return avaliacaoDao.deletar(id);
	}

	@Override
	public boolean atualizarMediaAvaliacao(Long dogwalkerId) {
		return avaliacaoDao.atualizarMediaAvaliacao(dogwalkerId);
	}

	@Override
	public Avaliacao buscarPorPasseioId(Long id) {
		return avaliacaoDao.buscarPorPasseioId(id);
	}

}
