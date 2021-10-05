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
import br.dogs.com.model.entities.ConfiguracaoHorario;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.ConfiguracaoHorarioService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/configuracao-horario")
public class ConfiguracaoHorarioRestController {
	
	@Autowired
	private ConfiguracaoHorarioService configuracaoHorarioService;
	
	@ApiOperation("Retorna a lista de horários do dogwalker.")
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodos(){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			List<ConfiguracaoHorario> configuracoes = configuracaoHorarioService.buscarTodos(usuario.getId());
			
			return ResponseEntity.ok(configuracoes);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}	
	}
	
	@ApiOperation("Endpoint retorna uma configuração de horário específica.")
	@RequestMapping(value="/{id}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarPorId(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			ConfiguracaoHorario configuracao = configuracaoHorarioService.buscarPorId(id);
			
			if(configuracao == null || configuracao.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Configuração de horário " + id + " não encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(configuracao.getUsuarioId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não tem permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
						
			return ResponseEntity.ok(configuracao);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
	@ApiOperation("Endpoint para cadastrar uma nova configuração de horário.")
	@RequestMapping(method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> cadastrar(@RequestBody ConfiguracaoHorario configuracaoHorario){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			if(configuracaoHorario.getDiaSemana() < 0 || configuracaoHorario.getDiaSemana() > 6) {
				throw new Exception("O dia da semana deve ser de 0 à 6, representando cada dia da semana.");
			}
			
			if(configuracaoHorario.getHoraInicio() == null) {
				throw new Exception("Hora início não pode ser vazio.");
			}
			
			if(configuracaoHorario.getHoraFinal() == null) {
				throw new Exception("Hora final não pode ser vazio.");
			}
			
			ConfiguracaoHorario configExistente = configuracaoHorarioService.buscarPorDiaSemana(configuracaoHorario.getDiaSemana(), usuario.getId());
			if(configExistente != null && configExistente.getId() != null) {
				throw new Exception("Não é possível cadastrar uma nova configuração para o mesmo dia da semana.");
			}
			
			configuracaoHorario.setUsuarioId(usuario.getId());
			ConfiguracaoHorario novaConfig = configuracaoHorarioService.cadastrar(configuracaoHorario);
			
			if(novaConfig.getId() == null) {
				throw new Exception("Ocorreu algum problema ao cadastrar a configuração de horário.");
			}
						
			return ResponseEntity.ok(novaConfig);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}	
		
	}
	
	@ApiOperation("Endpoint para alterar uma configuração de horário existente.")
	@RequestMapping(value="/{id}", method = RequestMethod.PUT, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> alterar(@PathVariable Long id, @RequestBody ConfiguracaoHorario configuracaoHorario){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			if(configuracaoHorario.getDiaSemana() < 0 || configuracaoHorario.getDiaSemana() > 6) {
				throw new Exception("O dia da semana deve ser de 0 à 6, representando cada dia da semana.");
			}
			
			if(configuracaoHorario.getHoraInicio() == null) {
				throw new Exception("Hora início não pode ser vazio.");
			}
			
			if(configuracaoHorario.getHoraFinal() == null) {
				throw new Exception("Hora final não pode ser vazio.");
			}
			
			ConfiguracaoHorario configExistente = configuracaoHorarioService.buscarPorId(id);
			if(configExistente == null || configExistente.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Configuração de horário " + id + " não foi encontrada.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(configExistente.getUsuarioId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não possui permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
			
			configuracaoHorario.setId(id);
			configuracaoHorario.setUsuarioId(usuario.getId());
			boolean configAlterada = configuracaoHorarioService.alterar(configuracaoHorario);
			
			if(configAlterada == false) {
				throw new Exception("Ocorreu algum problema ao alterar a configuração de horário.");
			}
									
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}	
		
	}
	
	@ApiOperation("Endpoint para excluir uma configuração de horário específica.")
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> deletar(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			ConfiguracaoHorario configExistente = configuracaoHorarioService.buscarPorId(id);
			if(configExistente == null || configExistente.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("A configuração de horário " + id + " não foi encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(configExistente.getUsuarioId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não possui permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
			
			boolean configDeletada = configuracaoHorarioService.deletarPorId(id);
			
			if(configDeletada == false) {
				throw new Exception("Ocorreu algum problema ao deletar a configuração de horário.");
			}
						
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Retorna a lista de horários do dogwalker.")
	@RequestMapping(value="/dogwalker/{id}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodosPorDogwalker(@PathVariable Long id){
		
		//Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			List<ConfiguracaoHorario> configuracoes = configuracaoHorarioService.buscarTodos(id);
			
			return ResponseEntity.ok(configuracoes);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}	
	}
	
}
