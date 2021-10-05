package br.dogs.com.database.dao;

public interface HorarioDao {
	
	public boolean verificarDisponibilidade(String datahora, Long usuarioId);
	
}
