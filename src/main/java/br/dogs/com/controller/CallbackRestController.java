package br.dogs.com.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.dto.safe2pay.callback.Callback;
import br.dogs.com.model.entities.Ticket;
import br.dogs.com.service.TicketService;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/callback")
public class CallbackRestController {

	Logger logger = LoggerFactory.getLogger(CallbackRestController.class);
	
	@Autowired
	private TicketService ticketService;
	
	@PostMapping("/safe2pay")
	public ResponseEntity<Object> callbackSafe2Pay(@RequestBody Callback callback) {
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			Ticket ticket = ticketService.buscarPorFaturaId(callback.Origin.SingleSaleHash);
			
			if(ticket != null && ticket.getId() != null) {
			
				// PENDENTE
				if(callback.TransactionStatus.Id == 1 && ticket.isPendente() == false) {
					ticketService.setarComoPendente(ticket.getId());
					
					return ResponseEntity.ok().build();
				}
				
				// AUTORIZADO
				if(callback.TransactionStatus.Id == 3 && ticket.isPago() == false) {
					ticketService.setarComoPago(ticket.getId());
					ticketService.creditarComprador(ticket.getId());
					
					return ResponseEntity.ok().build();
				}
				
				// EM CANCELAMENTO
				if(callback.TransactionStatus.Id == 12 && ticket.isCancelado() == false) {
					ticketService.setarComoCancelado(ticket.getId());
					
					return ResponseEntity.ok().build();
				}
				
				throw new Exception("Status não definido.");
				
			}else {
				throw new Exception("Ticket não encontrado.");
			}

		} catch (Exception e) {
			
			logger.error(e.getMessage());
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}

	}

}
