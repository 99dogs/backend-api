package br.dogs.com.database.dao;

import java.util.List;

import br.dogs.com.model.entities.FormaDePagamento;

public interface FormaDePagamentoDao {
	
	public List<FormaDePagamento> buscarTodos();
	
	public FormaDePagamento buscarPorId(Long id);
	
	public FormaDePagamento cadastrar(FormaDePagamento entity);
	
	public boolean alterar(FormaDePagamento entity);
	
	public boolean deletarPorId(Long id);
	
}
