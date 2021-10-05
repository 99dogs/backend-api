package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.ReclamacaoSugestaoDao;
import br.dogs.com.model.entities.ReclamacaoSugestao;
import br.dogs.com.service.ReclamacaoSugestaoService;

@Service
public class ReclamacaoSugestaoServiceImpl implements ReclamacaoSugestaoService {
	
	@Autowired
	private ReclamacaoSugestaoDao reclamacaoSugestaoDao;
	
	@Override
	public List<ReclamacaoSugestao> buscarTodos(Long usuarioId) {
		return reclamacaoSugestaoDao.bsucarTodos(usuarioId);
	}

	@Override
	public ReclamacaoSugestao cadastrar(ReclamacaoSugestao entity) {
		return reclamacaoSugestaoDao.cadastrar(entity);
	}

	@Override
	public ReclamacaoSugestao buscarPorId(Long id) {
		return reclamacaoSugestaoDao.buscarPorId(id);
	}

	@Override
	public boolean deletarPorId(Long id) {
		return reclamacaoSugestaoDao.deletarPorId(id);
	}

}
