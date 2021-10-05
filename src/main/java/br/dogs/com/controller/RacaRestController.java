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
import br.dogs.com.model.entities.Raca;
import br.dogs.com.service.RacaService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/raca")
public class RacaRestController {
	
	@Autowired
	private RacaService racaService;
	
	@ApiOperation("Retorna a lista de raças cadastradas na plataforma.")
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodos(){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			List<Raca> racas = racaService.findAll();
			
			return ResponseEntity.ok(racas);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para cadastrar uma nova raça na plataforma.")
	@RequestMapping(method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> cadastrar(@RequestBody Raca raca){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			if(raca.getNome() == null) {
				throw new Exception("Nome da raça não foi encontrado no corpo da requisição.");
			}
			
			if(raca.getNome().isEmpty() || raca.getNome().isBlank() ) {
				throw new Exception("O nome da raça não pode ser vazio.");
			}
			
			Raca novaRaca = racaService.create(raca);
			
			if(novaRaca.getId() == null || novaRaca.getId() == 0) {
				throw new Exception("Ocorreu algum problema ao cadastrar a raça.");
			}
						
			return ResponseEntity.ok(novaRaca);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para alterar as informações de uma raça específica.")
	@RequestMapping(value="/{id}", method = RequestMethod.PUT, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> alterar(@PathVariable Long id, @RequestBody Raca raca){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			if(raca.getNome() == null) {
				throw new Exception("Nome da raça não foi encontrado no corpo da requisição.");
			}
			
			if(raca.getNome().isEmpty() || raca.getNome().isBlank() ) {
				throw new Exception("O nome da raça não pode ser vazio.");
			}
			
			raca.setId(id);
			boolean racaAlterada = racaService.update(raca);
			
			if(racaAlterada == false) {
				throw new Exception("Ocorreu algum problema ao alterar a raça.");
			}
									
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para excluir uma raça específica.")
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> deletar(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			Raca racaExistente = racaService.findById(id);
			if(racaExistente == null || racaExistente.getId() == null || racaExistente.getId() == 0) {
				throw new Exception("A raça " + id + " não foi encontrada.");
			}
			
			boolean racaDeletada = racaService.deleteById(id);
			
			if(racaDeletada == false) {
				throw new Exception("Ocorreu algum problema ao deletar a raça.");
			}
						
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
}
