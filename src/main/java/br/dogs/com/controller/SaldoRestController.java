package br.dogs.com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.dogs.com.helper.TipoUsuario;
import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.entities.Deposito;
import br.dogs.com.model.entities.Saldo;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.DepositoService;
import br.dogs.com.service.SaldoService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/saldo")
public class SaldoRestController {
	
	@Autowired
	private SaldoService saldoService;
	
	@Autowired
	private DepositoService depositoService;
	
	@ApiOperation("Endpoint retorna a lista de saldo do dogwalker.")
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodos(){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			List<Saldo> saldos = saldoService.buscarTodos(usuario.getId());
						
			return ResponseEntity.ok(saldos);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
	@ApiOperation("Endpoint para solicitar o deposito de saldos em aberto")
	@RequestMapping(value = "/solicitar-deposito", method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> solicitarDeposito(){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			Deposito deposito = depositoService.solicitar(usuario.getId());
			
			if(deposito.getId() == null || deposito.getId() == 0) {
				throw new Exception("Ocorreu um problema ao solicitar o depósito."); 
			}
			
			return ResponseEntity.ok(deposito);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}
		
	}
	
	@ApiOperation("Endpoint retorna a lista de saldo do dogwalker.")
	@RequestMapping(value="/deposito/{id}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodosPorDeposito(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			Deposito deposito = depositoService.buscarPorId(id);
			
			if(deposito == null || deposito.getId() == 0) {
				throw new Exception("Depósito não foi encontrado."); 
			}
			
			if(usuario.getTipo().equals(TipoUsuario.DOGWALKER.toString())) {
				if(usuario.getId() != deposito.getUsuarioId()) {
					throw new Exception("Você não possui permissão para acessar esse objeto."); 
				}
			}
			
			List<Saldo> saldos = saldoService.buscarPorDeposito(id);
						
			return ResponseEntity.ok(saldos);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
}
