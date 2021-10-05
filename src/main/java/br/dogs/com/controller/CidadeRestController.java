package br.dogs.com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.entities.Cidade;
import br.dogs.com.model.entities.Estado;
import br.dogs.com.service.CidadeService;
import br.dogs.com.service.EstadoService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/cidade")
public class CidadeRestController {
	
	@Autowired
	private CidadeService cidadeService;
	
	@Autowired
	private EstadoService estadoService;
	
	@ApiOperation("Retorna a lista de cidades por estado.")
	@RequestMapping(value="/{estadoId}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarPorEstado(@PathVariable Long estadoId){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			Estado estado = estadoService.buscarPorId(estadoId);
			
			if(estado == null || estado.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("O estado " + estadoId + " não encontrado");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(estado.isAtivo() == false) {
				throw new Exception("O estado encontra-se inativo.");
			}
			
			List<Cidade> cidades = cidadeService.buscarPorEstado(estadoId);
			
			return ResponseEntity.ok(cidades);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
			
	}
	
}