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
import br.dogs.com.model.entities.ConfiguracaoBase;
import br.dogs.com.service.ConfiguracaoBaseService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/configuracao-base")
public class ConfiguracaoBaseRestController {
	
	@Autowired
	private ConfiguracaoBaseService configuracaoBaseService;
	
	@ApiOperation("Retorna a configuração base da plataforma.")
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodos(){
		
		ResponseData responseData = new ResponseData();
		
		try {
						
			List<ConfiguracaoBase> configuracoes = configuracaoBaseService.buscarTodos();
						
			return ResponseEntity.ok(configuracoes);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
			
	}
	
	@ApiOperation("Endpoint para cadastrar a configuração base da plataforma.")
	@RequestMapping(method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> cadastrar(@RequestBody ConfiguracaoBase configuracaoBase){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			List<ConfiguracaoBase> configExistente = configuracaoBaseService.buscarTodos();
			if(!configExistente.isEmpty()) {
				throw new Exception("Não é possível inserir outra configuração, considere excluir ou alterar a configuração existente.");
			}
			
			if(configuracaoBase.getValorTicket() <= 0) {
				throw new Exception("O valor do ticket deve ser maior que R$1,00");
			}
			
			if(configuracaoBase.getTaxaPlataforma() < 0) {
				throw new Exception("A taxa da plataforma não pode ser menor que zero.");
			}
			
			if(configuracaoBase.getValorMinimoDeposito() < 0) {
				throw new Exception("O valor mínimo para depósito não pode ser menor que zero.");
			}
			
			if(configuracaoBase.getTempoPasseio() < 0) {
				throw new Exception("O tempo do passeio não pode ser menor que zero.");
			}
			
			ConfiguracaoBase configCadastrada = configuracaoBaseService.cadastrar(configuracaoBase);
			
			if(configCadastrada == null|| configCadastrada.getId() == null) {
				throw new Exception("Ocorreu algum problema ao cadastrar a configuração.");
			}
						
			return ResponseEntity.ok(configCadastrada);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para alterar as informações da configuração base da plataforma.")
	@RequestMapping(value="/{id}", method = RequestMethod.PUT, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> alterar(@PathVariable Long id, @RequestBody ConfiguracaoBase configuracaoBase){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			ConfiguracaoBase configExistente = configuracaoBaseService.buscarPorId(id);
			if(configExistente.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Configuração base " + id + " não encontrado");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(configuracaoBase.getValorTicket() <= 0) {
				throw new Exception("O valor do ticket deve ser maior que R$1,00");
			}
			
			if(configuracaoBase.getTaxaPlataforma() < 0) {
				throw new Exception("A taxa da plataforma não pode ser menor que zero.");
			}
			
			if(configuracaoBase.getValorMinimoDeposito() < 0) {
				throw new Exception("O valor mínimo para depósito não pode ser menor que zero.");
			}
			
			if(configuracaoBase.getTempoPasseio() < 0) {
				throw new Exception("O tempo do passeio não pode ser menor que zero.");
			}
			
			configuracaoBase.setId(id);
			boolean configAlterada = configuracaoBaseService.alterar(configuracaoBase);
			
			if(configAlterada == false) {
				throw new Exception("Ocorreu algum problema ao cadastrar a configuração.");
			}
									
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para excluir a configuração base da plataforma.")
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> deletar(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			ConfiguracaoBase configExistente = configuracaoBaseService.buscarPorId(id);
			if(configExistente == null || configExistente.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("A configuração " + id + " não foi encontrada.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
						
			boolean configDeletada = configuracaoBaseService.deletarPorId(id);
			
			if(configDeletada == false) {
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
