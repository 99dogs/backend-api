package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.entities.Saldo;

public interface SaldoService {
	
	public boolean creditarSaldo(Long passeioId);
	
	public double retornaUltimoSaldo(Long usuarioId);
	
	public List<Saldo> buscarTodos(Long usuarioId);
	
	public List<Saldo> buscarPorDeposito(Long depositoId);
	
}
