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
import br.dogs.com.model.entities.Raca;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.RacaService;
import br.dogs.com.service.UsuarioService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class RacaRestControllerTest {
	
private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private RacaRestController racaRestController;
	
	@MockBean
	private RacaService racaService;
	
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
		racaRestController = context.getBean(RacaRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(racaRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(racaService);
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
		
		List<Raca> racas = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.racaService.findAll())
			.thenReturn(racas);
		
		mockMvc.perform(get("/api/v1/raca")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarTodos_ThrowException() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.racaService.findAll())
			.thenAnswer((t) -> { throw new Exception("bad request"); });
		
		mockMvc.perform(get("/api/v1/raca")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarForbidden_QuandoBuscarTodos_UsuarioDogwalker() throws Exception {
		
		List<Raca> racas = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.racaService.findAll())
			.thenReturn(racas);
		
		mockMvc.perform(get("/api/v1/raca")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isForbidden());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCadastrarUmaRaca() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", raca.getNome());
		
		String json = objectMapper.writeValueAsString(dados);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.create(Mockito.any(Raca.class)))
			.thenReturn(raca);
		
		mockMvc.perform(post("/api/v1/raca")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarUmaRaca_NomeVazio() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", "");
		
		String json = objectMapper.writeValueAsString(dados);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.create(Mockito.any(Raca.class)))
			.thenReturn(raca);
		
		mockMvc.perform(post("/api/v1/raca")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarUmaRaca_NomeNulo() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", null);
		
		String json = objectMapper.writeValueAsString(dados);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.create(Mockito.any(Raca.class)))
			.thenReturn(raca);
		
		mockMvc.perform(post("/api/v1/raca")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoCadastrarUmaRaca_UsuarioDogwalker() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", raca.getNome());
		
		String json = objectMapper.writeValueAsString(dados);
				
		when(this.racaService.create(Mockito.any(Raca.class)))
			.thenReturn(raca);
		
		mockMvc.perform(post("/api/v1/raca")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoCadastrarUmaRaca_UsuarioTutor() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", raca.getNome());
		
		String json = objectMapper.writeValueAsString(dados);
				
		when(this.racaService.create(Mockito.any(Raca.class)))
			.thenReturn(raca);
		
		mockMvc.perform(post("/api/v1/raca")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarUmaRaca_ErroAoCadastrarARaca() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(0L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", raca.getNome());
		
		String json = objectMapper.writeValueAsString(dados);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.create(Mockito.any(Raca.class)))
			.thenReturn(raca);
		
		mockMvc.perform(post("/api/v1/raca")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarUmaRaca_ThrowException() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", raca.getNome());
		
		String json = objectMapper.writeValueAsString(dados);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.create(Mockito.any(Raca.class)))
			.thenAnswer((t) -> { throw new Exception("bad request"); });
		
		mockMvc.perform(post("/api/v1/raca")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoAlterarUmaRaca() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", raca.getNome());
		
		String json = objectMapper.writeValueAsString(dados);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.racaService.update(Mockito.any(Raca.class)))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/raca/{id}", raca.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarUmaRaca_NomeVazio() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", "");
		
		String json = objectMapper.writeValueAsString(dados);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.racaService.update(Mockito.any(Raca.class)))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/raca/{id}", raca.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarUmaRaca_NomeNulo() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", null);
		
		String json = objectMapper.writeValueAsString(dados);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.racaService.update(Mockito.any(Raca.class)))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/raca/{id}", raca.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoAlterarUmaRaca_UsuarioTutor() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", raca.getNome());
		
		String json = objectMapper.writeValueAsString(dados);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.racaService.update(Mockito.any(Raca.class)))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/raca/{id}", raca.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoAlterarUmaRaca_UsuarioDogwalker() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", raca.getNome());
		
		String json = objectMapper.writeValueAsString(dados);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.racaService.update(Mockito.any(Raca.class)))
			.thenReturn(true);
		
		mockMvc.perform(put("/api/v1/raca/{id}", raca.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarUmaRaca_ErroAoAlterarARaca() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(0L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", raca.getNome());
		
		String json = objectMapper.writeValueAsString(dados);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.racaService.update(Mockito.any(Raca.class)))
			.thenReturn(false);
		
		mockMvc.perform(put("/api/v1/raca/{id}", raca.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarUmaRaca_ThrowException() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("nome", raca.getNome());
		
		String json = objectMapper.writeValueAsString(dados);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.racaService.update(Mockito.any(Raca.class)))
			.thenAnswer((t) -> { throw new Exception("bad request"); });
		
		mockMvc.perform(put("/api/v1/raca/{id}", raca.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoDeletarUmaRaca() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
				
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.racaService.deleteById(raca.getId()))
			.thenReturn(true);
		
		mockMvc.perform(delete("/api/v1/raca/{id}", raca.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
		
	
	@Test
	public void deveRetornarBadRequest_QuandoDeletarUmaRaca_ThrowException() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
				
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.racaService.deleteById(raca.getId()))
			.thenAnswer((t) -> { throw new Exception("bad request"); });
		
		mockMvc.perform(delete("/api/v1/raca/{id}", raca.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoDeletarUmaRaca_RacaNaoExistente() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(0L);
		raca.setNome("raca test");
				
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.racaService.deleteById(raca.getId()))
			.thenReturn(true);
		
		mockMvc.perform(delete("/api/v1/raca/{id}", raca.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoDeletarUmaRaca_ErroAoDeletarARaca() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(1L);
		raca.setNome("raca test");
				
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.racaService.deleteById(raca.getId()))
			.thenReturn(false);
		
		mockMvc.perform(delete("/api/v1/raca/{id}", raca.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoDeletarUmaRaca_UsuarioDogwalker() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(0L);
		raca.setNome("raca test");
						
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.racaService.deleteById(raca.getId()))
			.thenReturn(false);
		
		mockMvc.perform(delete("/api/v1/raca/{id}", raca.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoDeletarUmaRaca_UsuarioTutor() throws Exception {
		
		Raca raca = new Raca();
		
		raca.setId(0L);
		raca.setNome("raca test");
						
		when(this.racaService.findById(raca.getId()))
			.thenReturn(raca);
		
		when(this.racaService.deleteById(raca.getId()))
			.thenReturn(false);
		
		mockMvc.perform(delete("/api/v1/raca/{id}", raca.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
}
