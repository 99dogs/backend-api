package br.dogs.com.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import br.dogs.com.model.dto.PasseioLatLong;
import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.entities.Cachorro;
import br.dogs.com.model.entities.Passeio;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.CachorroService;
import br.dogs.com.service.HorarioService;
import br.dogs.com.service.PasseioService;
import br.dogs.com.service.SaldoService;
import br.dogs.com.service.TicketService;
import br.dogs.com.service.UsuarioService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/passeio")
public class PasseioRestController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private PasseioService passeioService;
	
	@Autowired
	private CachorroService cachorroService;
	
	@Autowired
	private HorarioService horarioService;
	
	@Autowired
	private SaldoService saldoService;
	
	@Autowired
	private TicketService ticketService;
	
	@ApiOperation("Endpoint para solicitar um novo passeio.")
	@RequestMapping(method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> solicitar(@RequestBody Passeio passeio){
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			Usuario usuarioLogado = usuarioService.buscarPorId(usuario.getId());
			if(usuarioLogado != null && usuarioLogado.getQtdeTicketDisponivel() == 0) {
				throw new Exception("Você não possui quantidade de ticket insuficiente para solicitar o passeio.");
			}
			
			passeio.setStatus(PasseioStatus.Espera.toString());
			
			if(passeio.getDatahora() == null) {
				throw new Exception("A data/hora não pode ser vazio ou nulo.");
			}
			
			if(passeio.getDatahora().isBefore(LocalDateTime.now())) {
				throw new Exception("A data/hora não pode ser no passado.");
			}
			
			if(passeio.getDogwalkerId() == null || passeio.getDogwalkerId() == 0) {
				throw new Exception("O dogwalkerId não pode ser vazio ou nulo.");
			}
			
			if(passeio.getCachorrosIds().size() == 0) {
				throw new Exception("Deve-se informar ao menos um cachorro na lista.");
			}
			
			for (int i = 0; i < passeio.getCachorrosIds().size(); i++) {
				
				Cachorro cachorro = cachorroService.buscarPorId(passeio.getCachorrosIds().get(i));
				
				if(cachorro == null || cachorro.getId() == null) {
					responseData.setTemErro(true);
					responseData.setMensagem("O cachorro " + passeio.getCachorrosIds().get(i) + " não foi encontrado.");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
				}
				
			}
			
			Usuario dogwalker = usuarioService.buscarPorId(passeio.getDogwalkerId());
			
			if(dogwalker == null || dogwalker.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("O dogwalkerId " + passeio.getDogwalkerId() + " não foi encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			String datahoraSolicitacao = passeio.getDatahora().format(formatter);
			
			boolean horarioDisponivel = horarioService.verificarDisponibilidade(datahoraSolicitacao, dogwalker.getId());
			
			if(horarioDisponivel == false) {
				throw new Exception("A data e hora solicitado não está disponível.");
			}
			
			passeio.setTutorId(usuario.getId());
			Passeio passeioSolicitado = passeioService.solicitar(passeio);
					
			return ResponseEntity.ok(passeioSolicitado);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}	
		
	}
	
	@ApiOperation("Endpoint retorna as informações de um passeio.")
	@RequestMapping(value="/{id}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarPorId(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		boolean acessoNegado = false;
		
		try {
			
			Passeio passeio = passeioService.buscarPorId(id);
			
			if(passeio == null || passeio.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Passeio " + id + " não encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(usuario.getTipo().equals(TipoUsuario.DOGWALKER.toString()) && passeio.getDogwalkerId() != usuario.getId()) {
				acessoNegado = true;
			} else if(usuario.getTipo().equals(TipoUsuario.TUTOR.toString()) && passeio.getTutorId() != usuario.getId()) {
				acessoNegado = true;
			}
			
			if(acessoNegado) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não tem permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
						
			return ResponseEntity.ok(passeio);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
	@ApiOperation("Endpoint retorna a lista de passeios do usuário.")
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodos(){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			List<Passeio> passeios = passeioService.buscarTodos(usuario.getId());
						
			return ResponseEntity.ok(passeios);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
	@ApiOperation("Endpoint para o dog walker aceitar um passeio.")
	@RequestMapping(value="/{id}/aceitar", method = RequestMethod.PUT, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> aceitarPasseio(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			Passeio passeio = passeioService.buscarPorId(id);
			
			if(passeio == null || passeio.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Passeio " +id+ " não encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(passeio.getDogwalkerId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não possui permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
			
			if(!passeio.getStatus().equals(PasseioStatus.Espera.toString())) {
				throw new Exception("Para aceitar o passeio o status do mesmo deve ser em '" + PasseioStatus.Espera.toString() + "'.");
			}
			
			boolean statusAlterado = passeioService.alterarStatus(id, PasseioStatus.Aceito.toString());
			
			if(statusAlterado == false) {
				throw new Exception("Ocorreu algum problema ao atualizar o status do passeio.");
			}
			
			ticketService.debitarComprador(passeio.getTutorId());
			
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
	@ApiOperation("Endpoint para o dog walker recusar um passeio.")
	@RequestMapping(value="/{id}/recusar", method = RequestMethod.PUT, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> recusarPasseio(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			Passeio passeio = passeioService.buscarPorId(id);
			
			if(passeio == null || passeio.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Passeio " +id+ " não encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(passeio.getDogwalkerId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não possui permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
			
			if(!passeio.getStatus().equals(PasseioStatus.Espera.toString())) {
				throw new Exception("Para recusar o passeio o status do mesmo deve ser em '" + PasseioStatus.Espera.toString() + "'.");
			}
			
			boolean statusAlterado = passeioService.alterarStatus(id, PasseioStatus.Recusado.toString());
			
			if(statusAlterado == false) {
				throw new Exception("Ocorreu algum problema ao atualizar o status do passeio.");
			}
						
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
	@ApiOperation("Endpoint para o dog walker iniciar um passeio.")
	@RequestMapping(value="/{id}/iniciar", method = RequestMethod.PUT, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> iniciarPasseio(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			Passeio passeio = passeioService.buscarPorId(id);
			
			if(passeio == null || passeio.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Passeio " +id+ " não encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(passeio.getDogwalkerId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não possui permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
			
			if(!passeio.getStatus().equals(PasseioStatus.Aceito.toString())) {
				throw new Exception("Para iniciar o passeio o status do mesmo deve ser '" + PasseioStatus.Aceito.toString() + "'.");
			}
			
			boolean statusAlterado = passeioService.alterarStatus(id, PasseioStatus.Andamento.toString());
			
			if(statusAlterado == false) {
				throw new Exception("Ocorreu algum problema ao atualizar o status do passeio.");
			}
			
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
	@ApiOperation("Endpoint para o dog walker finalizar um passeio.")
	@RequestMapping(value="/{id}/finalizar", method = RequestMethod.PUT, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> finalizarPasseio(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			Passeio passeio = passeioService.buscarPorId(id);
			
			if(passeio == null || passeio.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Passeio " +id+ " não encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
			
			if(passeio.getDogwalkerId() != usuario.getId()) {
				responseData.setTemErro(true);
				responseData.setMensagem("Você não possui permissão para acessar esse objeto.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
			}
			
			if(!passeio.getStatus().equals(PasseioStatus.Andamento.toString())) {
				throw new Exception("Para finalizar o passeio o status do mesmo deve ser em '" + PasseioStatus.Andamento.toString() + "'.");
			}
			
			boolean statusAlterado = passeioService.alterarStatus(id, PasseioStatus.Finalizado.toString());
			
			if(statusAlterado == false) {
				throw new Exception("Ocorreu algum problema ao atualizar o status do passeio.");
			}
			
			saldoService.creditarSaldo(id);
			
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
	@ApiOperation("Endpoint para enviar a latitude e longitude durante o passeio.")
	@RequestMapping(value="/lat-long", method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> registrarLatLong(@RequestBody PasseioLatLong passeioLatLong){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			if(passeioLatLong.getPasseioId() == null || passeioLatLong.getPasseioId() == 0) {
				throw new Exception("Campo passeioId não pode ser vazio.");
			}
			
			if(passeioLatLong.getLatitude() == null || passeioLatLong.getLatitude().isEmpty()) {
				throw new Exception("Campo latitude não pode ser vazio.");
			}
			
			if(passeioLatLong.getLongitude() == null || passeioLatLong.getLongitude().isEmpty()) {
				throw new Exception("Campo longitude não pode ser vazio.");
			}
			
			Passeio passeio = passeioService.buscarPorId(passeioLatLong.getPasseioId());
			
			if(passeio.getId() == null || passeio.getId() == 0) {
				throw new Exception("O passeio informado não foi encontrado.");
			}
			
			if(passeio.getDogwalkerId() != usuario.getId()) {
				throw new Exception("Você não tem permissão para acessar esse objeto.");
			}
			
			if(!passeio.getStatus().equals(PasseioStatus.Andamento.toString())) {
				throw new Exception("Para registrar a lat e long do passeio o status do mesmo deve ser em '" + PasseioStatus.Andamento.toString() + "'.");
			}
			
			boolean response = passeioService.registrarLatLong(passeioLatLong);
			
			if(response == false) {
				throw new Exception("Ocorreu algum problema ao registrar as informações.");
			}
						
			return ResponseEntity.ok(responseData);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para enviar a latitude e longitude durante o passeio.")
	@RequestMapping(value="/creditar-saldo/{passeioId}", method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> creditarSaldo(@PathVariable Long passeioId){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			boolean creditado = saldoService.creditarSaldo(passeioId);
			
			if(creditado == false) {
				throw new Exception("Ocorreu algum problema ao creditar o saldo do dogwalker.");
			}
			
			return ResponseEntity.ok(responseData);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}
		
	}
	
	@ApiOperation("Endpoint buscar a última localização do dogwalker durante o passeio.")
	@RequestMapping(value="/localizacao/posicao-atual/{passeioId}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> posicaoAtual(@PathVariable Long passeioId){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			Passeio passeio = passeioService.buscarPorId(passeioId);
			
			if(passeio.getId() == null || passeio.getId() == 0) {
				throw new Exception("Passeio não encontrado.");
			}
			
			if(passeio.getTutorId() != usuario.getId()) {
				throw new Exception("Você não possui permissão para acessar esse objeto.");
			}
			
			PasseioLatLong latLong = passeioService.posicaoAtual(passeioId);
			
			if(latLong.getId() == null || latLong.getId() == 0) {
				throw new Exception("Nenhuma posição encontrada.");
			}
			
			return ResponseEntity.ok(latLong);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}
		
	}
	
	@ApiOperation("Endpoint todas as localização do passeio.")
	@RequestMapping(value="/localizacao/posicao-completa/{passeioId}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> posicaoCompleta(@PathVariable Long passeioId){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			Passeio passeio = passeioService.buscarPorId(passeioId);
			
			if(passeio.getId() == null || passeio.getId() == 0) {
				throw new Exception("Passeio não encontrado.");
			}
			
			if(passeio.getTutorId() != usuario.getId()) {
				throw new Exception("Você não possui permissão para acessar esse objeto.");
			}
			
			List<PasseioLatLong> listLatLong = passeioService.posicaoCompleta(passeioId);
			
			if(listLatLong.size() == 0) {
				throw new Exception("Nenhuma posição encontrada.");
			}
			
			return ResponseEntity.ok(listLatLong);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}
		
	}
	
}
