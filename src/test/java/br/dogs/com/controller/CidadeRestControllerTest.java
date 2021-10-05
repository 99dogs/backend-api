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
import br.dogs.com.model.entities.Cidade;
import br.dogs.com.model.entities.Estado;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.CidadeService;
import br.dogs.com.service.EstadoService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class CidadeRestControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private CidadeRestController cidadeRestController;
	
	@MockBean
	private CidadeService cidadeService;
	
	@MockBean
	private EstadoService estadoService;
	
	private Usuario authUsuarioAdmin = new Usuario();
	
	private String tokenUsuarioAdmin;
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void startMocks(){
		cidadeRestController = context.getBean(CidadeRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(cidadeRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(cidadeService);
		standaloneSetup(estadoService);
		
		authUsuarioAdmin.setId(1L);
		authUsuarioAdmin.setEmail("admin");
		authUsuarioAdmin.setTipo(TipoUsuario.ADMIN.name());
		
		tokenUsuarioAdmin = jwtTokenProvider.createToken(authUsuarioAdmin);
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarPorEstado() throws Exception {
		
		List<Cidade> cidades = new ArrayList<>();
		Estado estado = new Estado();
		
		estado.setId(1L);
		estado.setAtivo(true);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(estado);
		
		when(this.cidadeService.buscarPorEstado(estado.getId()))
			.thenReturn(cidades);
		
		mockMvc.perform(get("/api/v1/cidade/{estadoId}", estado.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoBuscarPorEstado_EstadoNaoExistente() throws Exception {
		
		List<Cidade> cidades = new ArrayList<>();
		Estado estado = new Estado();
		
		estado.setId(1L);
		estado.setAtivo(true);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(new Estado());
		
		when(this.cidadeService.buscarPorEstado(estado.getId()))
			.thenReturn(cidades);
		
		mockMvc.perform(get("/api/v1/cidade/{estadoId}", estado.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarbadRequest_QuandoBuscarPorEstado_EstadoInativo() throws Exception {
		
		List<Cidade> cidades = new ArrayList<>();
		Estado estado = new Estado();
		
		estado.setId(1L);
		estado.setAtivo(false);
		
		when(this.estadoService.buscarPorId(estado.getId()))
			.thenReturn(estado);
		
		when(this.cidadeService.buscarPorEstado(estado.getId()))
			.thenReturn(cidades);
		
		mockMvc.perform(get("/api/v1/cidade/{estadoId}", estado.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
}
