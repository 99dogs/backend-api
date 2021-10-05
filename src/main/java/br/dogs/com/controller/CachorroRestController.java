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
import br.dogs.com.model.entities.Cachorro;
import br.dogs.com.model.entities.Porte;
import br.dogs.com.model.entities.Raca;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.CachorroService;
import br.dogs.com.service.PorteService;
import br.dogs.com.service.RacaService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/cachorro")
public class CachorroRestController {
	
	@Autowired
	private CachorroService cachorroService;
	
	@Autowired
	private RacaService racaService;
	
	@Autowired
	private PorteService porteService;
	
	@ApiOperation("Retorna a lista de cachorros do tutor.")
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodos(){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			Long usuarioId = (long) 0;
			Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			if(usuario.getTipo().equals("TUTOR")) {
				usuarioId = usuario.getId();
			}
			
			List<Cachorro> portes = cachorroService.buscarTodos(usuarioId);
			
			return ResponseEntity.ok(portes);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
			
	}
	
	@ApiOperation("Endpoint para buscar um cachorro específico do tutor.")
	@RequestMapping(value="/{id}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> cachorroPorId(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			Cachorro cachorro = cachorroService.buscarPorId(id);
			
			if(cachorro == null || cachorro.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Cachorro " + id + " não encontrado");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(cachorro.getUsuarioId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não tem permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
						
			return ResponseEntity.ok(cachorro);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
	@ApiOperation("Endpoint para o tutor cadastrar um cachorro.")
	@RequestMapping(method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> cadastrar(@RequestBody Cachorro cachorro){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			if(cachorro.getNome() == null || cachorro.getNome().isEmpty()) {
				throw new Exception("Campo nome não pode ser vazio.");
			}
			
			if(cachorro.getComportamento() == null || cachorro.getComportamento().isEmpty()) {
				throw new Exception("Campo comportamento não pode ser vazio.");
			}
			
			Porte porte = porteService.buscarPorId(cachorro.getPorteId());
			
			if(porte == null || porte.getId() == null){
				throw new Exception("Campo porteId informado não encontrado.");
			}
			
			Raca raca = racaService.findById(cachorro.getRacaId());
			
			if(raca == null || raca.getId() == null){
				throw new Exception("Campo racaId informado não encontrado.");
			}
			
			cachorro.setUsuarioId(usuario.getId());
			Cachorro novoCachorro = cachorroService.cadastrar(cachorro);
			
			if(novoCachorro == null|| novoCachorro.getId() == null) {
				throw new Exception("Ocorreu algum problema ao cadastrar o cachorro.");
			}
						
			return ResponseEntity.ok(responseData);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para o tutor alterar as informações de um cachorro específico.")
	@RequestMapping(value="/{id}", method = RequestMethod.PUT, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> alterar(@PathVariable Long id, @RequestBody Cachorro cachorro){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			if(cachorro.getNome() == null || cachorro.getNome().isEmpty()) {
				throw new Exception("Campo nome não pode ser vazio.");
			}
			
			if(cachorro.getComportamento() == null || cachorro.getComportamento().isEmpty()) {
				throw new Exception("Campo comportamento não pode ser vazio.");
			}
			
			Porte porte = porteService.buscarPorId(cachorro.getPorteId());
			
			if(porte == null || porte.getId() == null){
				throw new Exception("Campo porteId informado não encontrado.");
			}
			
			Raca raca = racaService.findById(cachorro.getRacaId());
			
			if(raca == null || raca.getId() == null){
				throw new Exception("Campo racaId informado não encontrado.");
			}
			
			Cachorro cachorroExistente = cachorroService.buscarPorId(id);
			
			if(cachorroExistente == null || cachorroExistente.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Cachorro " + id + " não encontrado");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(cachorroExistente.getUsuarioId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não tem permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
			
			cachorro.setId(id);
			cachorro.setUsuarioId(usuario.getId());
			boolean cachorroAlterado = cachorroService.alterar(cachorro);
			
			if(cachorroAlterado == false) {
				throw new Exception("Ocorre um problema ao alterar o cachorro.");
			}
			
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}
		
	}
	
	@ApiOperation("Endpoint para o tutor excluir um cachorro específico.")
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> deletar(@PathVariable Long id){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			Cachorro cachorroExistente = cachorroService.buscarPorId(id);
			if(cachorroExistente == null || cachorroExistente.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("O cachorro " + id + " não foi encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(cachorroExistente.getUsuarioId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não tem permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
			
			boolean cachorroDeletado = cachorroService.deletarPorId(id);
			
			if(cachorroDeletado == false) {
				throw new Exception("Ocorreu algum problema ao deletar o cão.");
			}
						
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
}
