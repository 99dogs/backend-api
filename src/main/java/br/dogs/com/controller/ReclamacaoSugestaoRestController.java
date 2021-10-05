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
import br.dogs.com.model.entities.ReclamacaoSugestao;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.ReclamacaoSugestaoService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/reclamacao-sugestao")
public class ReclamacaoSugestaoRestController {
	
	@Autowired
	private ReclamacaoSugestaoService reclamacaoSugestaoService;
	
	@ApiOperation("Retorna a lista de reclamação/sugestão cadastradas na plataforma.")
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodos(){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
						
			List<ReclamacaoSugestao> reclamacaoSugestao  = reclamacaoSugestaoService.buscarTodos(usuario.getId());
						
			return ResponseEntity.ok(reclamacaoSugestao);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
			
	}
	
	@ApiOperation("Endpoint para cadastrar uma reclamação/sugestão.")
	@RequestMapping(method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> cadastrar(@RequestBody ReclamacaoSugestao reclamacaoSugestao){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			if(reclamacaoSugestao.getAssunto() == null || reclamacaoSugestao.getAssunto().isEmpty()) {
				throw new Exception("É necessário informar o assunto.");
			}
			
			if(reclamacaoSugestao.getMensagem() == null || reclamacaoSugestao.getMensagem().isEmpty()) {
				throw new Exception("É necessário informar a mensagem.");
			}
			
			reclamacaoSugestao.setUsuarioId(usuario.getId());
			ReclamacaoSugestao reclamacaoSugestaoCadastrada = reclamacaoSugestaoService.cadastrar(reclamacaoSugestao);
			
			if(reclamacaoSugestaoCadastrada.getId() == null) {
				throw new Exception("Ocorreu algum problema ao cadastrar a reclamação/sugestão.");
			}
						
			return ResponseEntity.ok(reclamacaoSugestaoCadastrada);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
			
	}
	
	@ApiOperation("Endpoint para excluir uma reclamação/sugestão específica.")
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> deletar(@PathVariable Long id){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			ReclamacaoSugestao reclamacaoSugestao = reclamacaoSugestaoService.buscarPorId(id);
			
			if(reclamacaoSugestao == null || reclamacaoSugestao.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("A reclamação/sugestão " + id + " não foi encontrada.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(reclamacaoSugestao.getUsuarioId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não tem permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
			
			boolean itemDeletado = reclamacaoSugestaoService.deletarPorId(id);
			
			if(itemDeletado == false) {
				throw new Exception("Ocorreu algum problema ao deletar a configuração.");
			}
			
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
	
	}
	
}
