package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.QualificacaoDao;
import br.dogs.com.model.entities.Qualificacao;
import br.dogs.com.service.QualificacaoService;

@Service
public class QualificaoServiceImpl implements QualificacaoService {
	
	@Autowired
	private QualificacaoDao qualificacaoDao;
	
	@Override
	public List<Qualificacao> buscarTodos(Long usuarioId) {
		return qualificacaoDao.buscarTodos(usuarioId);
	}

	@Override
	public Qualificacao cadastrar(Qualificacao entity) {
		return qualificacaoDao.cadastrar(entity);
	}

	@Override
	public Qualificacao buscarPorId(Long id) {
		return qualificacaoDao.buscarPorId(id);
	}

	@Override
	public boolean alterar(Qualificacao entity) {
		return qualificacaoDao.alterar(entity);
	}

	@Override
	public boolean deletarPorId(Long id) {
		return qualificacaoDao.deletarPorId(id);
	}

}
