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

import br.dogs.com.helper.PasseioStatus;
import br.dogs.com.helper.TipoUsuario;
import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.entities.Avaliacao;
import br.dogs.com.model.entities.Passeio;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.AvaliacaoService;
import br.dogs.com.service.PasseioService;
import br.dogs.com.service.UsuarioService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/avaliacao")
public class AvaliacaoRestController {

	@Autowired
	private AvaliacaoService avaliacaoService;

	@Autowired
	private PasseioService passeioService;
	
	@Autowired
	private UsuarioService usuarioService;

	@ApiOperation("Registra uma avaliação referente ao passeio finalizado")
	@RequestMapping(method = RequestMethod.POST, produces = { "application/json; charset=utf-8" })
	public ResponseEntity<Object> cadastrar(@RequestBody Avaliacao avaliacao) {

		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();

		try {

			if (avaliacao.getNota() < 0 || avaliacao.getNota() > 5) {
				throw new Exception("A nota deve ser entre 0 - 5");
			}

			if (avaliacao.getPasseioId() == null || avaliacao.getPasseioId() == 0) {
				throw new Exception("É necessário informar o passeioId.");
			}

			Passeio passeio = passeioService.buscarPorId(avaliacao.getPasseioId());

			if (passeio == null || passeio.getId() == null || passeio.getId() == 0) {
				throw new Exception("Passeio não encontrado.");
			}
			
			if(!passeio.getStatus().equals(PasseioStatus.Finalizado.toString())) {
				throw new Exception("O passeio precisa estar finalizado para ser avaliado.");
			}
			
			if (passeio.getTutorId() != usuario.getId()) {
				throw new Exception("Você não possui permissão para acessar esse objeto.");
			}
			
			Avaliacao newAvaliacao = avaliacaoService.cadastrar(avaliacao);

			if (newAvaliacao == null || newAvaliacao.getId() == 0) {
				throw new Exception("Ocorreu um problema ao cadastrar a avaliação.");
			}
			
			avaliacaoService.atualizarMediaAvaliacao(passeio.getDogwalkerId());
			
			return ResponseEntity.ok(newAvaliacao);

		} catch (Exception e) {

			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);

		}

	}
	
	@ApiOperation("Retorna as avaliações do dogwalker.")
	@RequestMapping(value="/dogwalker/{id}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodosPorDogwalker(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			Usuario usuario = usuarioService.buscarPorId(id);
			
			if(usuario == null || usuario.getId() == 0) {
				throw new Exception("Dog walker não encontrado.");
			}
			
			if(!usuario.getTipo().equals(TipoUsuario.DOGWALKER.toString())) {
				throw new Exception("Usuário não é um dog walker.");
			}
			
			List<Avaliacao> avaliacoes = avaliacaoService.buscarTodosPorDogwalker(id);
			
			return ResponseEntity.ok(avaliacoes);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
			
	}
	
	@ApiOperation("Endpoint para o tutor deletar uma avaliação.")
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> deletar(@PathVariable Long id){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			Avaliacao avaliacao = avaliacaoService.buscarPorId(id);
			
			if(avaliacao.getId() == null || avaliacao.getId() == 0) {
				throw new Exception("Avaliação não encontrada.");
			}
			
			Passeio passeio = passeioService.buscarPorId(avaliacao.getPasseioId());
			
			if(passeio == null || passeio.getId() == 0) {
				throw new Exception("Passeio não encontrado.");
			}
			
			if(passeio.getTutorId() != usuario.getId()) {
				throw new Exception("Você não possui permissão para acessar esse objeto.");
			}
			
			boolean deletado = avaliacaoService.deletar(id);
			
			if(deletado == false) {
				throw new Exception("Ocorreu um problema ao deletar a avaliação.");
			}
			
			avaliacaoService.atualizarMediaAvaliacao(passeio.getDogwalkerId());
			
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}
	
	}
	
	@ApiOperation("Retorna a avaliação do passeio.")
	@RequestMapping(value="/passeio/{id}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarPorPasseioId(@PathVariable Long id){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			Passeio passeio = passeioService.buscarPorId(id);
			
			if(passeio == null || passeio.getId() == null || passeio.getId() == 0) {
				throw new Exception("Passeio não encontrado.");
			}
			
			if(usuario.getTipo().equals(TipoUsuario.DOGWALKER.toString()) && passeio.getDogwalkerId() != usuario.getId()) {
				throw new Exception("Você não possui permissão para acessar esse objeto.");
			}
			
			if(usuario.getTipo().equals(TipoUsuario.TUTOR.toString()) && passeio.getTutorId() != usuario.getId()) {
				throw new Exception("Você não possui permissão para acessar esse objeto.");
			}
			
			Avaliacao avaliacao = avaliacaoService.buscarPorPasseioId(id);
						
			return ResponseEntity.ok(avaliacao);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
			
	}
	
}
