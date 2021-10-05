package br.dogs.com.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.dogs.com.helper.TipoUsuario;
import br.dogs.com.model.dto.UsuarioAlterarDados;
import br.dogs.com.model.dto.UsuarioAutenticado;
import br.dogs.com.model.dto.UsuarioRegistro;
import br.dogs.com.model.dto.UsuarioSocialLogin;
import br.dogs.com.model.entities.Cidade;
import br.dogs.com.model.entities.Estado;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.CidadeService;
import br.dogs.com.service.EstadoService;
import br.dogs.com.service.UsuarioService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class UsuarioRestControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
		
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private UsuarioRestController usuarioRestController;
	
	@MockBean
	private PasswordEncoder passwordEncoder;
	
	@MockBean
	private AuthenticationManager authenticationManager;
	
	@MockBean
	private EstadoService estadoService;
	
	@MockBean
	private CidadeService cidadeService;
	
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
		usuarioRestController = context.getBean(UsuarioRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(usuarioRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(authenticationManager);
		standaloneSetup(estadoService);
		standaloneSetup(cidadeService);
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
	public void deveRetornarSucesso_QuandoRegistarUsuario() throws Exception {
		
		Usuario usuario = new Usuario();
		UsuarioRegistro usuarioRegistro = new UsuarioRegistro();
		
		usuarioRegistro.setNome("usuario test");
		usuarioRegistro.setEmail("usuario@email.com");
		usuarioRegistro.setSenha("senha test");
		usuarioRegistro.setTipo("TUTOR");
		
		usuario.setNome(usuarioRegistro.getNome());
		usuario.setEmail(usuarioRegistro.getEmail());
		usuario.setSenha(usuarioRegistro.getSenha());
		usuario.setTipo(usuarioRegistro.getTipo());
		
		when(this.usuarioService.buscarPorEmail(usuarioRegistro.getEmail()))
			.thenReturn(usuario);
		
		when(this.passwordEncoder.encode(usuarioRegistro.getSenha()))
			.thenReturn("senha encrypted");
		
		when(this.usuarioService.registrar(Mockito.any(UsuarioRegistro.class)))
			.thenReturn(new UsuarioAutenticado());
				
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioRegistro.getNome());
		dados.put("email", usuarioRegistro.getEmail());
		dados.put("senha", usuarioRegistro.getSenha());
		dados.put("tipo", usuarioRegistro.getTipo());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/registrar")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistarUsuario_UsuarioJaRegistrado() throws Exception {
		
		Usuario usuario = new Usuario();
		UsuarioRegistro usuarioRegistro = new UsuarioRegistro();
		
		usuarioRegistro.setNome("usuario test");
		usuarioRegistro.setEmail("usuario@email.com");
		usuarioRegistro.setSenha("senha test");
		usuarioRegistro.setTipo("TUTOR");
		
		usuario.setId(999L);
		usuario.setNome(usuarioRegistro.getNome());
		usuario.setEmail(usuarioRegistro.getEmail());
		usuario.setSenha(usuarioRegistro.getSenha());
		usuario.setTipo(usuarioRegistro.getTipo());
		
		when(this.usuarioService.buscarPorEmail(usuarioRegistro.getEmail()))
			.thenReturn(usuario);
		
		when(this.passwordEncoder.encode(usuarioRegistro.getSenha()))
			.thenReturn("senha encrypted");
		
		when(this.usuarioService.registrar(Mockito.any(UsuarioRegistro.class)))
			.thenReturn(new UsuarioAutenticado());
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioRegistro.getNome());
		dados.put("email", usuarioRegistro.getEmail());
		dados.put("senha", usuarioRegistro.getSenha());
		dados.put("tipo", usuarioRegistro.getTipo());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/registrar")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistarUsuario_ErroAoRegistarUsuario() throws Exception {
		
		Usuario usuario = new Usuario();
		UsuarioRegistro usuarioRegistro = new UsuarioRegistro();
		
		usuarioRegistro.setNome("usuario test");
		usuarioRegistro.setEmail("usuario@email.com");
		usuarioRegistro.setSenha("senha test");
		usuarioRegistro.setTipo("TUTOR");
		
		usuario.setNome(usuarioRegistro.getNome());
		usuario.setEmail(usuarioRegistro.getEmail());
		usuario.setSenha(usuarioRegistro.getSenha());
		usuario.setTipo(usuarioRegistro.getTipo());
		
		when(this.usuarioService.buscarPorEmail(usuarioRegistro.getEmail()))
			.thenReturn(usuario);
		
		when(this.passwordEncoder.encode(usuarioRegistro.getSenha()))
			.thenReturn("senha encrypted");
		
		when(this.usuarioService.registrar(Mockito.any(UsuarioRegistro.class)))
			.thenReturn(null);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioRegistro.getNome());
		dados.put("email", usuarioRegistro.getEmail());
		dados.put("senha", usuarioRegistro.getSenha());
		dados.put("tipo", usuarioRegistro.getTipo());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/registrar")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistarUsuario_ThrowException() throws Exception {
		
		Usuario usuario = new Usuario();
		UsuarioRegistro usuarioRegistro = new UsuarioRegistro();
		
		usuarioRegistro.setNome("usuario test");
		usuarioRegistro.setEmail("usuario@email.com");
		usuarioRegistro.setSenha("senha test");
		usuarioRegistro.setTipo("TUTOR");
		
		usuario.setNome(usuarioRegistro.getNome());
		usuario.setEmail(usuarioRegistro.getEmail());
		usuario.setSenha(usuarioRegistro.getSenha());
		usuario.setTipo(usuarioRegistro.getTipo());
		
		when(this.usuarioService.buscarPorEmail(usuarioRegistro.getEmail()))
			.thenReturn(usuario);
		
		when(this.passwordEncoder.encode(usuarioRegistro.getSenha()))
			.thenReturn("senha encrypted");
		
		when(this.usuarioService.registrar(Mockito.any(UsuarioRegistro.class)))
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioRegistro.getNome());
		dados.put("email", usuarioRegistro.getEmail());
		dados.put("senha", usuarioRegistro.getSenha());
		dados.put("tipo", usuarioRegistro.getTipo());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/registrar")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistarUsuario_EmailVazio() throws Exception {
		
		Usuario usuario = new Usuario();
		UsuarioRegistro usuarioRegistro = new UsuarioRegistro();
		
		usuarioRegistro.setNome("usuario test");
		usuarioRegistro.setEmail("usuario@email.com");
		usuarioRegistro.setSenha("senha test");
		usuarioRegistro.setTipo("TUTOR");
		
		usuario.setNome(usuarioRegistro.getNome());
		usuario.setEmail(usuarioRegistro.getEmail());
		usuario.setSenha(usuarioRegistro.getSenha());
		usuario.setTipo(usuarioRegistro.getTipo());
		
		when(this.usuarioService.buscarPorEmail(usuarioRegistro.getEmail()))
			.thenReturn(usuario);
		
		when(this.passwordEncoder.encode(usuarioRegistro.getSenha()))
			.thenReturn("senha encrypted");
		
		when(this.usuarioService.registrar(Mockito.any(UsuarioRegistro.class)))
			.thenReturn(new UsuarioAutenticado());
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioRegistro.getNome());
		dados.put("email", "");
		dados.put("senha", usuarioRegistro.getSenha());
		dados.put("tipo", usuarioRegistro.getTipo());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/registrar")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistarUsuario_SenhaVazio() throws Exception {
		
		Usuario usuario = new Usuario();
		UsuarioRegistro usuarioRegistro = new UsuarioRegistro();
		
		usuarioRegistro.setNome("usuario test");
		usuarioRegistro.setEmail("usuario@email.com");
		usuarioRegistro.setSenha("senha test");
		usuarioRegistro.setTipo("TUTOR");
		
		usuario.setNome(usuarioRegistro.getNome());
		usuario.setEmail(usuarioRegistro.getEmail());
		usuario.setSenha(usuarioRegistro.getSenha());
		usuario.setTipo(usuarioRegistro.getTipo());
		
		when(this.usuarioService.buscarPorEmail(usuarioRegistro.getEmail()))
			.thenReturn(usuario);
		
		when(this.passwordEncoder.encode(usuarioRegistro.getSenha()))
			.thenReturn("senha encrypted");
		
		when(this.usuarioService.registrar(Mockito.any(UsuarioRegistro.class)))
			.thenReturn(new UsuarioAutenticado());
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioRegistro.getNome());
		dados.put("email", usuarioRegistro.getEmail());
		dados.put("senha", "");
		dados.put("tipo", usuarioRegistro.getTipo());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/registrar")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistarUsuario_TipoVazio() throws Exception {
		
		Usuario usuario = new Usuario();
		UsuarioRegistro usuarioRegistro = new UsuarioRegistro();
		
		usuarioRegistro.setNome("usuario test");
		usuarioRegistro.setEmail("usuario@email.com");
		usuarioRegistro.setSenha("senha test");
		usuarioRegistro.setTipo("TUTOR");
		
		usuario.setNome(usuarioRegistro.getNome());
		usuario.setEmail(usuarioRegistro.getEmail());
		usuario.setSenha(usuarioRegistro.getSenha());
		usuario.setTipo(usuarioRegistro.getTipo());
		
		when(this.usuarioService.buscarPorEmail(usuarioRegistro.getEmail()))
			.thenReturn(usuario);
		
		when(this.passwordEncoder.encode(usuarioRegistro.getSenha()))
			.thenReturn("senha encrypted");
		
		when(this.usuarioService.registrar(Mockito.any(UsuarioRegistro.class)))
			.thenReturn(new UsuarioAutenticado());

		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioRegistro.getNome());
		dados.put("email", usuarioRegistro.getEmail());
		dados.put("senha", usuarioRegistro.getSenha());
		dados.put("tipo", "");
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/registrar")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoRealizarLogin() throws Exception {
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorEmail(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		Map<String, Object> dados = new HashMap<>();

		dados.put("email", authUsuarioTutor.getEmail());
		dados.put("senha", authUsuarioTutor.getSenha());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/login")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoRealizarLogin_LoginIncorreto() throws Exception {
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorEmail(authUsuarioTutor.getEmail()))
			.thenReturn(null);
		
		Map<String, Object> dados = new HashMap<>();

		dados.put("email", authUsuarioTutor.getEmail());
		dados.put("senha", authUsuarioTutor.getSenha());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/login")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoRealizarLogin_BadCredentialsException() throws Exception {
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorEmail(authUsuarioTutor.getEmail()))
			.thenAnswer((t)->{throw new BadCredentialsException("login invalido");});
		
		Map<String, Object> dados = new HashMap<>();

		dados.put("email", authUsuarioTutor.getEmail());
		dados.put("senha", authUsuarioTutor.getSenha());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/login")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarDogwalkers() throws Exception {
		
		List<Usuario> dogwalkers = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarDogwalkers())
			.thenReturn(dogwalkers);
		
		mockMvc.perform(get("/api/v1/usuario/dogwalker")
				  .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarDogwalkers_ThrowException() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarDogwalkers())
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		mockMvc.perform(get("/api/v1/usuario/dogwalker")
				  .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTutores() throws Exception {
		
		List<Usuario> tutores = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.usuarioService.buscarTutores())
			.thenReturn(tutores);
		
		mockMvc.perform(get("/api/v1/usuario/tutor")
				  .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarTutores_ThrowException() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.usuarioService.buscarTutores())
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		mockMvc.perform(get("/api/v1/usuario/tutor")
				  .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTodos() throws Exception {
		
		List<Usuario> usuarios = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.usuarioService.buscarTodos())
			.thenReturn(usuarios);
		
		mockMvc.perform(get("/api/v1/usuario/todos")
				  .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarTodos_ThrowException() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.usuarioService.buscarTodos())
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		mockMvc.perform(get("/api/v1/usuario/todos")
				  .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoAlterarDados() throws Exception {
		
		UsuarioAlterarDados usuarioAlterarDados = new UsuarioAlterarDados();
		Cidade cidade = new Cidade();
		Estado estado = new Estado();
		
		cidade.setId(11L);
		cidade.setAtivo(true);
		
		estado.setId(10L);
		estado.setAtivo(true);
		
		usuarioAlterarDados.setNome("usuario test");
		usuarioAlterarDados.setTelefone("usuario test");
		usuarioAlterarDados.setRua("usuario test");
		usuarioAlterarDados.setBairro("usuario test");
		usuarioAlterarDados.setEstadoId(estado.getId());
		usuarioAlterarDados.setCidadeId(cidade.getId());
		usuarioAlterarDados.setNumero("numero test");
		usuarioAlterarDados.setCep("37540000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
		.thenReturn(authUsuarioDogwalker);
		
		when(this.cidadeService.buscarPorId(cidade.getId()))
			.thenReturn(cidade);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(estado);
		
		when(this.usuarioService.alterarDados(Mockito.any(UsuarioAlterarDados.class)))
			.thenReturn(true);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioAlterarDados.getNome());
		dados.put("telefone", usuarioAlterarDados.getTelefone());
		dados.put("rua", usuarioAlterarDados.getRua());
		dados.put("bairro", usuarioAlterarDados.getBairro());
		dados.put("estadoId", usuarioAlterarDados.getEstadoId());
		dados.put("cidadeId", usuarioAlterarDados.getCidadeId());
		dados.put("numero", usuarioAlterarDados.getNumero());
		dados.put("cep", usuarioAlterarDados.getCep());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/usuario/dados")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarDados_ThrowException() throws Exception {
		
		UsuarioAlterarDados usuarioAlterarDados = new UsuarioAlterarDados();
		Cidade cidade = new Cidade();
		Estado estado = new Estado();
		
		cidade.setId(11L);
		cidade.setAtivo(true);
		
		estado.setId(10L);
		estado.setAtivo(true);
		
		usuarioAlterarDados.setNome("usuario test");
		usuarioAlterarDados.setTelefone("usuario test");
		usuarioAlterarDados.setRua("usuario test");
		usuarioAlterarDados.setBairro("usuario test");
		usuarioAlterarDados.setEstadoId(estado.getId());
		usuarioAlterarDados.setCidadeId(cidade.getId());
		usuarioAlterarDados.setNumero("numero test");
		usuarioAlterarDados.setCep("37540000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
		.thenReturn(authUsuarioDogwalker);
		
		when(this.cidadeService.buscarPorId(cidade.getId()))
			.thenReturn(cidade);
		
		when(this.estadoService.buscarPorId(estado.getId()))
		.thenReturn(estado);
		
		when(this.usuarioService.alterarDados(Mockito.any(UsuarioAlterarDados.class)))
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioAlterarDados.getNome());
		dados.put("telefone", usuarioAlterarDados.getTelefone());
		dados.put("rua", usuarioAlterarDados.getRua());
		dados.put("bairro", usuarioAlterarDados.getBairro());
		dados.put("estadoId", usuarioAlterarDados.getEstadoId());
		dados.put("cidadeId", usuarioAlterarDados.getCidadeId());
		dados.put("numero", usuarioAlterarDados.getNumero());
		dados.put("cep", usuarioAlterarDados.getCep());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/usuario/dados")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarDados_ErroAoAlterarDados() throws Exception {
		
		UsuarioAlterarDados usuarioAlterarDados = new UsuarioAlterarDados();
		Cidade cidade = new Cidade();
		Estado estado = new Estado();
		
		cidade.setId(11L);
		cidade.setAtivo(true);
		
		estado.setId(10L);
		estado.setAtivo(true);
		
		usuarioAlterarDados.setNome("usuario test");
		usuarioAlterarDados.setTelefone("usuario test");
		usuarioAlterarDados.setRua("usuario test");
		usuarioAlterarDados.setBairro("usuario test");
		usuarioAlterarDados.setEstadoId(estado.getId());
		usuarioAlterarDados.setCidadeId(cidade.getId());
		usuarioAlterarDados.setNumero("numero test");
		usuarioAlterarDados.setCep("37540000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
		.thenReturn(authUsuarioDogwalker);
		
		when(this.cidadeService.buscarPorId(cidade.getId()))
			.thenReturn(cidade);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(estado);
		
		when(this.usuarioService.alterarDados(Mockito.any(UsuarioAlterarDados.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioAlterarDados.getNome());
		dados.put("telefone", usuarioAlterarDados.getTelefone());
		dados.put("rua", usuarioAlterarDados.getRua());
		dados.put("bairro", usuarioAlterarDados.getBairro());
		dados.put("estadoId", usuarioAlterarDados.getEstadoId());
		dados.put("cidadeId", usuarioAlterarDados.getCidadeId());
		dados.put("numero", usuarioAlterarDados.getNumero());
		dados.put("cep", usuarioAlterarDados.getCep());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/usuario/dados")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarDados_NomeVazio() throws Exception {
		
		UsuarioAlterarDados usuarioAlterarDados = new UsuarioAlterarDados();
		Cidade cidade = new Cidade();
		Estado estado = new Estado();
		
		cidade.setId(11L);
		cidade.setAtivo(true);
		
		estado.setId(10L);
		estado.setAtivo(true);
		
		usuarioAlterarDados.setNome("usuario test");
		usuarioAlterarDados.setTelefone("usuario test");
		usuarioAlterarDados.setRua("usuario test");
		usuarioAlterarDados.setBairro("usuario test");
		usuarioAlterarDados.setEstadoId(estado.getId());
		usuarioAlterarDados.setCidadeId(cidade.getId());
		usuarioAlterarDados.setNumero("numero test");
		usuarioAlterarDados.setCep("37540000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
		.thenReturn(authUsuarioDogwalker);
		
		when(this.cidadeService.buscarPorId(cidade.getId()))
			.thenReturn(cidade);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(estado);
		
		when(this.usuarioService.alterarDados(Mockito.any(UsuarioAlterarDados.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", "");
		dados.put("telefone", usuarioAlterarDados.getTelefone());
		dados.put("rua", usuarioAlterarDados.getRua());
		dados.put("bairro", usuarioAlterarDados.getBairro());
		dados.put("estadoId", usuarioAlterarDados.getEstadoId());
		dados.put("cidadeId", usuarioAlterarDados.getCidadeId());
		dados.put("numero", usuarioAlterarDados.getNumero());
		dados.put("cep", usuarioAlterarDados.getCep());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/usuario/dados")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarDados_EstadoIdInvalido() throws Exception {
		
		UsuarioAlterarDados usuarioAlterarDados = new UsuarioAlterarDados();
		Cidade cidade = new Cidade();
		Estado estado = new Estado();
		
		cidade.setId(11L);
		cidade.setAtivo(true);
		
		estado.setId(10L);
		estado.setAtivo(true);
		
		usuarioAlterarDados.setNome("usuario test");
		usuarioAlterarDados.setTelefone("usuario test");
		usuarioAlterarDados.setRua("usuario test");
		usuarioAlterarDados.setBairro("usuario test");
		usuarioAlterarDados.setEstadoId(estado.getId());
		usuarioAlterarDados.setCidadeId(cidade.getId());
		usuarioAlterarDados.setNumero("numero test");
		usuarioAlterarDados.setCep("37540000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
		.thenReturn(authUsuarioDogwalker);
		
		when(this.cidadeService.buscarPorId(cidade.getId()))
			.thenReturn(cidade);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(estado);
		
		when(this.usuarioService.alterarDados(Mockito.any(UsuarioAlterarDados.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioAlterarDados.getNome());
		dados.put("telefone", usuarioAlterarDados.getTelefone());
		dados.put("rua", usuarioAlterarDados.getRua());
		dados.put("bairro", usuarioAlterarDados.getBairro());
		dados.put("estadoId", null);
		dados.put("cidadeId", usuarioAlterarDados.getCidadeId());
		dados.put("numero", usuarioAlterarDados.getNumero());
		dados.put("cep", usuarioAlterarDados.getCep());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/usuario/dados")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarDados_CidadeIdInvalido() throws Exception {
		
		UsuarioAlterarDados usuarioAlterarDados = new UsuarioAlterarDados();
		Cidade cidade = new Cidade();
		Estado estado = new Estado();
		
		cidade.setId(11L);
		cidade.setAtivo(true);
		
		estado.setId(10L);
		estado.setAtivo(true);
		
		usuarioAlterarDados.setNome("usuario test");
		usuarioAlterarDados.setTelefone("usuario test");
		usuarioAlterarDados.setRua("usuario test");
		usuarioAlterarDados.setBairro("usuario test");
		usuarioAlterarDados.setEstadoId(estado.getId());
		usuarioAlterarDados.setCidadeId(cidade.getId());
		usuarioAlterarDados.setNumero("numero test");
		usuarioAlterarDados.setCep("37540000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
		.thenReturn(authUsuarioDogwalker);
		
		when(this.cidadeService.buscarPorId(cidade.getId()))
			.thenReturn(cidade);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(estado);
		
		when(this.usuarioService.alterarDados(Mockito.any(UsuarioAlterarDados.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioAlterarDados.getNome());
		dados.put("telefone", usuarioAlterarDados.getTelefone());
		dados.put("rua", usuarioAlterarDados.getRua());
		dados.put("bairro", usuarioAlterarDados.getBairro());
		dados.put("estadoId", usuarioAlterarDados.getEstadoId());
		dados.put("cidadeId", null);
		dados.put("numero", usuarioAlterarDados.getNumero());
		dados.put("cep", usuarioAlterarDados.getCep());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/usuario/dados")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarDados_CepInvalido() throws Exception {
		
		UsuarioAlterarDados usuarioAlterarDados = new UsuarioAlterarDados();
		Cidade cidade = new Cidade();
		Estado estado = new Estado();
		
		cidade.setId(11L);
		cidade.setAtivo(true);
		
		estado.setId(10L);
		estado.setAtivo(true);
		
		usuarioAlterarDados.setNome("usuario test");
		usuarioAlterarDados.setTelefone("usuario test");
		usuarioAlterarDados.setRua("usuario test");
		usuarioAlterarDados.setBairro("usuario test");
		usuarioAlterarDados.setEstadoId(estado.getId());
		usuarioAlterarDados.setCidadeId(cidade.getId());
		usuarioAlterarDados.setNumero("numero test");
		usuarioAlterarDados.setCep("375400000000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
		.thenReturn(authUsuarioDogwalker);
		
		when(this.cidadeService.buscarPorId(cidade.getId()))
			.thenReturn(cidade);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(estado);
		
		when(this.usuarioService.alterarDados(Mockito.any(UsuarioAlterarDados.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioAlterarDados.getNome());
		dados.put("telefone", usuarioAlterarDados.getTelefone());
		dados.put("rua", usuarioAlterarDados.getRua());
		dados.put("bairro", usuarioAlterarDados.getBairro());
		dados.put("estadoId", usuarioAlterarDados.getEstadoId());
		dados.put("cidadeId", usuarioAlterarDados.getCidadeId());
		dados.put("numero", usuarioAlterarDados.getNumero());
		dados.put("cep", usuarioAlterarDados.getCep());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/usuario/dados")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarDados_EstadoNaoExistente() throws Exception {
		
		UsuarioAlterarDados usuarioAlterarDados = new UsuarioAlterarDados();
		Cidade cidade = new Cidade();
		Estado estado = new Estado();
		
		cidade.setId(11L);
		cidade.setAtivo(true);
		
		estado.setId(10L);
		estado.setAtivo(true);
		
		usuarioAlterarDados.setNome("usuario test");
		usuarioAlterarDados.setTelefone("usuario test");
		usuarioAlterarDados.setRua("usuario test");
		usuarioAlterarDados.setBairro("usuario test");
		usuarioAlterarDados.setEstadoId(estado.getId());
		usuarioAlterarDados.setCidadeId(cidade.getId());
		usuarioAlterarDados.setNumero("numero test");
		usuarioAlterarDados.setCep("37540000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
		.thenReturn(authUsuarioDogwalker);
		
		when(this.cidadeService.buscarPorId(cidade.getId()))
			.thenReturn(cidade);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(null);
		
		when(this.usuarioService.alterarDados(Mockito.any(UsuarioAlterarDados.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioAlterarDados.getNome());
		dados.put("telefone", usuarioAlterarDados.getTelefone());
		dados.put("rua", usuarioAlterarDados.getRua());
		dados.put("bairro", usuarioAlterarDados.getBairro());
		dados.put("estadoId", usuarioAlterarDados.getEstadoId());
		dados.put("cidadeId", usuarioAlterarDados.getCidadeId());
		dados.put("numero", usuarioAlterarDados.getNumero());
		dados.put("cep", usuarioAlterarDados.getCep());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/usuario/dados")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarDados_EstadoInativo() throws Exception {
		
		UsuarioAlterarDados usuarioAlterarDados = new UsuarioAlterarDados();
		Cidade cidade = new Cidade();
		Estado estado = new Estado();
		
		cidade.setId(11L);
		cidade.setAtivo(true);
		
		estado.setId(10L);
		estado.setAtivo(false);
		
		usuarioAlterarDados.setNome("usuario test");
		usuarioAlterarDados.setTelefone("usuario test");
		usuarioAlterarDados.setRua("usuario test");
		usuarioAlterarDados.setBairro("usuario test");
		usuarioAlterarDados.setEstadoId(estado.getId());
		usuarioAlterarDados.setCidadeId(cidade.getId());
		usuarioAlterarDados.setNumero("numero test");
		usuarioAlterarDados.setCep("37540000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
		.thenReturn(authUsuarioDogwalker);
		
		when(this.cidadeService.buscarPorId(cidade.getId()))
			.thenReturn(cidade);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(estado);
		
		when(this.usuarioService.alterarDados(Mockito.any(UsuarioAlterarDados.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioAlterarDados.getNome());
		dados.put("telefone", usuarioAlterarDados.getTelefone());
		dados.put("rua", usuarioAlterarDados.getRua());
		dados.put("bairro", usuarioAlterarDados.getBairro());
		dados.put("estadoId", usuarioAlterarDados.getEstadoId());
		dados.put("cidadeId", usuarioAlterarDados.getCidadeId());
		dados.put("numero", usuarioAlterarDados.getNumero());
		dados.put("cep", usuarioAlterarDados.getCep());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/usuario/dados")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarDados_CidadeNaoExistente() throws Exception {
		
		UsuarioAlterarDados usuarioAlterarDados = new UsuarioAlterarDados();
		Cidade cidade = new Cidade();
		Estado estado = new Estado();
		
		cidade.setId(11L);
		cidade.setAtivo(true);
		
		estado.setId(10L);
		estado.setAtivo(true);
		
		usuarioAlterarDados.setNome("usuario test");
		usuarioAlterarDados.setTelefone("usuario test");
		usuarioAlterarDados.setRua("usuario test");
		usuarioAlterarDados.setBairro("usuario test");
		usuarioAlterarDados.setEstadoId(estado.getId());
		usuarioAlterarDados.setCidadeId(cidade.getId());
		usuarioAlterarDados.setNumero("numero test");
		usuarioAlterarDados.setCep("37540000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
		.thenReturn(authUsuarioDogwalker);
		
		when(this.cidadeService.buscarPorId(cidade.getId()))
			.thenReturn(null);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(estado);
		
		when(this.usuarioService.alterarDados(Mockito.any(UsuarioAlterarDados.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioAlterarDados.getNome());
		dados.put("telefone", usuarioAlterarDados.getTelefone());
		dados.put("rua", usuarioAlterarDados.getRua());
		dados.put("bairro", usuarioAlterarDados.getBairro());
		dados.put("estadoId", usuarioAlterarDados.getEstadoId());
		dados.put("cidadeId", usuarioAlterarDados.getCidadeId());
		dados.put("numero", usuarioAlterarDados.getNumero());
		dados.put("cep", usuarioAlterarDados.getCep());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/usuario/dados")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarDados_CidadeInativa() throws Exception {
		
		UsuarioAlterarDados usuarioAlterarDados = new UsuarioAlterarDados();
		Cidade cidade = new Cidade();
		Estado estado = new Estado();
		
		cidade.setId(11L);
		cidade.setAtivo(false);
		
		estado.setId(10L);
		estado.setAtivo(true);
		
		usuarioAlterarDados.setNome("usuario test");
		usuarioAlterarDados.setTelefone("usuario test");
		usuarioAlterarDados.setRua("usuario test");
		usuarioAlterarDados.setBairro("usuario test");
		usuarioAlterarDados.setEstadoId(estado.getId());
		usuarioAlterarDados.setCidadeId(cidade.getId());
		usuarioAlterarDados.setNumero("numero test");
		usuarioAlterarDados.setCep("37540000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.cidadeService.buscarPorId(cidade.getId()))
			.thenReturn(cidade);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(estado);
		
		when(this.usuarioService.alterarDados(Mockito.any(UsuarioAlterarDados.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioAlterarDados.getNome());
		dados.put("telefone", usuarioAlterarDados.getTelefone());
		dados.put("rua", usuarioAlterarDados.getRua());
		dados.put("bairro", usuarioAlterarDados.getBairro());
		dados.put("estadoId", usuarioAlterarDados.getEstadoId());
		dados.put("cidadeId", usuarioAlterarDados.getCidadeId());
		dados.put("numero", usuarioAlterarDados.getNumero());
		dados.put("cep", usuarioAlterarDados.getCep());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/usuario/dados")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoAlterarDados_UsuarioNaoExistente() throws Exception {
		
		UsuarioAlterarDados usuarioAlterarDados = new UsuarioAlterarDados();
		Cidade cidade = new Cidade();
		Estado estado = new Estado();
		
		cidade.setId(11L);
		cidade.setAtivo(true);
		
		estado.setId(10L);
		estado.setAtivo(true);
		
		usuarioAlterarDados.setNome("usuario test");
		usuarioAlterarDados.setTelefone("usuario test");
		usuarioAlterarDados.setRua("usuario test");
		usuarioAlterarDados.setBairro("usuario test");
		usuarioAlterarDados.setEstadoId(estado.getId());
		usuarioAlterarDados.setCidadeId(cidade.getId());
		usuarioAlterarDados.setNumero("numero test");
		usuarioAlterarDados.setCep("37540000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
		.thenReturn(null);
		
		when(this.cidadeService.buscarPorId(cidade.getId()))
			.thenReturn(cidade);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(estado);
		
		when(this.usuarioService.alterarDados(Mockito.any(UsuarioAlterarDados.class)))
			.thenReturn(false);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuarioAlterarDados.getNome());
		dados.put("telefone", usuarioAlterarDados.getTelefone());
		dados.put("rua", usuarioAlterarDados.getRua());
		dados.put("bairro", usuarioAlterarDados.getBairro());
		dados.put("estadoId", usuarioAlterarDados.getEstadoId());
		dados.put("cidadeId", usuarioAlterarDados.getCidadeId());
		dados.put("numero", usuarioAlterarDados.getNumero());
		dados.put("cep", usuarioAlterarDados.getCep());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(put("/api/v1/usuario/dados")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarMinhasInformacoes() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(authUsuarioTutor);
		
		mockMvc.perform(get("/api/v1/usuario/me")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoBuscarMinhasInformacoes_UsuarioNaoExistente() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenReturn(null);
		
		mockMvc.perform(get("/api/v1/usuario/me")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarMinhasInformacoes_ThrowException() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioTutor.getId()))
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		mockMvc.perform(get("/api/v1/usuario/me")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarDogwalkerPorId() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenReturn(authUsuarioDogwalker);
		
		mockMvc.perform(get("/api/v1/usuario/dogwalker/{id}", authUsuarioDogwalker.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarDogwalkerPorId_ThrowException() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(authUsuarioDogwalker.getId()))
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		mockMvc.perform(get("/api/v1/usuario/dogwalker/{id}", authUsuarioDogwalker.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoRegistrarSocialLogin() throws Exception {
		
		Usuario newUsuario = new Usuario();
		UsuarioSocialLogin usuario = new UsuarioSocialLogin();
		UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
		
		usuario.setNome("usuario test");
		usuario.setEmail("usuario@email.com");
		usuario.setSocialId("213213213");
		usuario.setFotoUrl("url");
		usuario.setTipo("TUTOR");
		
		usuarioAutenticado.setId(10L);
		
		newUsuario.setId(usuarioAutenticado.getId());
		newUsuario.setEmail(usuario.getEmail());
		newUsuario.setTipo(usuario.getTipo());
		
		when(this.usuarioService.buscarPorId(usuarioAutenticado.getId()))
			.thenReturn(newUsuario);
		
		when(this.usuarioService.registrarSocialLogin(Mockito.any(UsuarioSocialLogin.class)))
			.thenReturn(usuarioAutenticado);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuario.getNome());
		dados.put("email", usuario.getEmail());
		dados.put("socialId", usuario.getSocialId());
		dados.put("fotoUrl", usuario.getFotoUrl());
		dados.put("tipo", usuario.getTipo());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/social-login")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistrarSocialLogin_ErroAoRegistrarSocialLogin() throws Exception {
		
		Usuario newUsuario = new Usuario();
		UsuarioSocialLogin usuario = new UsuarioSocialLogin();
		UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
		
		usuario.setNome("usuario test");
		usuario.setEmail("usuario@email.com");
		usuario.setSocialId("213213213");
		usuario.setFotoUrl("url");
		usuario.setTipo("TUTOR");
		
		usuarioAutenticado.setId(10L);
		
		newUsuario.setId(usuarioAutenticado.getId());
		newUsuario.setEmail(usuario.getEmail());
		newUsuario.setTipo(usuario.getTipo());
		
		when(this.usuarioService.buscarPorId(usuarioAutenticado.getId()))
			.thenReturn(newUsuario);
		
		when(this.usuarioService.registrarSocialLogin(Mockito.any(UsuarioSocialLogin.class)))
			.thenReturn(null);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuario.getNome());
		dados.put("email", usuario.getEmail());
		dados.put("socialId", usuario.getSocialId());
		dados.put("fotoUrl", usuario.getFotoUrl());
		dados.put("tipo", usuario.getTipo());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/social-login")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistrarSocialLogin_ThrowException() throws Exception {
		
		Usuario newUsuario = new Usuario();
		UsuarioSocialLogin usuario = new UsuarioSocialLogin();
		UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
		
		usuario.setNome("usuario test");
		usuario.setEmail("usuario@email.com");
		usuario.setSocialId("213213213");
		usuario.setFotoUrl("url");
		usuario.setTipo("TUTOR");
		
		usuarioAutenticado.setId(10L);
		
		newUsuario.setId(usuarioAutenticado.getId());
		newUsuario.setEmail(usuario.getEmail());
		newUsuario.setTipo(usuario.getTipo());
		
		when(this.usuarioService.buscarPorId(usuarioAutenticado.getId()))
			.thenReturn(newUsuario);
		
		when(this.usuarioService.registrarSocialLogin(Mockito.any(UsuarioSocialLogin.class)))
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuario.getNome());
		dados.put("email", usuario.getEmail());
		dados.put("socialId", usuario.getSocialId());
		dados.put("fotoUrl", usuario.getFotoUrl());
		dados.put("tipo", usuario.getTipo());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/social-login")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistrarSocialLogin_NomeVazio() throws Exception {
		
		Usuario newUsuario = new Usuario();
		UsuarioSocialLogin usuario = new UsuarioSocialLogin();
		UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
		
		usuario.setNome("usuario test");
		usuario.setEmail("usuario@email.com");
		usuario.setSocialId("213213213");
		usuario.setFotoUrl("url");
		usuario.setTipo("TUTOR");
		
		usuarioAutenticado.setId(10L);
		
		newUsuario.setId(usuarioAutenticado.getId());
		newUsuario.setEmail(usuario.getEmail());
		newUsuario.setTipo(usuario.getTipo());
		
		when(this.usuarioService.buscarPorId(usuarioAutenticado.getId()))
			.thenReturn(newUsuario);
		
		when(this.usuarioService.registrarSocialLogin(Mockito.any(UsuarioSocialLogin.class)))
			.thenReturn(usuarioAutenticado);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", "");
		dados.put("email", usuario.getEmail());
		dados.put("socialId", usuario.getSocialId());
		dados.put("fotoUrl", usuario.getFotoUrl());
		dados.put("tipo", usuario.getTipo());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/social-login")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistrarSocialLogin_EmailVazio() throws Exception {
		
		Usuario newUsuario = new Usuario();
		UsuarioSocialLogin usuario = new UsuarioSocialLogin();
		UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
		
		usuario.setNome("usuario test");
		usuario.setEmail("usuario@email.com");
		usuario.setSocialId("213213213");
		usuario.setFotoUrl("url");
		usuario.setTipo("TUTOR");
		
		usuarioAutenticado.setId(10L);
		
		newUsuario.setId(usuarioAutenticado.getId());
		newUsuario.setEmail(usuario.getEmail());
		newUsuario.setTipo(usuario.getTipo());
		
		when(this.usuarioService.buscarPorId(usuarioAutenticado.getId()))
			.thenReturn(newUsuario);
		
		when(this.usuarioService.registrarSocialLogin(Mockito.any(UsuarioSocialLogin.class)))
			.thenReturn(usuarioAutenticado);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuario.getNome());
		dados.put("email", "");
		dados.put("socialId", usuario.getSocialId());
		dados.put("fotoUrl", usuario.getFotoUrl());
		dados.put("tipo", usuario.getTipo());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/social-login")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistrarSocialLogin_SocialIdVazio() throws Exception {
		
		Usuario newUsuario = new Usuario();
		UsuarioSocialLogin usuario = new UsuarioSocialLogin();
		UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
		
		usuario.setNome("usuario test");
		usuario.setEmail("usuario@email.com");
		usuario.setSocialId("213213213");
		usuario.setFotoUrl("url");
		usuario.setTipo("TUTOR");
		
		usuarioAutenticado.setId(10L);
		
		newUsuario.setId(usuarioAutenticado.getId());
		newUsuario.setEmail(usuario.getEmail());
		newUsuario.setTipo(usuario.getTipo());
		
		when(this.usuarioService.buscarPorId(usuarioAutenticado.getId()))
			.thenReturn(newUsuario);
		
		when(this.usuarioService.registrarSocialLogin(Mockito.any(UsuarioSocialLogin.class)))
			.thenReturn(usuarioAutenticado);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuario.getNome());
		dados.put("email", usuario.getEmail());
		dados.put("socialId", "");
		dados.put("fotoUrl", usuario.getFotoUrl());
		dados.put("tipo", usuario.getTipo());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/social-login")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoRegistrarSocialLogin_TipoVazio() throws Exception {
		
		Usuario newUsuario = new Usuario();
		UsuarioSocialLogin usuario = new UsuarioSocialLogin();
		UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
		
		usuario.setNome("usuario test");
		usuario.setEmail("usuario@email.com");
		usuario.setSocialId("213213213");
		usuario.setFotoUrl("url");
		usuario.setTipo("TUTOR");
		
		usuarioAutenticado.setId(10L);
		
		newUsuario.setId(usuarioAutenticado.getId());
		newUsuario.setEmail(usuario.getEmail());
		newUsuario.setTipo(usuario.getTipo());
		
		when(this.usuarioService.buscarPorId(usuarioAutenticado.getId()))
			.thenReturn(newUsuario);
		
		when(this.usuarioService.registrarSocialLogin(Mockito.any(UsuarioSocialLogin.class)))
			.thenReturn(usuarioAutenticado);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", usuario.getNome());
		dados.put("email", usuario.getEmail());
		dados.put("socialId", usuario.getSocialId());
		dados.put("fotoUrl", usuario.getFotoUrl());
		dados.put("tipo", "");
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/usuario/social-login")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarPasseiosEfetuados() throws Exception {
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.retornaQtdePasseiosEfetuados(authUsuarioDogwalker.getId()))
			.thenReturn(100L);
		
		mockMvc.perform(get("/api/v1/usuario/dogwalker/passeios-efetuados/{id}", authUsuarioDogwalker.getId())
				  .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarPasseiosEfetuados_ThrowException() throws Exception {
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.retornaQtdePasseiosEfetuados(authUsuarioDogwalker.getId()))
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		mockMvc.perform(get("/api/v1/usuario/dogwalker/passeios-efetuados/{id}", authUsuarioDogwalker.getId())
				  .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());
		
	}
	
}
