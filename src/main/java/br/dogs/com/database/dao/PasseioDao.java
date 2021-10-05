package br.dogs.com.database.dao;

import java.util.List;

import br.dogs.com.model.dto.PasseioLatLong;
import br.dogs.com.model.entities.Passeio;

public interface PasseioDao {
	
	public Passeio solicitar(Passeio entity);
	
	public Passeio buscarPorId(Long id);
	
	public boolean alterarStatus(Long id, String status);
	
	public List<Passeio> buscarTodos(Long usuarioId);
	
	public boolean registrarLatLong(PasseioLatLong entity);
	
	public PasseioLatLong posicaoAtual(Long id);
	
	public List<PasseioLatLong> posicaoCompleta(Long id);
	
}
