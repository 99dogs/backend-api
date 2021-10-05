package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.dto.PasseioLatLong;
import br.dogs.com.model.entities.Passeio;

public interface PasseioService {
	
	public Passeio solicitar(Passeio passeio);
	
	public Passeio buscarPorId(Long id);
	
	public boolean alterarStatus(Long id, String status);
	
	public List<Passeio> buscarTodos(Long usuarioId);
	
	public boolean registrarLatLong(PasseioLatLong entity);
	
	public PasseioLatLong posicaoAtual(Long id);
	
	public List<PasseioLatLong> posicaoCompleta(Long id);
	
}
