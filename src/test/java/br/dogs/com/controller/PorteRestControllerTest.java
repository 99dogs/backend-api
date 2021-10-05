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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import br.dogs.com.model.entities.Porte;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.PorteService;
import br.dogs.com.service.UsuarioService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class PorteRestControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private PorteRestController porteRestController;
	
	@MockBean
	private PorteService porteService;
	
	@MockBean
	private UsuarioService usuarioService;
	
	private Usuario authUsuarioAdmin = new Usuario();
	
	private Usuario authUsuarioTutor = new Usuario();
		
	private String tokenUsuarioAdmin;
	
	private String tokenUsuarioTutor;
		
	ObjectMapper objectMapper = new ObjectMapper();
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void startMocks(){
		porteRestController = context.getBean(PorteRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(porteRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(porteService);
		standaloneSetup(usuarioService);
		
		authUsuarioAdmin.setId(1L);
		authUsuarioAdmin.setEmail("admin");
		authUsuarioAdmin.setTipo(TipoUsuario.ADMIN.name());
		
		authUsuarioTutor.setId(1L);
		authUsuarioTutor.setEmail("tutor");
		authUsuarioTutor.setTipo(TipoUsuario.TUTOR.name());
		
		tokenUsuarioAdmin = jwtTokenProvider.createToken(authUsuarioAdmin);
		tokenUsuarioTutor = jwtTokenProvider.createToken(authUsuarioTutor);
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTodos() throws Exception {
		
		List<Porte> portes = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarTodos())
			.thenReturn(portes);
		
		mockMvc.perform(get("/api/v1/porte")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoBuscarTodos_ThrowException() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.porteService.buscarTodos())
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		mockMvc.perform(get("/api/v1/porte")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCadastrarUmPorte() throws Exception {
		
		Porte porte = new Porte();
		
		porte.setId(1L);
		porte.setNome("porte test");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.porteService.cadastrar(Mockito.any(Porte.class)))
			.thenReturn(porte);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", "porte test");
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/porte")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarUmPorte_NomeNulo() throws Exception {
		
		Porte porte = new Porte();
		
		porte.setId(1L);
		porte.setNome("porte test");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.porteService.cadastrar(Mockito.any(Porte.class)))
			.thenReturn(porte);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", null);
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/porte")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarUmPorte_NomeVazio() throws Exception {
		
		Porte porte = new Porte();
		
		porte.setId(1L);
		porte.setNome("porte test");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.porteService.cadastrar(Mockito.any(Porte.class)))
			.thenReturn(porte);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", "");
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/porte")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarUmPorte_ErroAoCadastrarPorte() throws Exception {
		
		Porte porte = new Porte();
		
		porte.setId(0L);
		porte.setNome("porte test");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.porteService.cadastrar(Mockito.any(Porte.class)))
			.thenReturn(porte);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", "porte test");
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/porte")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoAlterarUmPorte() throws Exception {
		
		Porte porte = new Porte();
		
		porte.setId(1L);
		porte.setNome("porte test");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		when(this.porteService.alterar(Mockito.any(Porte.class)))
			.thenReturn(true);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", "porte test");
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/porte/{id}", porte.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarUmPorte_NomeNulo() throws Exception {
		
		Porte porte = new Porte();
		
		porte.setId(1L);
		porte.setNome("porte test");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
	
		when(this.porteService.alterar(Mockito.any(Porte.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", null);
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/porte/{id}", porte.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarUmPorte_NomeVazio() throws Exception {
		
		Porte porte = new Porte();
		
		porte.setId(1L);
		porte.setNome("porte test");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.porteService.buscarPorId(porte.getId()))
		.thenReturn(porte);
	
		when(this.porteService.alterar(Mockito.any(Porte.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", "");
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/porte/{id}", porte.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoAlterarUmPorte_PorteNaoExistente() throws Exception {
		
		Porte porte = new Porte();
		
		porte.setId(0L);
		porte.setNome("porte test");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.porteService.buscarPorId(porte.getId()))
		.thenReturn(porte);
	
		when(this.porteService.alterar(Mockito.any(Porte.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", "porte test");
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/porte/{id}", porte.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarUmPorte_ErroAoAlterarPorte() throws Exception {
		
		Porte porte = new Porte();
		
		porte.setId(1L);
		porte.setNome("porte test");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.porteService.buscarPorId(porte.getId()))
			.thenReturn(porte);
	
		when(this.porteService.alterar(Mockito.any(Porte.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", "porte test");
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/porte/{id}", porte.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoDeletarUmPorte() throws Exception {
		
		Porte porte = new Porte();
		
		porte.setId(1L);
		porte.setNome("porte test");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.porteService.buscarPorId(porte.getId()))
		.thenReturn(porte);
	
		when(this.porteService.deletarPorId(porte.getId()))
			.thenReturn(true);
		
		mockMvc.perform(delete("/api/v1/porte/{id}", porte.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoDeletarUmPorte_PorteNaoExistente() throws Exception {
		
		Porte porte = new Porte();
		
		porte.setId(0L);
		porte.setNome("porte test");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.porteService.buscarPorId(porte.getId()))
		.thenReturn(porte);
	
		when(this.porteService.deletarPorId(porte.getId()))
			.thenReturn(false);
		
		mockMvc.perform(delete("/api/v1/porte/{id}", porte.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoDeletarUmPorte_ErroAoDeletarOPorte() throws Exception {
		
		Porte porte = new Porte();
		
		porte.setId(1L);
		porte.setNome("porte test");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.porteService.buscarPorId(porte.getId()))
		.thenReturn(porte);
	
		when(this.porteService.deletarPorId(porte.getId()))
			.thenReturn(false);
		
		mockMvc.perform(delete("/api/v1/porte/{id}", porte.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
}
