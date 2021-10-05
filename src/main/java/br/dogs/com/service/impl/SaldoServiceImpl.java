package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.SaldoDao;
import br.dogs.com.helper.PasseioStatus;
import br.dogs.com.model.entities.ConfiguracaoBase;
import br.dogs.com.model.entities.Passeio;
import br.dogs.com.model.entities.Saldo;
import br.dogs.com.service.ConfiguracaoBaseService;
import br.dogs.com.service.PasseioService;
import br.dogs.com.service.SaldoService;

@Service
public class SaldoServiceImpl implements SaldoService {
	
	@Autowired
	private ConfiguracaoBaseService configuracaoBaseService;
	
	@Autowired
	private SaldoDao saldoDao;
	
	@Autowired
	private PasseioService passeioService;
	
	@Override
	public boolean creditarSaldo(Long passeioId) {
		
		Passeio passeio = passeioService.buscarPorId(passeioId);
			
		if(passeio.getId() == null || passeio.getId() == 0) {
			return false;
		}
		
		if(!passeio.getStatus().equals(PasseioStatus.Finalizado.toString())) {
			return false;
		}
		
		Saldo saldo = new Saldo();
		
		List<ConfiguracaoBase> configs = configuracaoBaseService.buscarTodos();
		if(configs.size() == 0 ) {
			return false;
		}
		
		ConfiguracaoBase config = configs.get(0);
		
		double valorSaldo = 0;
		double descontoTaxa = 0;
		
		if(config.getTaxaPlataforma() > 0) {
			descontoTaxa = config.getValorTicket() * (config.getTaxaPlataforma() / 100);
		}
		
		if(config.getValorTicket() > 0) {
			valorSaldo = config.getValorTicket() - descontoTaxa;
		}
		
		saldo.setPasseioId(passeioId);
		saldo.setUsuarioId(passeio.getDogwalkerId());
		saldo.setUnitario(valorSaldo);
		
		return saldoDao.creditarSaldo(saldo);
		
	}

	@Override
	public double retornaUltimoSaldo(Long usuarioId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Saldo> buscarTodos(Long usuarioId) {
		return saldoDao.buscarTodos(usuarioId);
	}

	@Override
	public List<Saldo> buscarPorDeposito(Long depositoId) {
		return saldoDao.buscarPorDeposito(depositoId);
	}

}
