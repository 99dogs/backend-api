package br.dogs.com.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import br.dogs.com.helper.TipoUsuario;
import br.dogs.com.model.entities.Cachorro;
import br.dogs.com.model.entities.Porte;
import br.dogs.com.model.entities.Raca;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.CachorroService;
import br.dogs.com.service.PorteService;
import br.dogs.com.service.RacaService;
import br.dogs.com.service.UsuarioService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class CachorroRestControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@InjectMocks
	private CachorroRestController cachorroRestController;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@MockBean
	private UsuarioService usuarioService;
	
	@MockBean
	private CachorroService cachorroService;
	
	@MockBean
	private RacaService racaService;
	
	@MockBean
	private PorteService porteService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	private Usuario authUsuarioTutor = new Usuario();
	
	private String tokenUsuarioTutor;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void startMocks(){
		cachorroRestController = context.getBean(CachorroRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(cachorroRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(cachorroService);
		standaloneSetup(racaService);
		standaloneSetup(porteService);
		standaloneSetup(usuarioService);
		
		authUsuarioTutor.setId(1L);
		authUsuarioTutor.setEmail("tutor");
		authUsuarioTutor.setTipo(TipoUsuario.TUTOR.name());
		
		tokenUsuarioTutor = jwtTokenProvider.createToken(authUsuarioTutor);
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTodos() throws Exception {
		
		List<Cachorro> cachorros = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.cachorroService.buscarTodos(authUsuarioTutor.getId()))
			.thenReturn(cachorros);
		
		mockMvc.perform(get("/api/v1/cachorro")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarCachorroPorId() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		
		cachorro.setId(1L);
		cachorro.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		mockMvc.perform(get("/api/v1/cachorro/{id}", cachorro.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCadastrarUmCachorro() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setNome("cachorro test");
		cachorro.setComportamento("comportamento test");
		cachorro.setRacaId(raca.getId());
		cachorro.setRaca(raca);
		cachorro.setPorteId(porte.getId());
		cachorro.setPorte(porte);
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.cachorroService.cadastrar(Mockito.any(Cachorro.class)))
			.thenReturn(cachorro);
		
		String json = objectMapper.writeValueAsString(cachorro);
		
		mockMvc.perform(post("/api/v1/cachorro")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarUmCachorro_CachorroSemNome() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setComportamento("comportamento test");
		cachorro.setRacaId(raca.getId());
		cachorro.setRaca(raca);
		cachorro.setPorteId(porte.getId());
		cachorro.setPorte(porte);
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.cachorroService.cadastrar(Mockito.any(Cachorro.class)))
			.thenReturn(cachorro);
		
		String json = objectMapper.writeValueAsString(cachorro);
		
		mockMvc.perform(post("/api/v1/cachorro")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarUmCachorro_CachorroSemComportamento() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setNome("cachorro test");
		cachorro.setRacaId(raca.getId());
		cachorro.setRaca(raca);
		cachorro.setPorteId(porte.getId());
		cachorro.setPorte(porte);
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.cachorroService.cadastrar(Mockito.any(Cachorro.class)))
			.thenReturn(cachorro);
		
		String json = objectMapper.writeValueAsString(cachorro);
		
		mockMvc.perform(post("/api/v1/cachorro")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarUmCachorro_CachorroSemPorte() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setNome("nome test");
		cachorro.setComportamento("comportamento test");
		cachorro.setRacaId(raca.getId());
		cachorro.setRaca(raca);
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.cachorroService.cadastrar(Mockito.any(Cachorro.class)))
			.thenReturn(cachorro);
		
		String json = objectMapper.writeValueAsString(cachorro);
		
		mockMvc.perform(post("/api/v1/cachorro")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarUmCachorro_CachorroSemRaca() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setNome("nome test");
		cachorro.setComportamento("comportamento test");
		cachorro.setPorte(porte);
		cachorro.setPorteId(porte.getId());
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.cachorroService.cadastrar(Mockito.any(Cachorro.class)))
			.thenReturn(cachorro);
		
		String json = objectMapper.writeValueAsString(cachorro);
		
		mockMvc.perform(post("/api/v1/cachorro")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoAlterarUmCachorro() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setNome("cachorro test update");
		cachorro.setComportamento("comportamento test update");
		cachorro.setRacaId(raca.getId());
		cachorro.setRaca(raca);
		cachorro.setPorteId(porte.getId());
		cachorro.setPorte(porte);
		cachorro.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.cachorroService.alterar(Mockito.any(Cachorro.class)))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(cachorro);
		
		mockMvc.perform(put("/api/v1/cachorro/{id}", cachorro.getId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarUmCachorro_CachorroSemNome() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setComportamento("comportamento test");
		cachorro.setRacaId(raca.getId());
		cachorro.setRaca(raca);
		cachorro.setPorteId(porte.getId());
		cachorro.setPorte(porte);
		cachorro.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.cachorroService.alterar(Mockito.any(Cachorro.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(cachorro);
		
		mockMvc.perform(put("/api/v1/cachorro/{id}", cachorro.getId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarUmCachorro_CachorroSemComportamento() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setNome("nome test");
		cachorro.setRacaId(raca.getId());
		cachorro.setRaca(raca);
		cachorro.setPorteId(porte.getId());
		cachorro.setPorte(porte);
		cachorro.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.cachorroService.alterar(Mockito.any(Cachorro.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(cachorro);
		
		mockMvc.perform(put("/api/v1/cachorro/{id}", cachorro.getId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarUmCachorro_CachorroSemPorte() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setNome("nome test");
		cachorro.setComportamento("comportamento test");
		cachorro.setRacaId(raca.getId());
		cachorro.setRaca(raca);
		cachorro.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.cachorroService.alterar(Mockito.any(Cachorro.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(cachorro);
		
		mockMvc.perform(put("/api/v1/cachorro/{id}", cachorro.getId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarUmCachorro_CachorroSemRaca() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setNome("nome test");
		cachorro.setComportamento("comportamento test");
		cachorro.setPorteId(porte.getId());
		cachorro.setPorte(porte);
		cachorro.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.cachorroService.alterar(Mockito.any(Cachorro.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(cachorro);
		
		mockMvc.perform(put("/api/v1/cachorro/{id}", cachorro.getId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoAlterarUmCachorro_CachorroNaoExistente() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setNome("nome test");
		cachorro.setComportamento("comportamento test");
		cachorro.setRacaId(raca.getId());
		cachorro.setRaca(raca);
		cachorro.setPorteId(porte.getId());
		cachorro.setPorte(porte);
		cachorro.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(new Cachorro());
		
		when(this.cachorroService.alterar(Mockito.any(Cachorro.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(cachorro);
		
		mockMvc.perform(put("/api/v1/cachorro/{id}", cachorro.getId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoAlterarUmCachorro_TutorDivergente() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setNome("nome test");
		cachorro.setComportamento("comportamento test");
		cachorro.setRacaId(raca.getId());
		cachorro.setRaca(raca);
		cachorro.setPorteId(porte.getId());
		cachorro.setPorte(porte);
		cachorro.setUsuarioId(0L);
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.cachorroService.alterar(Mockito.any(Cachorro.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(cachorro);
		
		mockMvc.perform(put("/api/v1/cachorro/{id}", cachorro.getId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoDeletarUmCachorro() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setNome("nome test");
		cachorro.setComportamento("comportamento test");
		cachorro.setRacaId(raca.getId());
		cachorro.setRaca(raca);
		cachorro.setPorteId(porte.getId());
		cachorro.setPorte(porte);
		cachorro.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
				
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.cachorroService.deletarPorId(cachorro.getId()))
			.thenReturn(true);
				
		mockMvc.perform(delete("/api/v1/cachorro/{id}", cachorro.getId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoDeletarUmCachorro_CachorroNaoExistente() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setNome("nome test");
		cachorro.setComportamento("comportamento test");
		cachorro.setRacaId(raca.getId());
		cachorro.setRaca(raca);
		cachorro.setPorteId(porte.getId());
		cachorro.setPorte(porte);
		cachorro.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
				
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(new Cachorro());
		
		when(this.cachorroService.deletarPorId(cachorro.getId()))
			.thenReturn(true);
				
		mockMvc.perform(delete("/api/v1/cachorro/{id}", cachorro.getId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoDeletarUmCachorro_TutorDivergente() throws Exception {
		
		Cachorro cachorro = new Cachorro();
		Porte porte = new Porte();
		Raca raca = new Raca();
		
		porte.setId(1L);
		raca.setId(1L);
		
		cachorro.setId(1L);
		cachorro.setNome("nome test");
		cachorro.setComportamento("comportamento test");
		cachorro.setRacaId(raca.getId());
		cachorro.setRaca(raca);
		cachorro.setPorteId(porte.getId());
		cachorro.setPorte(porte);
		cachorro.setUsuarioId(0L);
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
				
		when(this.cachorroService.buscarPorId(cachorro.getId()))
			.thenReturn(cachorro);
		
		when(this.cachorroService.deletarPorId(cachorro.getId()))
			.thenReturn(true);
				
		mockMvc.perform(delete("/api/v1/cachorro/{id}", cachorro.getId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
                .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(status().isUnauthorized());
		
	}
	
}
