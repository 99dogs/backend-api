package br.dogs.com.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Time;
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
import br.dogs.com.model.entities.ConfiguracaoHorario;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.ConfiguracaoHorarioService;
import br.dogs.com.service.UsuarioService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class ConfiguracaoHorarioRestControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private ConfiguracaoHorarioRestController configuracaoHorarioRestController;
	
	@MockBean
	private ConfiguracaoHorarioService configuracaoHorarioService;
	
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
		configuracaoHorarioRestController = context.getBean(ConfiguracaoHorarioRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(configuracaoHorarioRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(configuracaoHorarioService);
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
		
		List<ConfiguracaoHorario> horarios = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarTodos(authUsuarioDogwalker.getId()))
			.thenReturn(horarios);
		
		mockMvc.perform(get("/api/v1/configuracao-horario")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoBuscarTodos_UsuarioNaoPermitido() throws Exception {
		
		List<ConfiguracaoHorario> horarios = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.configuracaoHorarioService.buscarTodos(authUsuarioTutor.getId()))
			.thenReturn(horarios);
		
		mockMvc.perform(get("/api/v1/configuracao-horario")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarPorId() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorId(horario.getId()))
			.thenReturn(horario);
		
		mockMvc.perform(get("/api/v1/configuracao-horario/{id}", horario.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoBuscarPorId() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorId(horario.getId()))
			.thenReturn(new ConfiguracaoHorario());
		
		mockMvc.perform(get("/api/v1/configuracao-horario/{id}", horario.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoBuscarPorId_UsuarioDivergente() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setUsuarioId(2L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorId(horario.getId()))
			.thenReturn(horario);
		
		mockMvc.perform(get("/api/v1/configuracao-horario/{id}", horario.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCadastrarConfiguracaoHorario() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(0);
		horario.setHoraInicio(Time.valueOf("08:00:00"));
		horario.setHoraFinal(Time.valueOf("20:00:00"));
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorDiaSemana(horario.getDiaSemana(), horario.getUsuarioId()))
			.thenReturn(null);
		
		when(this.configuracaoHorarioService.cadastrar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(horario);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(post("/api/v1/configuracao-horario")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoCadastrarConfiguracaoHorario_UsuarioNaoPermitido() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(0);
		horario.setHoraInicio(Time.valueOf("08:00:00"));
		horario.setHoraFinal(Time.valueOf("20:00:00"));
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorDiaSemana(horario.getDiaSemana(), horario.getUsuarioId()))
			.thenReturn(null);
		
		when(this.configuracaoHorarioService.cadastrar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(horario);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(post("/api/v1/configuracao-horario")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarConfiguracaoHorario_HorarioJaCriadoParaODiaDaSemana() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(0);
		horario.setHoraInicio(Time.valueOf("08:00:00"));
		horario.setHoraFinal(Time.valueOf("20:00:00"));
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorDiaSemana(horario.getDiaSemana(), horario.getUsuarioId()))
			.thenReturn(horario);
		
		when(this.configuracaoHorarioService.cadastrar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(horario);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(post("/api/v1/configuracao-horario")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarConfiguracaoHorario_DiaSemanaMenorQueZero() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(-1);
		horario.setHoraInicio(Time.valueOf("08:00:00"));
		horario.setHoraFinal(Time.valueOf("20:00:00"));
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorDiaSemana(horario.getDiaSemana(), horario.getUsuarioId()))
			.thenReturn(null);
		
		when(this.configuracaoHorarioService.cadastrar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(horario);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(post("/api/v1/configuracao-horario")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarConfiguracaoHorario_DiaSemanaMaiorQueSeis() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(7);
		horario.setHoraInicio(Time.valueOf("08:00:00"));
		horario.setHoraFinal(Time.valueOf("20:00:00"));
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorDiaSemana(horario.getDiaSemana(), horario.getUsuarioId()))
			.thenReturn(null);
		
		when(this.configuracaoHorarioService.cadastrar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(horario);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(post("/api/v1/configuracao-horario")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarConfiguracaoHorario_HoraInicioNulo() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(0);
		horario.setHoraInicio(null);
		horario.setHoraFinal(Time.valueOf("20:00:00"));
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorDiaSemana(horario.getDiaSemana(), horario.getUsuarioId()))
			.thenReturn(null);
		
		when(this.configuracaoHorarioService.cadastrar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(horario);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(post("/api/v1/configuracao-horario")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarConfiguracaoHorario_HoraFinalNulo() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(0);
		horario.setHoraInicio(Time.valueOf("08:00:00"));
		horario.setHoraFinal(null);
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorDiaSemana(horario.getDiaSemana(), horario.getUsuarioId()))
			.thenReturn(null);
		
		when(this.configuracaoHorarioService.cadastrar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(horario);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(post("/api/v1/configuracao-horario")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoAlterarConfiguracaoHorario() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(0);
		horario.setHoraInicio(Time.valueOf("08:00:00"));
		horario.setHoraFinal(Time.valueOf("20:00:00"));
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorId(horario.getId()))
			.thenReturn(horario);
		
		when(this.configuracaoHorarioService.alterar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(put("/api/v1/configuracao-horario/{id}", horario.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoAlterarConfiguracaoHorario_UsuarioNaoPermitido() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(0);
		horario.setHoraInicio(Time.valueOf("08:00:00"));
		horario.setHoraFinal(Time.valueOf("20:00:00"));
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorId(horario.getId()))
			.thenReturn(horario);
		
		when(this.configuracaoHorarioService.alterar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(put("/api/v1/configuracao-horario/{id}", horario.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoAlterarConfiguracaoHorario_HorarioNaoExistente() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(0);
		horario.setHoraInicio(Time.valueOf("08:00:00"));
		horario.setHoraFinal(Time.valueOf("20:00:00"));
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorId(horario.getId()))
			.thenReturn(null);
		
		when(this.configuracaoHorarioService.alterar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(put("/api/v1/configuracao-horario/{id}", horario.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoAlterarConfiguracaoHorario_UsuarioDivergente() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(0);
		horario.setHoraInicio(Time.valueOf("08:00:00"));
		horario.setHoraFinal(Time.valueOf("20:00:00"));
		horario.setUsuarioId(999L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorId(horario.getId()))
			.thenReturn(horario);
		
		when(this.configuracaoHorarioService.alterar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(put("/api/v1/configuracao-horario/{id}", horario.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarConfiguracaoHorario_DiaSemanaMenorQueZero() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(-1);
		horario.setHoraInicio(Time.valueOf("08:00:00"));
		horario.setHoraFinal(Time.valueOf("20:00:00"));
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorId(horario.getId()))
			.thenReturn(horario);
		
		when(this.configuracaoHorarioService.alterar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(put("/api/v1/configuracao-horario/{id}", horario.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarConfiguracaoHorario_DiaSemanaMaiorQueSeis() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(7);
		horario.setHoraInicio(Time.valueOf("08:00:00"));
		horario.setHoraFinal(Time.valueOf("20:00:00"));
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorId(horario.getId()))
			.thenReturn(horario);
		
		when(this.configuracaoHorarioService.alterar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(put("/api/v1/configuracao-horario/{id}", horario.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarConfiguracaoHorario_HoraInicioNulo() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(0);
		horario.setHoraInicio(null);
		horario.setHoraFinal(Time.valueOf("20:00:00"));
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorId(horario.getId()))
			.thenReturn(horario);
		
		when(this.configuracaoHorarioService.alterar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(put("/api/v1/configuracao-horario/{id}", horario.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarConfiguracaoHorario_HoraFinalNulo() throws Exception {
		
		ConfiguracaoHorario horario = new ConfiguracaoHorario();
		
		horario.setId(1L);
		horario.setDiaSemana(0);
		horario.setHoraInicio(Time.valueOf("08:00:00"));
		horario.setHoraFinal(null);
		horario.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.configuracaoHorarioService.buscarPorId(horario.getId()))
			.thenReturn(horario);
		
		when(this.configuracaoHorarioService.alterar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(horario);
		
		mockMvc.perform(put("/api/v1/configuracao-horario/{id}", horario.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
}
