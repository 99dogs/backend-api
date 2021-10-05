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
import br.dogs.com.model.entities.Qualificacao;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.QualificacaoService;
import br.dogs.com.service.UsuarioService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class QualificacaoRestControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private QualificacaoRestController qualificacaoRestController;
	
	@MockBean
	private QualificacaoService qualificacaoService;
	
	@MockBean
	private UsuarioService usuarioService;
	
	private Usuario authUsuarioAdmin = new Usuario();
	
	private Usuario authUsuarioTutor = new Usuario();
	
	private Usuario authUsuarioDogwalker = new Usuario();
	
	private String tokenUsuarioAdmin;
	
	private String tokenUsuarioTutor;
	
	private String tokenUsuarioDogwalker;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void startMocks(){
		qualificacaoRestController = context.getBean(QualificacaoRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(qualificacaoRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(qualificacaoService);
		standaloneSetup(usuarioService);
		
		authUsuarioAdmin.setId(1L);
		authUsuarioAdmin.setEmail("admin");
		authUsuarioAdmin.setTipo(TipoUsuario.ADMIN.name());
		
		authUsuarioTutor.setId(1L);
		authUsuarioTutor.setEmail("tutor");
		authUsuarioTutor.setTipo(TipoUsuario.TUTOR.name());
		
		authUsuarioDogwalker.setId(1L);
		authUsuarioDogwalker.setEmail("dogwalker");
		authUsuarioDogwalker.setTipo(TipoUsuario.DOGWALKER.name());
		
		tokenUsuarioAdmin = jwtTokenProvider.createToken(authUsuarioAdmin);
		tokenUsuarioTutor = jwtTokenProvider.createToken(authUsuarioTutor);
		tokenUsuarioDogwalker = jwtTokenProvider.createToken(authUsuarioDogwalker);
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTodos() throws Exception {
		
		List<Qualificacao> qualificacoes = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.buscarTodos(authUsuarioDogwalker.getId()))
			.thenReturn(qualificacoes);
		
		mockMvc.perform(get("/api/v1/qualificacao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarTodos_ThrowException() throws Exception {
		
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.buscarTodos(authUsuarioDogwalker.getId()))
			.thenAnswer((t) -> { throw new Exception("bad request"); });
		
		mockMvc.perform(get("/api/v1/qualificacao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCadastrarQualificacao() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.cadastrar(Mockito.any(Qualificacao.class)))
			.thenReturn(qualificacao);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/qualificacao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarForbidden_QuandoCadastrarQualificacao_UsuarioAdmin() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.qualificacaoService.cadastrar(Mockito.any(Qualificacao.class)))
			.thenReturn(qualificacao);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/qualificacao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isForbidden());
		
	}
	
	@Test
	public void deveRetornarForbidden_QuandoCadastrarQualificacao_UsuarioTutor() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.qualificacaoService.cadastrar(Mockito.any(Qualificacao.class)))
			.thenReturn(qualificacao);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/qualificacao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isForbidden());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarQualificacao_TituloVazio() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.cadastrar(Mockito.any(Qualificacao.class)))
			.thenReturn(qualificacao);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", "");
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/qualificacao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarQualificacao_ModalidadeVazio() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.cadastrar(Mockito.any(Qualificacao.class)))
			.thenReturn(qualificacao);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", "");
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/qualificacao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarQualificacao_DescricaoVazio() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.cadastrar(Mockito.any(Qualificacao.class)))
			.thenReturn(qualificacao);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", "");
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/qualificacao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarQualificacao_ErroAoCadastrarQualificacao() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(0L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.cadastrar(Mockito.any(Qualificacao.class)))
			.thenReturn(qualificacao);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/qualificacao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarQualificacao_ThrowException() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(0L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.cadastrar(Mockito.any(Qualificacao.class)))
			.thenAnswer((t) -> { throw new Exception("bad request"); });
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/qualificacao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoAlterarQualificacao() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		when(this.qualificacaoService.alterar(Mockito.any(Qualificacao.class)))
			.thenReturn(true);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoAlterarQualificacao_QualificacaoNaoExistente() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(null);
		
		when(this.qualificacaoService.alterar(Mockito.any(Qualificacao.class)))
			.thenReturn(true);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoAlterarQualificacao_UsuarioDivergente() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(999L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		when(this.qualificacaoService.alterar(Mockito.any(Qualificacao.class)))
			.thenReturn(true);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarForbidden_QuandoAlterarQualificacao_UsuarioAdmin() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		when(this.qualificacaoService.alterar(Mockito.any(Qualificacao.class)))
			.thenReturn(true);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isForbidden());
		
	}
	
	@Test
	public void deveRetornarForbidden_QuandoAlterarQualificacao_UsuarioTutor() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		when(this.qualificacaoService.alterar(Mockito.any(Qualificacao.class)))
			.thenReturn(true);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isForbidden());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarQualificacao_TituloVazio() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.alterar(Mockito.any(Qualificacao.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", "");
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarQualificacao_ModalidadeVazio() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.alterar(Mockito.any(Qualificacao.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", "");
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarQualificacao_DescricaoVazio() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.alterar(Mockito.any(Qualificacao.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", "");
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarQualificacao_ErroAoAlterarQualificacao() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		when(this.qualificacaoService.alterar(Mockito.any(Qualificacao.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarQualificacao_ThrowException() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		when(this.qualificacaoService.alterar(Mockito.any(Qualificacao.class)))
			.thenAnswer((t) -> { throw new Exception("bad request"); });
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("titulo", qualificacao.getTitulo());
		dados.put("modalidade", qualificacao.getModalidade());
		dados.put("descricao", qualificacao.getDescricao());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarPorId() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
		.thenReturn(authUsuarioDogwalker);
	
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		mockMvc.perform(get("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoBuscarPorId_QualificacaoNaoExistente() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(0L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
	
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		mockMvc.perform(get("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoBuscarPorId_UsuarioDivergente() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(999L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
	
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		mockMvc.perform(get("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarPorId_ThrowException() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
	
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenAnswer((t) -> { throw new Exception("bad request"); });
		
		mockMvc.perform(get("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoDeletarUmaQualificacao() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
	
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		when(this.qualificacaoService.deletarPorId(qualificacao.getId()))
			.thenReturn(true);
		
		mockMvc.perform(delete("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoDeletarUmaQualificacao_QualificacaoNaoExistente() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(0L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
	
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		when(this.qualificacaoService.deletarPorId(qualificacao.getId()))
			.thenReturn(true);
		
		mockMvc.perform(delete("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoDeletarUmaQualificacao_UsuarioDivergente() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(999L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
	
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		when(this.qualificacaoService.deletarPorId(qualificacao.getId()))
			.thenReturn(true);
		
		mockMvc.perform(delete("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoDeletarUmaQualificacao_ErroAoDeletarQualificacao() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
	
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		when(this.qualificacaoService.deletarPorId(qualificacao.getId()))
			.thenReturn(false);
		
		mockMvc.perform(delete("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoDeletarUmaQualificacao_ThrowException() throws Exception {
		
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
	
		when(this.qualificacaoService.buscarPorId(qualificacao.getId()))
			.thenReturn(qualificacao);
		
		when(this.qualificacaoService.deletarPorId(qualificacao.getId()))
			.thenAnswer((t) -> { throw new Exception("bad request"); });
		
		mockMvc.perform(delete("/api/v1/qualificacao/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarTodosPorDogwalker_ThrowException() throws Exception {
		
		List<Qualificacao> qualificacoes = new ArrayList<>();
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		qualificacoes.add(qualificacao);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
	
		
		when(this.qualificacaoService.buscarTodos(authUsuarioDogwalker.getId()))
			.thenAnswer((t) -> { throw new Exception("bad request"); });
		
		mockMvc.perform(get("/api/v1/qualificacao/dogwalker/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTodosPorDogwalker() throws Exception {
		
		List<Qualificacao> qualificacoes = new ArrayList<>();
		Qualificacao qualificacao = new Qualificacao();
		
		qualificacao.setId(1L);
		qualificacao.setModalidade("Curso");
		qualificacao.setTitulo("Adestrador");
		qualificacao.setDescricao("descricao");
		qualificacao.setUsuarioId(authUsuarioDogwalker.getId());
		
		qualificacoes.add(qualificacao);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
	
		
		when(this.qualificacaoService.buscarTodos(authUsuarioDogwalker.getId()))
			.thenReturn(qualificacoes);
		
		mockMvc.perform(get("/api/v1/qualificacao/dogwalker/{id}", qualificacao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
}
