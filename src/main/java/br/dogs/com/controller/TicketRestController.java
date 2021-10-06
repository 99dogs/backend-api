package br.dogs.com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.dto.TicketFatura;
import br.dogs.com.model.entities.FormaDePagamento;
import br.dogs.com.model.entities.Ticket;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.FormaDePagamentoService;
import br.dogs.com.service.TicketService;
import br.dogs.com.service.payment.Safe2PayPaymentService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/ticket")
public class TicketRestController {
	
	@Autowired
	private TicketService ticketService;
	
	@Autowired
	private FormaDePagamentoService formaDePagamentoService;
	
	@Autowired
	private Safe2PayPaymentService safe2PayPaymentService;
	
	@ApiOperation("Retorna os tickets efetuados na plataforma.")
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodos(){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			Long usuarioId = (long) 0;
			Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			if(usuario.getTipo().equals("TUTOR")) {
				usuarioId = usuario.getId();
			}
			
			List<Ticket> tickets = ticketService.buscarTodos(usuarioId);
			
			return ResponseEntity.ok(tickets);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
			
	}
	
	@ApiOperation("Endpoint para buscar um ticket específico do tutor.")
	@RequestMapping(value="/{id}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarPorId(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			Ticket ticket = ticketService.buscarPorId(id);
			
			if(ticket == null || ticket.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Ticket " + id + " não encontrado");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(ticket.getUsuarioId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não tem permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
						
			return ResponseEntity.ok(ticket);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
	@ApiOperation("Endpoint para criar um ticket.")
	@RequestMapping(method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> cadastrar(@RequestBody Ticket ticket){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();

		try {
			
			if(ticket.getQuantidade() < 1) {
				throw new Exception("A quantidade deve ser maior ou igual à 1.");
			}
			
			if(ticket.getUnitario() < 1) {
				throw new Exception("O unitário deve ser maior ou igual à R$1,00.");
			}
			
			if(ticket.getTotal() < 1) {
				throw new Exception("O total deve ser maior ou igual à R$1,00.");
			}
			
			FormaDePagamento formaPagamento = formaDePagamentoService.buscarPorId(ticket.getFormaDePagamentoId());
			
			if(formaPagamento == null || formaPagamento.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Forma de pagamento " + ticket.getFormaDePagamentoId() + " não encontrada.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			ticket.setUsuarioId(usuario.getId());
			ticket.setPendente(true);
			Ticket novoTicket = ticketService.cadastrar(ticket);
			
			if(novoTicket == null || novoTicket.getId() == null || novoTicket.getId() == 0) {
				throw new Exception("Ocorreu algum problema ao cadastrar o ticket.");
			}
			
			return ResponseEntity.ok(novoTicket);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para excluir um ticket específico.")
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> deletar(@PathVariable Long id){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			Ticket ticketExistente = ticketService.buscarPorId(id);
			if(ticketExistente == null || ticketExistente.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("O ticket " + id + " não foi encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(ticketExistente.getUsuarioId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não tem permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
			
			boolean ticketDeletado = ticketService.deletarPorId(id);
			
			if(ticketDeletado == false) {
				throw new Exception("Ocorreu algum problema ao excluir o ticket.");
			}
			
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para faturar um ticket.")
	@RequestMapping(value="/faturar", method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> faturar(@RequestBody TicketFatura ticketFatura){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();

		try {
			
			if(ticketFatura.getTicketId() == null || ticketFatura.getTicketId() <= 0) {
				throw new Exception("É necessário informar o ticketId corretamente.");
			}
			
			if(ticketFatura.getCpfPagador() == null || ticketFatura.getCpfPagador().isEmpty()) {
				throw new Exception("É necesssário informar o CPF do pagador.");
			}
						
			Ticket ticketExistente = ticketService.buscarPorId(ticketFatura.getTicketId());
			
			if(ticketExistente.getUsuarioId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não possui permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
			
			responseData = ticketService.faturar(ticketFatura);
			
			if(responseData.isTemErro()) {
				throw new Exception(responseData.getMensagem());
			}
			
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para cancelar uma fatura.")
	@RequestMapping(value="/cancelar/{faturaId}", method = RequestMethod.DELETE, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> cancelarFatura(@PathVariable String faturaId){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			Ticket ticket = ticketService.buscarPorFaturaId(faturaId);
			
			boolean cancelado = safe2PayPaymentService.cancelarFatura(ticket);
			
			if(cancelado == false) {
				throw new Exception("Ocorreu algum problema para cancelar a fatura.");
			}

			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}
		
	}
	
	@ApiOperation("Endpoint para aprovar o pagamento do ticket")
	@RequestMapping(value="/aprovar-pagamento/{ticketId}", method = RequestMethod.PUT, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> aprovarPagamento(@PathVariable Long ticketId){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			Ticket ticket = ticketService.buscarPorId(ticketId);
			
			if(ticket.isPago() == true) {
				throw new Exception("Ticket já consta como pago.");
			}
			
			if(ticket == null || ticket.getId() == null || ticket.getId() == 0) {
				throw new Exception("Ticket informado não foi encontrado.");
			}
			
			boolean pago = ticketService.setarComoPago(ticketId);
			
			if(pago == false) {
				throw new Exception("Ocorreu algum problema ao marcar o ticket como pago.");
			}
			
			boolean creditado = ticketService.creditarComprador(ticketId);
			
			if(creditado == false) {
				ticketService.setarComoPendente(ticketId);
				throw new Exception("Ocorreu algum problema ao marcar o ticket como pago e creditar o comprador.");
			}
			
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}
		
	}
	
}
