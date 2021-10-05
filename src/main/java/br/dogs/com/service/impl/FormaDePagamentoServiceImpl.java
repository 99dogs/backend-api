package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.FormaDePagamentoDao;
import br.dogs.com.model.entities.FormaDePagamento;
import br.dogs.com.service.FormaDePagamentoService;

@Service
public class FormaDePagamentoServiceImpl implements FormaDePagamentoService {
	
	@Autowired
	private FormaDePagamentoDao formaDePagamentoDao;
	
	@Override
	public List<FormaDePagamento> buscarTodos() {
		return formaDePagamentoDao.buscarTodos();
	}

	@Override
	public FormaDePagamento buscarPorId(Long id) {
		return formaDePagamentoDao.buscarPorId(id);
	}

	@Override
	public FormaDePagamento cadastrar(FormaDePagamento entity) {
		return formaDePagamentoDao.cadastrar(entity);
	}

	@Override
	public boolean alterar(FormaDePagamento entity) {
		return formaDePagamentoDao.alterar(entity);
	}

	@Override
	public boolean deletarPorId(Long id) {
		return formaDePagamentoDao.deletarPorId(id);
	}

}
