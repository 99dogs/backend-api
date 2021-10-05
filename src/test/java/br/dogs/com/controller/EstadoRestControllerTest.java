package br.dogs.com.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import br.dogs.com.helper.TipoUsuario;
import br.dogs.com.model.entities.Estado;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.EstadoService;
import br.dogs.com.service.UsuarioService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class EstadoRestControllerTest {

	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private EstadoRestController estadoRestController;
	
	@MockBean
	private EstadoService estadoService;
	
	@MockBean
	private UsuarioService usuarioService;
	
	private Usuario authUsuarioAdmin = new Usuario();
	
	private Usuario authUsuarioTutor = new Usuario();
	
	private Usuario authUsuarioDogwalker = new Usuario();
	
	private String tokenUsuarioAdmin;
	
	private String tokenUsuarioTutor;
	
	private String tokenUsuarioDogwalker;
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void startMocks(){
		estadoRestController = context.getBean(EstadoRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(estadoRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(estadoService);
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
	public void deveRetornarSucesso_QuandoBuscarTodos_UsuarioDogwalker() throws Exception {
		
		List<Estado> estados = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.estadoService.buscarTodos())
			.thenReturn(estados);
		
		mockMvc.perform(get("/api/v1/estado")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTodos_UsuarioTutor() throws Exception {
		
		List<Estado> estados = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.estadoService.buscarTodos())
			.thenReturn(estados);
		
		mockMvc.perform(get("/api/v1/estado")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTodos_UsuarioAdmin() throws Exception {
		
		List<Estado> estados = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.estadoService.buscarTodos())
			.thenReturn(estados);
		
		mockMvc.perform(get("/api/v1/estado")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarTodos_ThrowException() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.estadoService.buscarTodos())
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		mockMvc.perform(get("/api/v1/estado")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
}
