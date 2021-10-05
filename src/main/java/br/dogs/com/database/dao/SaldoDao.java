package br.dogs.com.database.dao;

import java.util.List;

import br.dogs.com.model.entities.Saldo;

public interface SaldoDao {
	
	public boolean creditarSaldo(Saldo saldo);
	
	public double retornaUltimoSaldo(Long usuarioId);
	
	public List<Saldo> buscarTodos(Long usuarioId);
	
	public List<Saldo> buscarPorDeposito(Long depositoId);
	
}
