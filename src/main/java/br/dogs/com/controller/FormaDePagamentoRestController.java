package br.dogs.com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.entities.FormaDePagamento;
import br.dogs.com.service.FormaDePagamentoService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/forma-de-pagamento")
public class FormaDePagamentoRestController {
	
	@Autowired
	private FormaDePagamentoService formaDePagamentoService;
	
	@ApiOperation("Retorna a lista de formas de pagamento suportados na plataforma.")
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodos(){
		
		ResponseData responseData = new ResponseData();
		
		try {
						
			List<FormaDePagamento> formasDePagamento = formaDePagamentoService.buscarTodos();
						
			return ResponseEntity.ok(formasDePagamento);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
			
	}
	
	@ApiOperation("Endpoint para buscar uma forma de pagamento específica.")
	@RequestMapping(value="/{id}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarPorId(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			FormaDePagamento formaPagamento = formaDePagamentoService.buscarPorId(id);
			
			if(formaPagamento == null || formaPagamento.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Forma de pagamento " + id + " não encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
									
			return ResponseEntity.ok(formaPagamento);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
	@ApiOperation("Endpoint para cadastrar uma nova de pagamento na plataforma.")
	@RequestMapping(method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> cadastrar(@RequestBody FormaDePagamento formaDePagamento){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			if(formaDePagamento.getNome() == null) {
				throw new Exception("Nome não pode ser vazio.");
			}
			
			if(formaDePagamento.getNome().isEmpty() || formaDePagamento.getNome().isBlank()) {
				throw new Exception("Campo nome não pode ser vazio.");
			}
			
			if(formaDePagamento.getTipo() == null || formaDePagamento.getTipo().isEmpty()) {
				throw new Exception("Campo tipo não pode ser vazio.");
			}
			
			if(formaDePagamento.isAtivo() != true && formaDePagamento.isAtivo() != false) {
				throw new Exception("Campo ativo deve ser true ou false.");
			}
			
			FormaDePagamento novaForma = formaDePagamentoService.cadastrar(formaDePagamento);
			
			if(novaForma.getId() == null) {
				throw new Exception("Ocorreu algum problema ao cadastrar a forma de pagamento.");
			}
						
			return ResponseEntity.ok(novaForma);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}	
		
	}
	
	@ApiOperation("Endpoint para alterar as informações de uma forma de pagamento específica.")
	@RequestMapping(value="/{id}", method = RequestMethod.PUT, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> alterar(@PathVariable Long id, @RequestBody FormaDePagamento formaDePagamento){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			FormaDePagamento formaExistente = formaDePagamentoService.buscarPorId(id);
			if(formaExistente == null || formaExistente.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("A forma de pagamento " + id + " não foi encontrada.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(formaDePagamento.getNome() == null) {
				throw new Exception("Nome não pode ser vazio.");
			}
			
			if(formaDePagamento.getNome().isEmpty() || formaDePagamento.getNome().isBlank()) {
				throw new Exception("Campo nome não pode ser vazio.");
			}
			
			if(formaDePagamento.getTipo() == null || formaDePagamento.getTipo().isEmpty()) {
				throw new Exception("Campo tipo não pode ser vazio.");
			}
			
			if(formaDePagamento.isAtivo() != true && formaDePagamento.isAtivo() != false) {
				throw new Exception("Campo ativo deve ser true ou false.");
			}
			
			formaDePagamento.setId(id);
			boolean formaAlterada = formaDePagamentoService.alterar(formaDePagamento);
			
			if(formaAlterada == false) {
				throw new Exception("Ocorreu algum problema ao alterar a forma de pagamento.");
			}
									
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}	
		
	}
	
	@ApiOperation("Endpoint para excluir uma forma de pagamento específica.")
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> deletar(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			FormaDePagamento formaExistente = formaDePagamentoService.buscarPorId(id);
			if(formaExistente == null || formaExistente.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("A forma de pagamento " + id + " não foi encontrada.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			boolean formaDeletada = formaDePagamentoService.deletarPorId(id);
			
			if(formaDeletada == false) {
				throw new Exception("Ocorreu algum problema ao deletar a forma de pagamento.");
			}
			
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
}
