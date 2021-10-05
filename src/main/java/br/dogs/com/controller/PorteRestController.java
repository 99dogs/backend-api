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
import br.dogs.com.model.entities.Porte;
import br.dogs.com.service.PorteService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/porte")
public class PorteRestController {
	
	@Autowired
	private PorteService porteService;
	
	@ApiOperation("Retorna a lista de portes cadastrados na plataforma.")
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodos(){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			List<Porte> portes = porteService.buscarTodos();
			return ResponseEntity.ok(portes);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}	
	}
	
	@ApiOperation("Endpoint para cadastrar um novo porte.")
	@RequestMapping(method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> cadastrar(@RequestBody Porte porte){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			if(porte.getNome() == null) {
				throw new Exception("Nome do porte não foi encontrado no corpo da requisição.");
			}
			
			if(porte.getNome().isEmpty() || porte.getNome().isBlank()) {
				throw new Exception("Nome do porte não pode ser vazio.");
			}
			
			Porte novoPorte = porteService.cadastrar(porte);
			
			if(novoPorte.getId() == null || novoPorte.getId() == 0) {
				throw new Exception("Ocorreu algum problema ao cadastrar o porte.");
			}
						
			return ResponseEntity.ok(novoPorte);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}	
		
	}
	
	@ApiOperation("Endpoint para alterar as informações de um porte específico.")
	@RequestMapping(value="/{id}", method = RequestMethod.PUT, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> alterar(@PathVariable Long id, @RequestBody Porte porte){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			Porte porteExistente = porteService.buscarPorId(id);
			if(porteExistente == null || porteExistente.getId() == 0) {
				responseData.setTemErro(true);
				responseData.setMensagem("O porte " + id + " não foi encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(porte.getNome() == null) {
				throw new Exception("Nome do porte não foi encontrado no corpo da requisição.");
			}
			
			if(porte.getNome().isEmpty() || porte.getNome().isBlank()) {
				throw new Exception("Nome do porte não pode ser vazio.");
			}
			
			porte.setId(id);
			boolean porteAlterado = porteService.alterar(porte);
			
			if(porteAlterado == false) {
				throw new Exception("Ocorreu algum problema ao alterar o porte.");
			}
									
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}	
		
	}
	
	@ApiOperation("Endpoint para excluir um porte específico.")
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> deletar(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			Porte porteExistente = porteService.buscarPorId(id);
			if(porteExistente == null || porteExistente.getId() == 0) {
				responseData.setTemErro(true);
				responseData.setMensagem("O porte " + id + " não foi encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			boolean porteDeletado = porteService.deletarPorId(id);
			
			if(porteDeletado == false) {
				throw new Exception("Ocorreu algum problema ao deletar o porte.");
			}
			
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
}
