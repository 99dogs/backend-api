package br.dogs.com.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.TicketDao;
import br.dogs.com.database.dao.UsuarioDao;
import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.dto.ResponseFatura;
import br.dogs.com.model.dto.TicketFatura;
import br.dogs.com.model.entities.Ticket;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.TicketService;
import br.dogs.com.service.payment.Safe2PayPaymentService;

@Service
public class TicketServiceImpl implements TicketService {
	
	@Autowired
	private TicketDao ticketDao;
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	@Autowired
	private Safe2PayPaymentService safe2PayPaymentService;
	
	@Override
	public List<Ticket> buscarTodos(Long usuarioId) {
		return ticketDao.buscarTodos(usuarioId);
	}

	@Override
	public Ticket buscarPorId(Long id) {
		return ticketDao.buscarPorId(id);
	}

	@Override
	public Ticket cadastrar(Ticket entity) {
		return ticketDao.cadastrar(entity);
	}

	@Override
	public boolean deletarPorId(Long id) {
		return ticketDao.deletarPorId(id);
	}

	@Override
	public ResponseData faturar(TicketFatura entity) {
		
		ResponseData responseData = new ResponseData();
		
		Ticket ticket = ticketDao.buscarPorId(entity.getTicketId());
		Usuario tutor = usuarioDao.buscarPorId(ticket.getUsuarioId());
		
		if(ticket.getFaturaId() != null && ticket.getFaturaUrl() != null) {
			
			responseData.setTemErro(true);
			responseData.setMensagem("O ticket já foi faturado.");
			return responseData;
			
		}
		
		ResponseFatura response = safe2PayPaymentService.gerarFatura(entity,ticket, tutor);
		
		if(response.isTemErro() == false) {
			
			ticket.setFaturaId(response.getId());
			ticket.setFaturaUrl(response.getUrl());
			ticket.setPendente(true);
			
			boolean ticketAlterado = ticketDao.alterar(ticket);
			
			if(ticketAlterado) {
				
				ticket = ticketDao.buscarPorId(entity.getTicketId());
				
				Map<String, Object> result = new HashMap<>();
				
				result.put("Ticket", ticket);
				
				responseData.setMensagem("Requisição efetuada com sucesso.");
				responseData.setConteudo(result);
				
			}else {
				
				safe2PayPaymentService.cancelarFatura(ticket);
				
				responseData.setTemErro(true);
				responseData.setMensagem("Ocorreu algum problema ao atualizar as informações do ticket.");
			}
			
		}else {
			
			responseData.setTemErro(true);
			responseData.setMensagem(response.getMensagem());
			responseData.setConteudo(response.getConteudo());
			
		}
		
		return responseData;
		
	}

	@Override
	public Ticket buscarPorFaturaId(String faturaId) {
		return ticketDao.buscarPorFaturaId(faturaId);
	}

	@Override
	public boolean setarComoPendente(Long id) {
		return ticketDao.setarComoPendente(id);
	}

	@Override
	public boolean setarComoPago(Long id) {
		return ticketDao.setarComoPago(id);
	}

	@Override
	public boolean setarComoCancelado(Long id) {
		return ticketDao.setarComoCancelado(id);
	}

	@Override
	public boolean creditarComprador(Long id) {
		return ticketDao.creditarComprador(id);
	}

	@Override
	public boolean debitarComprador(Long tutorId) {
		return ticketDao.debitarComprador(tutorId);
	}

}
