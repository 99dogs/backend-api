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
import br.dogs.com.model.entities.Qualificacao;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.QualificacaoService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/qualificacao")
public class QualificacaoRestController {
	
	@Autowired
	private QualificacaoService qualificacaoService;
	
	@ApiOperation("Retorna a lista de qualificações do Dog walker.")
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodos(){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			List<Qualificacao> qualificacoes = qualificacaoService.buscarTodos(usuario.getId());
						
			return ResponseEntity.ok(qualificacoes);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
			
	}
	
	@ApiOperation("Endpoint para o dog walker cadastrar suas qualificações.")
	@RequestMapping(method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> cadastrar(@RequestBody Qualificacao qualificacao){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			if(qualificacao.getTitulo() == null || qualificacao.getTitulo().isEmpty()) {
				throw new Exception("Campo título não pode ser vazio.");
			}
			
			if(qualificacao.getModalidade() == null || qualificacao.getModalidade().isEmpty()) {
				throw new Exception("Campo modalidade não pode ser vazio.");
			}
			
			if(qualificacao.getDescricao() == null || qualificacao.getDescricao().isEmpty()) {
				throw new Exception("Campo descrição não pode ser vazio.");
			}
			
			qualificacao.setUsuarioId(usuario.getId());
			Qualificacao novaQualificacao = qualificacaoService.cadastrar(qualificacao);
			
			if(novaQualificacao == null|| novaQualificacao.getId() == null || novaQualificacao.getId() == 0) {
				throw new Exception("Ocorreu algum problema ao cadastrar a qualificação.");
			}
						
			return ResponseEntity.ok(novaQualificacao);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para o dog walker alterar as informações de uma qualificação.")
	@RequestMapping(value="/{id}", method = RequestMethod.PUT, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> alterar(@PathVariable Long id, @RequestBody Qualificacao qualificacao){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			if(qualificacao.getTitulo() == null || qualificacao.getTitulo().isEmpty()) {
				throw new Exception("Campo título não pode ser vazio.");
			}
			
			if(qualificacao.getModalidade() == null || qualificacao.getModalidade().isEmpty()) {
				throw new Exception("Campo modalidade não pode ser vazio.");
			}
			
			if(qualificacao.getDescricao() == null || qualificacao.getDescricao().isEmpty()) {
				throw new Exception("Campo descrição não pode ser vazio.");
			}
			
			Qualificacao qualificacaoExistente = qualificacaoService.buscarPorId(id);
			
			if(qualificacaoExistente == null || qualificacaoExistente.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("A qualificação " + id + " não foi encontrada.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(qualificacaoExistente.getUsuarioId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não tem permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
			
			qualificacao.setId(id);
			qualificacao.setUsuarioId(usuario.getId());
			boolean qualificacaoAlterada = qualificacaoService.alterar(qualificacao);
			
			if(qualificacaoAlterada == false) {
				throw new Exception("Ocorreu algum problema ao alterar a qualificação.");
			}
			
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para buscar uma qualificação específica.")
	@RequestMapping(value="/{id}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarPorId(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			Qualificacao qualificacao = qualificacaoService.buscarPorId(id);
			
			if(qualificacao == null || qualificacao.getId() == null || qualificacao.getId() == 0) {
				responseData.setTemErro(true);
				responseData.setMensagem("A qualificação " + id + " não encontrada.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(qualificacao.getUsuarioId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não tem permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
						
			return ResponseEntity.ok(qualificacao);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
	@ApiOperation("Endpoint para o dog walker excluir uma qualificação específica.")
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> deletar(@PathVariable Long id){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			Qualificacao qualificacaoExistente = qualificacaoService.buscarPorId(id);
			if(qualificacaoExistente == null || qualificacaoExistente.getId() == null || qualificacaoExistente.getId() == 0) {
				responseData.setTemErro(true);
				responseData.setMensagem("A qualificação " + id + " não foi encontrada.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(qualificacaoExistente.getUsuarioId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não tem permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
			
			boolean qualificacaoDeletada = qualificacaoService.deletarPorId(id);
			
			if(qualificacaoDeletada == false) {
				throw new Exception("Ocorreu algum problema ao deletar a qualificação.");
			}
						
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Retorna a lista de qualificações do Dog walker especificado.")
	@RequestMapping(value="/dogwalker/{id}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodosPorDogwalker(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			//Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			List<Qualificacao> qualificacoes = qualificacaoService.buscarTodos(id);
						
			return ResponseEntity.ok(qualificacoes);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
			
	}
	
}
