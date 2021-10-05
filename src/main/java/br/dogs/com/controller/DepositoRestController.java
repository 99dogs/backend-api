package br.dogs.com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.entities.Deposito;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.DepositoService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/deposito")
public class DepositoRestController {
	
	@Autowired
	private DepositoService depositoService;
	
	@ApiOperation("Retorna a lista de depositos do dogwalker.")
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodos(){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			List<Deposito> depositos = depositoService.buscarTodos(usuario.getId());
			
			return ResponseEntity.ok(depositos);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}	
	}
	
}
