package br.dogs.com.service;

import java.util.List;

public interface BaseCrudService<T> {
	
	List<T> findAll();
	
	List<T> findAll(Long usuarioId);
	
	T findById(Long id);
	
	T create(T entity);
	
	boolean updateById(Long id);
	
	boolean deleteById(Long id);
	
	
}
