package br.dogs.com.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.HorarioDao;
import br.dogs.com.service.HorarioService;

@Service
public class HorarioServiceImpl implements HorarioService {
	
	@Autowired
	private HorarioDao horarioDao;
	
	@Override
	public boolean verificarDisponibilidade(String datahora, Long usuarioId) {
		return horarioDao.verificarDisponibilidade(datahora, usuarioId);
	}
	
}
