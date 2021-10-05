package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.entities.FormaDePagamento;

public interface FormaDePagamentoService {
	
	public List<FormaDePagamento> buscarTodos();
	
	public FormaDePagamento buscarPorId(Long id);
	
	public FormaDePagamento cadastrar(FormaDePagamento entity);
	
	public boolean alterar(FormaDePagamento entity);
	
	public boolean deletarPorId(Long id);
	
}
