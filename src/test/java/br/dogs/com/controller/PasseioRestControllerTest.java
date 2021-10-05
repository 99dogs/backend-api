package br.dogs.com.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.dogs.com.helper.PasseioStatus;
import br.dogs.com.helper.TipoUsuario;
import br.dogs.com.model.dto.PasseioLatLong;
import br.dogs.com.model.entities.Cachorro;
import br.dogs.com.model.entities.Passeio;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.CachorroService;
import br.dogs.com.service.HorarioService;
import br.dogs.com.service.PasseioService;
import br.dogs.com.service.SaldoService;
import br.dogs.com.service.TicketService;
import br.dogs.com.service.UsuarioService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class PasseioRestControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private PasseioRestController passeioRestController;
	
	@MockBean
	private PasseioService passeioService;
	
	@MockBean
	private CachorroService cachorroService;
	
	@MockBean
	private HorarioService horarioService;
	
	@MockBean
	private SaldoService saldoService;
	
	@MockBean
	private TicketService ticketService;
	
	@MockBean
	private UsuarioService usuarioService;
	
	private Usuario authUsuarioAdmin = new Usuario();
	
	private Usuario authUsuarioTutor = new Usuario();
	
	private Usuario authUsuarioDogwalker = new Usuario();
	
	private String tokenUsuarioAdmin;
	
	private String tokenUsuarioTutor;
	
	private String tokenUsuarioDogwalker;
	
	ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void startMocks(){
		passeioRestController = context.getBean(PasseioRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(passeioRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(passeioService);
		standaloneSetup(usuarioService);
		standaloneSetup(cachorroService);
		standaloneSetup(horarioService);
		standaloneSetup(saldoService);
		standaloneSetup(ticketService);
		
		authUsuarioAdmin.setId(1L);
		authUsuarioAdmin.setEmail("admin");
		authUsuarioAdmin.setTipo(TipoUsuario.ADMIN.name());
		
		authUsuarioTutor.setId(2L);
		authUsuarioTutor.setEmail("tutor");
		authUsuarioTutor.setTipo(TipoUsuario.TUTOR.name());
		
		authUsuarioDogwalker.setId(3L);
		authUsuarioDogwalker.setEmail("dogwalker");
		authUsuarioDogwalker.setTipo(TipoUsuario.DOGWALKER.name());
		
		tokenUsuarioAdmin = jwtTokenProvider.createToken(authUsuarioAdmin);
		tokenUsuarioTutor = jwtTokenProvider.createToken(authUsuarioTutor);
		tokenUsuarioDogwalker = jwtTokenProvider.createToken(authUsuarioDogwalker);
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoSolicitarPasseio() throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		Passeio passeio = new Passeio();
		Cachorro cachorro = new Cachorro();
		
		cachorro.setId(10L);
		cachorro.setNome("cachorro test");
		
		List<Long> cachorros = new ArrayList<>();
		cachorros.add(cachorro.getId());
		
		passeio.setDatahora(LocalDateTime.of(2050, 1, 21, 23, 59, 00));
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setCachorrosIds(cachorros);
				
		String datahoraSolicitacao = passeio.getDatahora().format(formatter);
		
		authUsuarioTutor.setQtdeTicketDisponivel(1);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(authUsuarioTutor);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(datahoraSolicitacao, authUsuarioDogwalker.getId()))
			.thenReturn(true);
		
		when(this.passeioService.solicitar(Mockito.any(Passeio.class)))
			.thenReturn(passeio);
		
		String json = objectMapper.writeValueAsString(passeio);
		
		mockMvc.perform(post("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarForbidden_QuandoSolicitarPasseio_UsuarioDogwalker() throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		Passeio passeio = new Passeio();
		Cachorro cachorro = new Cachorro();
		
		cachorro.setId(10L);
		cachorro.setNome("cachorro test");
		
		List<Long> cachorros = new ArrayList<>();
		cachorros.add(cachorro.getId());
		
		passeio.setDatahora(LocalDateTime.of(2050, 1, 21, 23, 59, 00));
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setCachorrosIds(cachorros);
				
		String datahoraSolicitacao = passeio.getDatahora().format(formatter);
		
		authUsuarioTutor.setQtdeTicketDisponivel(1);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(authUsuarioTutor);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(datahoraSolicitacao, authUsuarioDogwalker.getId()))
			.thenReturn(true);
		
		when(this.passeioService.solicitar(Mockito.any(Passeio.class)))
			.thenReturn(passeio);
		
		String json = objectMapper.writeValueAsString(passeio);
		
		mockMvc.perform(post("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isForbidden());
		
	}
	
	@Test
	public void deveRetornarForbidden_QuandoSolicitarPasseio_UsuarioAdmin() throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		Passeio passeio = new Passeio();
		Cachorro cachorro = new Cachorro();
		
		cachorro.setId(10L);
		cachorro.setNome("cachorro test");
		
		List<Long> cachorros = new ArrayList<>();
		cachorros.add(cachorro.getId());
		
		passeio.setDatahora(LocalDateTime.of(2050, 1, 21, 23, 59, 00));
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setCachorrosIds(cachorros);
				
		String datahoraSolicitacao = passeio.getDatahora().format(formatter);
		
		authUsuarioTutor.setQtdeTicketDisponivel(1);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(authUsuarioTutor);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(datahoraSolicitacao, authUsuarioDogwalker.getId()))
			.thenReturn(true);
		
		when(this.passeioService.solicitar(Mockito.any(Passeio.class)))
			.thenReturn(passeio);
		
		String json = objectMapper.writeValueAsString(passeio);
		
		mockMvc.perform(post("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isForbidden());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoSolicitarPasseio_TutorComTicketIndisponivel() throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		Passeio passeio = new Passeio();
		Cachorro cachorro = new Cachorro();
		
		cachorro.setId(10L);
		cachorro.setNome("cachorro test");
		
		List<Long> cachorros = new ArrayList<>();
		cachorros.add(cachorro.getId());
		
		passeio.setDatahora(LocalDateTime.of(2050, 1, 21, 23, 59, 00));
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setCachorrosIds(cachorros);
				
		String datahoraSolicitacao = passeio.getDatahora().format(formatter);
		
		authUsuarioTutor.setQtdeTicketDisponivel(0);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(authUsuarioTutor);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(datahoraSolicitacao, authUsuarioDogwalker.getId()))
			.thenReturn(true);
		
		when(this.passeioService.solicitar(Mockito.any(Passeio.class)))
			.thenReturn(passeio);
		
		String json = objectMapper.writeValueAsString(passeio);
		
		mockMvc.perform(post("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoSolicitarPasseio_DatahoraNula() throws Exception {
				
		Passeio passeio = new Passeio();
		Cachorro cachorro = new Cachorro();
		
		cachorro.setId(10L);
		cachorro.setNome("cachorro test");
		
		List<Long> cachorros = new ArrayList<>();
		cachorros.add(cachorro.getId());
		
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setCachorrosIds(cachorros);
						
		authUsuarioTutor.setQtdeTicketDisponivel(0);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(authUsuarioTutor);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade("", authUsuarioDogwalker.getId()))
			.thenReturn(false);
		
		when(this.passeioService.solicitar(Mockito.any(Passeio.class)))
			.thenReturn(passeio);
		
		String json = objectMapper.writeValueAsString(passeio);
		
		mockMvc.perform(post("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoSolicitarPasseio_DatahoraNoPassado() throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		Passeio passeio = new Passeio();
		Cachorro cachorro = new Cachorro();
		
		cachorro.setId(10L);
		cachorro.setNome("cachorro test");
		
		List<Long> cachorros = new ArrayList<>();
		cachorros.add(cachorro.getId());
		
		passeio.setDatahora(LocalDateTime.of(2020, 1, 21, 23, 59, 00));
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setCachorrosIds(cachorros);
				
		String datahoraSolicitacao = passeio.getDatahora().format(formatter);
		
		authUsuarioTutor.setQtdeTicketDisponivel(1);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(authUsuarioTutor);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(datahoraSolicitacao, authUsuarioDogwalker.getId()))
			.thenReturn(true);
		
		when(this.passeioService.solicitar(Mockito.any(Passeio.class)))
			.thenReturn(passeio);
		
		String json = objectMapper.writeValueAsString(passeio);
		
		mockMvc.perform(post("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoSolicitarPasseio_DogwalkerIdNaoInformado() throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		Passeio passeio = new Passeio();
		Cachorro cachorro = new Cachorro();
		
		cachorro.setId(10L);
		cachorro.setNome("cachorro test");
		
		List<Long> cachorros = new ArrayList<>();
		cachorros.add(cachorro.getId());
		
		passeio.setDatahora(LocalDateTime.of(2050, 1, 21, 23, 59, 00));
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(0L);
		passeio.setCachorrosIds(cachorros);
				
		String datahoraSolicitacao = passeio.getDatahora().format(formatter);
		
		authUsuarioTutor.setQtdeTicketDisponivel(1);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(authUsuarioTutor);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(datahoraSolicitacao, authUsuarioDogwalker.getId()))
			.thenReturn(true);
		
		when(this.passeioService.solicitar(Mockito.any(Passeio.class)))
			.thenReturn(passeio);
		
		String json = objectMapper.writeValueAsString(passeio);
		
		mockMvc.perform(post("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoSolicitarPasseio_DogwalkerIdNaoExistente() throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		Passeio passeio = new Passeio();
		Cachorro cachorro = new Cachorro();
		
		cachorro.setId(10L);
		cachorro.setNome("cachorro test");
		
		List<Long> cachorros = new ArrayList<>();
		cachorros.add(cachorro.getId());
		
		passeio.setDatahora(LocalDateTime.of(2050, 1, 21, 23, 59, 00));
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setCachorrosIds(cachorros);
				
		String datahoraSolicitacao = passeio.getDatahora().format(formatter);
		
		authUsuarioTutor.setQtdeTicketDisponivel(1);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(authUsuarioTutor);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(null);
		
		when(this.horarioService.verificarDisponibilidade(datahoraSolicitacao, authUsuarioDogwalker.getId()))
			.thenReturn(true);
		
		when(this.passeioService.solicitar(Mockito.any(Passeio.class)))
			.thenReturn(passeio);
		
		String json = objectMapper.writeValueAsString(passeio);
		
		mockMvc.perform(post("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoSolicitarPasseio_CachorrosIdsNaoInformado() throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		Passeio passeio = new Passeio();
		Cachorro cachorro = new Cachorro();
		
		cachorro.setId(10L);
		cachorro.setNome("cachorro test");
		
		List<Long> cachorros = new ArrayList<>();
		cachorros.add(cachorro.getId());
		
		passeio.setDatahora(LocalDateTime.of(2050, 1, 21, 23, 59, 00));
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
				
		String datahoraSolicitacao = passeio.getDatahora().format(formatter);
		
		authUsuarioTutor.setQtdeTicketDisponivel(1);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(authUsuarioTutor);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(datahoraSolicitacao, authUsuarioDogwalker.getId()))
			.thenReturn(true);
		
		when(this.passeioService.solicitar(Mockito.any(Passeio.class)))
			.thenReturn(passeio);
		
		String json = objectMapper.writeValueAsString(passeio);
		
		mockMvc.perform(post("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoSolicitarPasseio_CachorrosIdsNaoExistente() throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		Passeio passeio = new Passeio();
		Cachorro cachorro = new Cachorro();
		
		cachorro.setId(10L);
		cachorro.setNome("cachorro test");
		
		List<Long> cachorros = new ArrayList<>();
		cachorros.add(cachorro.getId());
		
		passeio.setDatahora(LocalDateTime.of(2050, 1, 21, 23, 59, 00));
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setCachorrosIds(cachorros);
				
		String datahoraSolicitacao = passeio.getDatahora().format(formatter);
		
		authUsuarioTutor.setQtdeTicketDisponivel(1);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(authUsuarioTutor);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(null);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(datahoraSolicitacao, authUsuarioDogwalker.getId()))
			.thenReturn(true);
		
		when(this.passeioService.solicitar(Mockito.any(Passeio.class)))
			.thenReturn(passeio);
		
		String json = objectMapper.writeValueAsString(passeio);
		
		mockMvc.perform(post("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoSolicitarPasseio_HorarioIndisponivel() throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		Passeio passeio = new Passeio();
		Cachorro cachorro = new Cachorro();
		
		cachorro.setId(10L);
		cachorro.setNome("cachorro test");
		
		List<Long> cachorros = new ArrayList<>();
		cachorros.add(cachorro.getId());
		
		passeio.setDatahora(LocalDateTime.of(2050, 1, 21, 23, 59, 00));
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setCachorrosIds(cachorros);
				
		String datahoraSolicitacao = passeio.getDatahora().format(formatter);
		
		authUsuarioTutor.setQtdeTicketDisponivel(1);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(authUsuarioTutor);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(datahoraSolicitacao, authUsuarioDogwalker.getId()))
			.thenReturn(false);
		
		when(this.passeioService.solicitar(Mockito.any(Passeio.class)))
			.thenReturn(passeio);
		
		String json = objectMapper.writeValueAsString(passeio);
		
		mockMvc.perform(post("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarPorId_UsuarioTutor() throws Exception {
		
		Passeio passeio = new Passeio();
		passeio.setId(1L);
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		mockMvc.perform(get("/api/v1/passeio/{id}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarPorId_UsuarioDogwalker() throws Exception {
		
		Passeio passeio = new Passeio();
		passeio.setId(1L);
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		mockMvc.perform(get("/api/v1/passeio/{id}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarForbidden_QuandoBuscarPorId_UsuarioAdmin() throws Exception {
		
		Passeio passeio = new Passeio();
		passeio.setId(1L);
		passeio.setDogwalkerId(authUsuarioAdmin.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		mockMvc.perform(get("/api/v1/passeio/{id}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isForbidden());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoBuscarPorId_DogwalkerIdDivergente() throws Exception {
		
		Passeio passeio = new Passeio();
		passeio.setId(1L);
		passeio.setDogwalkerId(999L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		mockMvc.perform(get("/api/v1/passeio/{id}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoBuscarPorId_TutorIdDivergente() throws Exception {
		
		Passeio passeio = new Passeio();
		passeio.setId(1L);
		passeio.setTutorId(999L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		mockMvc.perform(get("/api/v1/passeio/{id}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoBuscarPorId_PasseioNaoExistente() throws Exception {
		
		Passeio passeio = new Passeio();
		passeio.setId(1L);
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(null);
		
		mockMvc.perform(get("/api/v1/passeio/{id}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarPorId_ThrowException() throws Exception {
		
		Passeio passeio = new Passeio();
		passeio.setId(1L);
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenAnswer((t) -> {throw new Exception("bad request");});
		
		mockMvc.perform(get("/api/v1/passeio/{id}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarPorTodos_UsuarioTutor() throws Exception {
		
		List<Passeio> passeios = new ArrayList<>();
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setTutorId(authUsuarioTutor.getId());
		
		passeios.add(passeio);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.passeioService.buscarTodos(authUsuarioTutor.getId()))
			.thenReturn(passeios);
		
		mockMvc.perform(get("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarPorTodos_UsuarioDogwalker() throws Exception {
		
		List<Passeio> passeios = new ArrayList<>();
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setTutorId(authUsuarioDogwalker.getId());
		
		passeios.add(passeio);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarTodos(authUsuarioDogwalker.getId()))
			.thenReturn(passeios);
		
		mockMvc.perform(get("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarForbidden_QuandoBuscarPorTodos_UsuarioAdmin() throws Exception {
		
		List<Passeio> passeios = new ArrayList<>();
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setTutorId(authUsuarioAdmin.getId());
		
		passeios.add(passeio);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.passeioService.buscarTodos(authUsuarioAdmin.getId()))
			.thenReturn(passeios);
		
		mockMvc.perform(get("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isForbidden());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarPorTodos_ThrowException() throws Exception {
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarTodos(authUsuarioDogwalker.getId()))
			.thenAnswer((t) -> {throw new Exception("bad request");});
		
		mockMvc.perform(get("/api/v1/passeio")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoAceitarPasseio() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Aceito.name()))
			.thenReturn(true);
		
		when(this.ticketService.debitarComprador(authUsuarioTutor.getId()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/aceitar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoAceitarPasseio_PasseioNaoExistente() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(null);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Aceito.name()))
			.thenReturn(true);
		
		when(this.ticketService.debitarComprador(authUsuarioTutor.getId()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/aceitar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoAceitarPasseio_DogwalkerDivergente() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(999L);
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Aceito.name()))
			.thenReturn(true);
		
		when(this.ticketService.debitarComprador(authUsuarioTutor.getId()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/aceitar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAceitarPasseio_StatusDiferenteDeEspera() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Aceito.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Aceito.name()))
			.thenReturn(true);
		
		when(this.ticketService.debitarComprador(authUsuarioTutor.getId()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/aceitar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAceitarPasseio_ErroAoAtualizarOStatus() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Aceito.name()))
			.thenReturn(false);
		
		when(this.ticketService.debitarComprador(authUsuarioTutor.getId()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/aceitar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoRecusarPasseio() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Recusado.name()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/recusar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoRecusarPasseio_PasseioNaoExistente() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(null);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Aceito.name()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/recusar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoRecusarPasseio_DogwalkerDivergente() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(999L);
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Recusado.name()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/recusar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRecusarPasseio_StatusDiferenteDeEspera() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Aceito.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Recusado.name()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/recusar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRecusarPasseio_ErroAoAtualizarOStatus() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Recusado.name()))
			.thenReturn(false);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/recusar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoFinalizarPasseio_PasseioNaoExistente() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Aceito.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(null);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Finalizado.name()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/finalizar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoFinalizarPasseio_DogwalkerDivergente() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Andamento.name());
		passeio.setDogwalkerId(999L);
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Finalizado.name()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/finalizar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoFinalizarPasseio_StatusDiferenteDeAndamento() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Aceito.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Finalizado.name()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/finalizar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoFinalizarPasseio_ErroAoAtualizarOStatus() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Andamento.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Finalizado.name()))
			.thenReturn(false);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/finalizar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoFinalizarPasseio() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Andamento.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Finalizado.name()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/finalizar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoIniciarPasseio() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Aceito.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Andamento.name()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/iniciar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoIniciarPasseio_PasseioNaoExistente() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Aceito.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(null);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Andamento.name()))
			.thenReturn(true);
		
		when(this.ticketService.debitarComprador(authUsuarioTutor.getId()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/iniciar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoIniciarPasseio_DogwalkerDivergente() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Aceito.name());
		passeio.setDogwalkerId(999L);
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Andamento.name()))
			.thenReturn(true);
		
		when(this.ticketService.debitarComprador(authUsuarioTutor.getId()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/iniciar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoIniciarPasseio_StatusDiferenteDeAceito() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Espera.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Andamento.name()))
			.thenReturn(true);
		
		when(this.ticketService.debitarComprador(authUsuarioTutor.getId()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/iniciar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoIniciarPasseio_ErroAoAtualizarOStatus() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Aceito.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.alterarStatus(passeio.getId(), PasseioStatus.Andamento.name()))
			.thenReturn(false);
		
		when(this.ticketService.debitarComprador(authUsuarioTutor.getId()))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/passeio/{id}/iniciar", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoRegistrarLatLong() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Andamento.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		PasseioLatLong latLong = new PasseioLatLong();
		
		latLong.setId(1L);
		latLong.setLatitude("00000");
		latLong.setLongitude("00000");
		latLong.setPasseioId(passeio.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.registrarLatLong(Mockito.any(PasseioLatLong.class)))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(latLong);
		
		mockMvc.perform(post("/api/v1/passeio/lat-long")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistrarLatLong_DogwalkerDivergente() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Andamento.name());
		passeio.setDogwalkerId(999L);
		passeio.setTutorId(authUsuarioTutor.getId());
		
		PasseioLatLong latLong = new PasseioLatLong();
		
		latLong.setId(1L);
		latLong.setLatitude("00000");
		latLong.setLongitude("00000");
		latLong.setPasseioId(passeio.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.registrarLatLong(Mockito.any(PasseioLatLong.class)))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(latLong);
		
		mockMvc.perform(post("/api/v1/passeio/lat-long")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistrarLatLong_PasseioIdNaoInformado() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Andamento.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		PasseioLatLong latLong = new PasseioLatLong();
		
		latLong.setId(1L);
		latLong.setLatitude("00000");
		latLong.setLongitude("00000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.registrarLatLong(Mockito.any(PasseioLatLong.class)))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(latLong);
		
		mockMvc.perform(post("/api/v1/passeio/lat-long")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistrarLatLong_LatitudeNaoInformado() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Andamento.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		PasseioLatLong latLong = new PasseioLatLong();
		
		latLong.setId(1L);
		latLong.setLongitude("00000");
		latLong.setPasseioId(passeio.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.registrarLatLong(Mockito.any(PasseioLatLong.class)))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(latLong);
		
		mockMvc.perform(post("/api/v1/passeio/lat-long")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistrarLatLong_LongitudeNaoInformado() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Andamento.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		PasseioLatLong latLong = new PasseioLatLong();
		
		latLong.setId(1L);
		latLong.setLatitude("00000");
		latLong.setPasseioId(passeio.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.registrarLatLong(Mockito.any(PasseioLatLong.class)))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(latLong);
		
		mockMvc.perform(post("/api/v1/passeio/lat-long")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistrarLatLong_PasseioStatusDiferenteDeAndamento() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Finalizado.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		PasseioLatLong latLong = new PasseioLatLong();
		
		latLong.setId(1L);
		latLong.setLongitude("00000");
		latLong.setLatitude("00000");
		latLong.setPasseioId(passeio.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.registrarLatLong(Mockito.any(PasseioLatLong.class)))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(latLong);
		
		mockMvc.perform(post("/api/v1/passeio/lat-long")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistrarLatLong_ErroARegistrarLatLong() throws Exception {
		
		Passeio passeio = new Passeio();
		
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Andamento.name());
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		PasseioLatLong latLong = new PasseioLatLong();
		
		latLong.setId(1L);
		latLong.setLongitude("00000");
		latLong.setLatitude("00000");
		latLong.setPasseioId(passeio.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.registrarLatLong(Mockito.any(PasseioLatLong.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(latLong);
		
		mockMvc.perform(post("/api/v1/passeio/lat-long")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCreditarSaldo() throws Exception {
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.saldoService.creditarSaldo(1L))
			.thenReturn(true);
		
		mockMvc.perform(post("/api/v1/passeio/creditar-saldo/{passeioId}", 1L)
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCreditarSaldo_ErroAoCreditarSaldo() throws Exception {
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.saldoService.creditarSaldo(1L))
			.thenReturn(false);
		
		mockMvc.perform(post("/api/v1/passeio/creditar-saldo/{passeioId}", 1L)
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarForbidden_QuandoCreditarSaldo_UsuarioDogwalker() throws Exception {
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.saldoService.creditarSaldo(1L))
			.thenReturn(true);
		
		mockMvc.perform(post("/api/v1/passeio/creditar-saldo/{passeioId}", 1L)
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isForbidden());
		
	}
	
	@Test
	public void deveRetornarForbidden_QuandoCreditarSaldo_UsuarioTutor() throws Exception {
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.saldoService.creditarSaldo(1L))
			.thenReturn(true);
		
		mockMvc.perform(post("/api/v1/passeio/creditar-saldo/{passeioId}", 1L)
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isForbidden());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoObterPosicaoAtual() throws Exception {
		
		Passeio passeio = new Passeio();
		PasseioLatLong latLong = new PasseioLatLong();
		
		passeio.setId(2L);
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		latLong.setId(2L);
		latLong.setLatitude("00000");
		latLong.setLongitude("00000");
		latLong.setPasseioId(passeio.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.posicaoAtual(passeio.getId()))
			.thenReturn(latLong);
		
		mockMvc.perform(get("/api/v1/passeio/localizacao/posicao-atual/{passeioId}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoObterPosicaoAtual_PosicaoNaoExistente() throws Exception {
		
		Passeio passeio = new Passeio();
		PasseioLatLong latLong = new PasseioLatLong();
		
		passeio.setId(2L);
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		latLong.setId(2L);
		latLong.setLatitude("00000");
		latLong.setLongitude("00000");
		latLong.setPasseioId(passeio.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.posicaoAtual(passeio.getId()))
			.thenReturn(null);
		
		mockMvc.perform(get("/api/v1/passeio/localizacao/posicao-atual/{passeioId}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoObterPosicaoAtual_TutorDivergente() throws Exception {
		
		Passeio passeio = new Passeio();
		PasseioLatLong latLong = new PasseioLatLong();
		
		passeio.setId(2L);
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(999L);
		
		latLong.setId(2L);
		latLong.setLatitude("00000");
		latLong.setLongitude("00000");
		latLong.setPasseioId(passeio.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.posicaoAtual(passeio.getId()))
			.thenReturn(null);
		
		mockMvc.perform(get("/api/v1/passeio/localizacao/posicao-atual/{passeioId}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoObterPosicaoAtual_PasseioIdNaoExistente() throws Exception {
		
		Passeio passeio = new Passeio();
		PasseioLatLong latLong = new PasseioLatLong();
		
		passeio.setId(2L);
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		latLong.setId(2L);
		latLong.setLatitude("00000");
		latLong.setLongitude("00000");
		latLong.setPasseioId(0L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(null);
		
		when(this.passeioService.posicaoAtual(passeio.getId()))
			.thenReturn(latLong);
		
		mockMvc.perform(get("/api/v1/passeio/localizacao/posicao-atual/{passeioId}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoObterPosicaoCompleta() throws Exception {
		
		Passeio passeio = new Passeio();
		PasseioLatLong latLong = new PasseioLatLong();
		List<PasseioLatLong> listLatLong = new ArrayList<>();
		
		passeio.setId(2L);
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		latLong.setId(2L);
		latLong.setLatitude("00000");
		latLong.setLongitude("00000");
		latLong.setPasseioId(passeio.getId());
		
		listLatLong.add(latLong);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.posicaoCompleta(passeio.getId()))
			.thenReturn(listLatLong);
		
		mockMvc.perform(get("/api/v1/passeio/localizacao/posicao-completa/{passeioId}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoObterPosicaoCompleta_PosicaoNaoExistente() throws Exception {
		
		Passeio passeio = new Passeio();
		PasseioLatLong latLong = new PasseioLatLong();
		List<PasseioLatLong> listLatLong = new ArrayList<>();
		
		passeio.setId(2L);
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		latLong.setId(2L);
		latLong.setLatitude("00000");
		latLong.setLongitude("00000");
		latLong.setPasseioId(passeio.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.posicaoCompleta(passeio.getId()))
			.thenReturn(listLatLong);
		
		mockMvc.perform(get("/api/v1/passeio/localizacao/posicao-completa/{passeioId}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoObterPosicaoCompleta_TutorDivergente() throws Exception {
		
		Passeio passeio = new Passeio();
		PasseioLatLong latLong = new PasseioLatLong();
		List<PasseioLatLong> listLatLong = new ArrayList<>();
		
		passeio.setId(2L);
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(9999L);
		
		latLong.setId(2L);
		latLong.setLatitude("00000");
		latLong.setLongitude("00000");
		latLong.setPasseioId(passeio.getId());
		
		listLatLong.add(latLong);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.passeioService.posicaoCompleta(passeio.getId()))
			.thenReturn(listLatLong);
		
		mockMvc.perform(get("/api/v1/passeio/localizacao/posicao-completa/{passeioId}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoObterPosicaoCompleta_PasseioIdNaoExistente() throws Exception {
		
		Passeio passeio = new Passeio();
		PasseioLatLong latLong = new PasseioLatLong();
		List<PasseioLatLong> listLatLong = new ArrayList<>();
		
		passeio.setId(2L);
		passeio.setDogwalkerId(authUsuarioDogwalker.getId());
		passeio.setTutorId(authUsuarioTutor.getId());
		
		latLong.setId(2L);
		latLong.setLatitude("00000");
		latLong.setLongitude("00000");
		latLong.setPasseioId(0L);
		
		listLatLong.add(latLong);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(null);
		
		when(this.passeioService.posicaoCompleta(passeio.getId()))
			.thenReturn(listLatLong);
		
		mockMvc.perform(get("/api/v1/passeio/localizacao/posicao-completa/{passeioId}", passeio.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
}
