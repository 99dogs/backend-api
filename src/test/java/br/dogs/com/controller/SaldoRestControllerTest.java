package br.dogs.com.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.dogs.com.helper.TipoUsuario;
import br.dogs.com.model.entities.Deposito;
import br.dogs.com.model.entities.Saldo;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.DepositoService;
import br.dogs.com.service.SaldoService;
import br.dogs.com.service.UsuarioService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class SaldoRestControllerTest {

	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private SaldoRestController saldoRestController;
	
	@MockBean
	private SaldoService saldoService;
	
	@MockBean
	private DepositoService depositoService;
	
	@MockBean
	private UsuarioService usuarioService;
	
	private Usuario authUsuarioTutor = new Usuario();
	
	private Usuario authUsuarioDogwalker = new Usuario();
	
	
	private String tokenUsuarioTutor;
	
	private String tokenUsuarioDogwalker;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void startMocks(){
		saldoRestController = context.getBean(SaldoRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(saldoRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(saldoService);
		standaloneSetup(depositoService);
		standaloneSetup(usuarioService);
		
		authUsuarioTutor.setId(1L);
		authUsuarioTutor.setEmail("tutor");
		authUsuarioTutor.setTipo(TipoUsuario.TUTOR.name());
		
		authUsuarioDogwalker.setId(1L);
		authUsuarioDogwalker.setEmail("dogwalker");
		authUsuarioDogwalker.setTipo(TipoUsuario.DOGWALKER.name());
		
		tokenUsuarioTutor = jwtTokenProvider.createToken(authUsuarioTutor);
		tokenUsuarioDogwalker = jwtTokenProvider.createToken(authUsuarioDogwalker);
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTodos() throws Exception {
		
		List<Saldo> saldos = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.saldoService.buscarTodos(authUsuarioDogwalker.getId()))
			.thenReturn(saldos);
		
		mockMvc.perform(get("/api/v1/saldo")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarTodos_ThrowException() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.saldoService.buscarTodos(authUsuarioDogwalker.getId()))
			.thenAnswer((t) -> {throw new Exception("bad request");});
		
		mockMvc.perform(get("/api/v1/saldo")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoBuscarTodos_UsuarioNaoPermitido() throws Exception {
		
		List<Saldo> saldos = new ArrayList<>();
				
		when(this.saldoService.buscarTodos(authUsuarioTutor.getId()))
			.thenReturn(saldos);
		
		mockMvc.perform(get("/api/v1/saldo")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoSolicitarDeposito() throws Exception {
		
		Deposito deposito = new Deposito();
		
		deposito.setId(1L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.depositoService.solicitar(authUsuarioDogwalker.getId()))
			.thenReturn(deposito);
		
		mockMvc.perform(post("/api/v1/saldo/solicitar-deposito")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarUnathorized_QuandoSolicitarDeposito_UsuarioNaoPermitido() throws Exception {
		
		Deposito deposito = new Deposito();
		
		deposito.setId(1L);
				
		when(this.depositoService.solicitar(authUsuarioDogwalker.getId()))
			.thenReturn(deposito);
		
		mockMvc.perform(post("/api/v1/saldo/solicitar-deposito")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoSolicitarDeposito_ErroAoSolicitarDeposito() throws Exception {
		
		Deposito deposito = new Deposito();
				
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.depositoService.solicitar(authUsuarioDogwalker.getId()))
			.thenReturn(deposito);
		
		mockMvc.perform(post("/api/v1/saldo/solicitar-deposito")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoSolicitarDeposito_ThrowException() throws Exception {
						
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.depositoService.solicitar(authUsuarioDogwalker.getId()))
			.thenAnswer((t) -> {throw new Exception("bad request");});
		
		mockMvc.perform(post("/api/v1/saldo/solicitar-deposito")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTodosPorDeposito() throws Exception {
		
		List<Saldo> saldos = new ArrayList<>();
		Deposito deposito = new Deposito();
		
		deposito.setId(1L);
		deposito.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.depositoService.buscarPorId(deposito.getId()))
			.thenReturn(deposito);
		
		when(this.saldoService.buscarPorDeposito(deposito.getId()))
			.thenReturn(saldos);
		
		mockMvc.perform(get("/api/v1/saldo/deposito/{id}", deposito.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarTodosPorDeposito_DepositoNaoExistente() throws Exception {
		
		List<Saldo> saldos = new ArrayList<>();
		Deposito deposito = new Deposito();
		
		deposito.setId(1L);
		deposito.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.depositoService.buscarPorId(deposito.getId()))
			.thenReturn(null);
		
		when(this.saldoService.buscarPorDeposito(deposito.getId()))
			.thenReturn(saldos);
		
		mockMvc.perform(get("/api/v1/saldo/deposito/{id}", deposito.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarTodosPorDeposito_UsuarioDivergente() throws Exception {
		
		List<Saldo> saldos = new ArrayList<>();
		Deposito deposito = new Deposito();
		
		deposito.setId(1L);
		deposito.setUsuarioId(999L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.depositoService.buscarPorId(deposito.getId()))
			.thenReturn(deposito);
		
		when(this.saldoService.buscarPorDeposito(deposito.getId()))
			.thenReturn(saldos);
		
		mockMvc.perform(get("/api/v1/saldo/deposito/{id}", deposito.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarTodosPorDeposito_ThrowException() throws Exception {
		
		Deposito deposito = new Deposito();
		
		deposito.setId(1L);
		deposito.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.depositoService.buscarPorId(deposito.getId()))
			.thenReturn(deposito);
		
		when(this.saldoService.buscarPorDeposito(deposito.getId()))
			.thenAnswer((t) -> {throw new Exception("bad requedt");});
		
		mockMvc.perform(get("/api/v1/saldo/deposito/{id}", deposito.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
}
