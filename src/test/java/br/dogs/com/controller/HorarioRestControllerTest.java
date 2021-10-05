package br.dogs.com.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import br.dogs.com.model.dto.Disponibilidade;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.HorarioService;
import br.dogs.com.service.UsuarioService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class HorarioRestControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private HorarioRestController horarioRestController;
	
	@MockBean
	private HorarioService horarioService;
	
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
		horarioRestController = context.getBean(HorarioRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(horarioRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(horarioService);
		standaloneSetup(usuarioService);
		
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
	public void deveRetornarSucesso_QuandoVerificarDisponibilidade_HorarioDisponivel() throws Exception {
		
		Disponibilidade disponibilidade = new Disponibilidade();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00");
		String datahora = df.format(new Date(System.currentTimeMillis() + 1 * (3600*1000))).toString();

		disponibilidade.setDatahora(datahora);
		disponibilidade.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(disponibilidade.getDatahora(), disponibilidade.getUsuarioId()))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(disponibilidade);
		
		mockMvc.perform(post("/api/v1/horario/disponibilidade")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoVerificarDisponibilidade_UsuarioAdmin() throws Exception {
		
		Disponibilidade disponibilidade = new Disponibilidade();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00");
		String datahora = df.format(new Date(System.currentTimeMillis() + 1 * (3600*1000))).toString();

		disponibilidade.setDatahora(datahora);
		disponibilidade.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(disponibilidade.getDatahora(), disponibilidade.getUsuarioId()))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(disponibilidade);
		
		mockMvc.perform(post("/api/v1/horario/disponibilidade")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoVerificarDisponibilidade_UsuarioDogwalker() throws Exception {
		
		Disponibilidade disponibilidade = new Disponibilidade();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00");
		String datahora = df.format(new Date(System.currentTimeMillis() + 1 * (3600*1000))).toString();

		disponibilidade.setDatahora(datahora);
		disponibilidade.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(disponibilidade.getDatahora(), disponibilidade.getUsuarioId()))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(disponibilidade);
		
		mockMvc.perform(post("/api/v1/horario/disponibilidade")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoVerificarDisponibilidade_DataHoraNoPassado() throws Exception {
		
		Disponibilidade disponibilidade = new Disponibilidade();
		
		disponibilidade.setDatahora("2020-01-01T00:00:00");
		disponibilidade.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(disponibilidade.getDatahora(), disponibilidade.getUsuarioId()))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(disponibilidade);
		
		mockMvc.perform(post("/api/v1/horario/disponibilidade")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoVerificarDisponibilidade_UsuarioComTipoInvalido() throws Exception {
		
		Disponibilidade disponibilidade = new Disponibilidade();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00");
		String datahora = df.format(new Date(System.currentTimeMillis() + 1 * (3600*1000))).toString();

		disponibilidade.setDatahora(datahora);
		disponibilidade.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(authUsuarioTutor);
		
		when(this.horarioService.verificarDisponibilidade(disponibilidade.getDatahora(), disponibilidade.getUsuarioId()))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(disponibilidade);
		
		mockMvc.perform(post("/api/v1/horario/disponibilidade")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoVerificarDisponibilidade_HorarioIndisponivel() throws Exception {
		
		Disponibilidade disponibilidade = new Disponibilidade();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00");
		String datahora = df.format(new Date(System.currentTimeMillis() + 1 * (3600*1000))).toString();

		disponibilidade.setDatahora(datahora);
		disponibilidade.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(disponibilidade.getDatahora(), disponibilidade.getUsuarioId()))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(disponibilidade);
		
		mockMvc.perform(post("/api/v1/horario/disponibilidade")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoVerificarDisponibilidade_DatahoraVazio() throws Exception {
		
		Disponibilidade disponibilidade = new Disponibilidade();
		
		disponibilidade.setDatahora("");
		disponibilidade.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(disponibilidade.getDatahora(), disponibilidade.getUsuarioId()))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(disponibilidade);
		
		mockMvc.perform(post("/api/v1/horario/disponibilidade")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoVerificarDisponibilidade_UsuarioNaoExistente() throws Exception {
		
		Disponibilidade disponibilidade = new Disponibilidade();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00");
		String datahora = df.format(new Date(System.currentTimeMillis() + 1 * (3600*1000))).toString();

		disponibilidade.setDatahora(datahora);
		disponibilidade.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(null);
		
		when(this.horarioService.verificarDisponibilidade(disponibilidade.getDatahora(), disponibilidade.getUsuarioId()))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(disponibilidade);
		
		mockMvc.perform(post("/api/v1/horario/disponibilidade")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoVerificarDisponibilidade_UsuarioInvalido() throws Exception {
		
		Disponibilidade disponibilidade = new Disponibilidade();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00");
		String datahora = df.format(new Date(System.currentTimeMillis() + 1 * (3600*1000))).toString();

		disponibilidade.setDatahora(datahora);
		disponibilidade.setUsuarioId(null);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.horarioService.verificarDisponibilidade(disponibilidade.getDatahora(), disponibilidade.getUsuarioId()))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(disponibilidade);
		
		mockMvc.perform(post("/api/v1/horario/disponibilidade")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
}
