package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.RacaDao;
import br.dogs.com.model.entities.Raca;
import br.dogs.com.service.RacaService;

@Service
public class RacaServiceImpl implements RacaService {
	
	@Autowired
	private RacaDao racaDao;
	
	@Override
	public List<Raca> findAll() {
		return racaDao.findAll();
	}

	@Override
	public Raca findById(Long id) {
		return racaDao.findById(id);
	}

	@Override
	public Raca create(Raca entity) {
		return racaDao.create(entity);
	}

	@Override
	public boolean deleteById(Long id) {
		return racaDao.deleteById(id);
	}

	@Override
	public boolean update(Raca entity) {
		return racaDao.update(entity);
	}

}
