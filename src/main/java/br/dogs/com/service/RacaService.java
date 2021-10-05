package br.dogs.com.service;

import java.util.List;

import br.dogs.com.model.entities.Raca;

public interface RacaService {
	
	public List<Raca> findAll();

	public Raca findById(Long id);

	public Raca create(Raca entity);

	public boolean update(Raca entity);

	public boolean deleteById(Long id);
	
}
