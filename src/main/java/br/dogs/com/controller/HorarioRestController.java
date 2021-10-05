package br.dogs.com.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.dogs.com.model.dto.Disponibilidade;
import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.HorarioService;
import br.dogs.com.service.UsuarioService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/horario")
public class HorarioRestController {
	
	@Autowired
	private HorarioService horarioService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@ApiOperation("Verifica a disponibilidade de horário de um dogwalker.")
	@RequestMapping(value="/disponibilidade", method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> verificarDisponibilidade(@RequestBody Disponibilidade disponibilidade){

		ResponseData responseData = new ResponseData();
		
		try {
			
			if(disponibilidade.getDatahora() == null || disponibilidade.getDatahora().isEmpty()) {
				throw new Exception("A data/hora não pode ser vazio.");
			}else {
				LocalDateTime datahora = LocalDateTime.parse(disponibilidade.getDatahora());
				if(datahora.isBefore(LocalDateTime.now())) {
					throw new Exception("A data/hora não pode ser no passado.");
				}
			}
			
			if(disponibilidade.getUsuarioId() == null || disponibilidade.getUsuarioId() == 0) {
				throw new Exception("Campo usuarioId inválido.");
			}
			
			Usuario usuario = usuarioService.buscarPorId(disponibilidade.getUsuarioId());
			
			if(usuario == null || usuario.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Usuário " + disponibilidade.getUsuarioId() + " não encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(!usuario.getTipo().equals("DOGWALKER")) {
				throw new Exception("O usuário informado não é um dog walker.");
			}
			
			boolean disponivel = horarioService.verificarDisponibilidade(disponibilidade.getDatahora(), disponibilidade.getUsuarioId());
			
			if(disponivel == false) {
				throw new Exception("Horário indisponível.");
			}
						
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
}
