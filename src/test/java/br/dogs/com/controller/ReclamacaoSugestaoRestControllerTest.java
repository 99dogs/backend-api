package br.dogs.com.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import br.dogs.com.model.entities.ReclamacaoSugestao;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.ReclamacaoSugestaoService;
import br.dogs.com.service.UsuarioService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class ReclamacaoSugestaoRestControllerTest {

private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private ReclamacaoSugestaoRestController reclamacaoSugestaoRestController;
	
	@MockBean
	private ReclamacaoSugestaoService reclamacaoSugestaoService;
	
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
		reclamacaoSugestaoRestController = context.getBean(ReclamacaoSugestaoRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(reclamacaoSugestaoRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(reclamacaoSugestaoService);
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
		
		List<ReclamacaoSugestao> items = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.reclamacaoSugestaoService.buscarTodos(authUsuarioDogwalker.getId()))
			.thenReturn(items);
		
		mockMvc.perform(get("/api/v1/reclamacao-sugestao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTodos_UsuarioTutor() throws Exception {
		
		List<ReclamacaoSugestao> items = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.reclamacaoSugestaoService.buscarTodos(authUsuarioTutor.getId()))
			.thenReturn(items);
		
		mockMvc.perform(get("/api/v1/reclamacao-sugestao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoBuscarTodos_UsuarioAdmin() throws Exception {
		
		List<ReclamacaoSugestao> items = new ArrayList<>();
				
		when(this.reclamacaoSugestaoService.buscarTodos(authUsuarioAdmin.getId()))
			.thenReturn(items);
		
		mockMvc.perform(get("/api/v1/reclamacao-sugestao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarTodos_ThrowException() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.reclamacaoSugestaoService.buscarTodos(authUsuarioDogwalker.getId()))
			.thenAnswer((t) -> { throw new Exception("bad request"); });
		
		mockMvc.perform(get("/api/v1/reclamacao-sugestao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCadastrarReclamacaoSugestao() throws Exception {
		
		ReclamacaoSugestao item = new ReclamacaoSugestao();
		
		item.setId(1L);
		item.setAssunto("assunto test");
		item.setMensagem("msg test");
		item.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.reclamacaoSugestaoService.cadastrar(Mockito.any(ReclamacaoSugestao.class)))
			.thenReturn(item);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("assunto", item.getAssunto());
		dados.put("mensagem", item.getMensagem());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/reclamacao-sugestao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarReclamacaoSugestao_SemAssunto() throws Exception {
		
		ReclamacaoSugestao item = new ReclamacaoSugestao();
		
		item.setId(1L);
		item.setAssunto("assunto test");
		item.setMensagem("msg test");
		item.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.reclamacaoSugestaoService.cadastrar(Mockito.any(ReclamacaoSugestao.class)))
			.thenReturn(item);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("assunto", null);
		dados.put("mensagem", item.getMensagem());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/reclamacao-sugestao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarReclamacaoSugestao_SemMensagem() throws Exception {
		
		ReclamacaoSugestao item = new ReclamacaoSugestao();
		
		item.setId(1L);
		item.setAssunto("assunto test");
		item.setMensagem("msg test");
		item.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.reclamacaoSugestaoService.cadastrar(Mockito.any(ReclamacaoSugestao.class)))
			.thenReturn(item);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("assunto", item.getAssunto());
		dados.put("mensagem", null);
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/reclamacao-sugestao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarReclamacaoSugestao_ErroAoCadastrar() throws Exception {
		
		ReclamacaoSugestao item = new ReclamacaoSugestao();
		
		item.setId(null);
		item.setAssunto("assunto test");
		item.setMensagem("msg test");
		item.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.reclamacaoSugestaoService.cadastrar(Mockito.any(ReclamacaoSugestao.class)))
			.thenReturn(item);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("assunto", item.getAssunto());
		dados.put("mensagem", item.getAssunto());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/reclamacao-sugestao")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoDeletarReclamacaoSugestao() throws Exception {
		
		ReclamacaoSugestao item = new ReclamacaoSugestao();
		
		item.setId(1L);
		item.setAssunto("assunto test");
		item.setMensagem("msg test");
		item.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.reclamacaoSugestaoService.buscarPorId(item.getId()))
			.thenReturn(item);
		
		when(this.reclamacaoSugestaoService.deletarPorId(item.getId()))
			.thenReturn(true);
		
		mockMvc.perform(delete("/api/v1/reclamacao-sugestao/{id}", item.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoDeletarReclamacaoSugestao_ItemNaoExistente() throws Exception {
		
		ReclamacaoSugestao item = new ReclamacaoSugestao();
		
		item.setId(1L);
		item.setAssunto("assunto test");
		item.setMensagem("msg test");
		item.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.reclamacaoSugestaoService.buscarPorId(item.getId()))
			.thenReturn(null);
		
		when(this.reclamacaoSugestaoService.deletarPorId(item.getId()))
			.thenReturn(false);
		
		mockMvc.perform(delete("/api/v1/reclamacao-sugestao/{id}", item.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoDeletarReclamacaoSugestao_UsuarioDivergente() throws Exception {
		
		ReclamacaoSugestao item = new ReclamacaoSugestao();
		
		item.setId(1L);
		item.setAssunto("assunto test");
		item.setMensagem("msg test");
		item.setUsuarioId(999L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.reclamacaoSugestaoService.buscarPorId(item.getId()))
			.thenReturn(item);
		
		when(this.reclamacaoSugestaoService.deletarPorId(item.getId()))
			.thenReturn(false);
		
		mockMvc.perform(delete("/api/v1/reclamacao-sugestao/{id}", item.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoDeletarReclamacaoSugestao_ErroAoDeletarItem() throws Exception {
		
		ReclamacaoSugestao item = new ReclamacaoSugestao();
		
		item.setId(1L);
		item.setAssunto("assunto test");
		item.setMensagem("msg test");
		item.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.reclamacaoSugestaoService.buscarPorId(item.getId()))
			.thenReturn(item);
		
		when(this.reclamacaoSugestaoService.deletarPorId(item.getId()))
			.thenReturn(false);
		
		mockMvc.perform(delete("/api/v1/reclamacao-sugestao/{id}", item.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoDeletarReclamacaoSugestao_ThrowException() throws Exception {
		
		ReclamacaoSugestao item = new ReclamacaoSugestao();
		
		item.setId(1L);
		item.setAssunto("assunto test");
		item.setMensagem("msg test");
		item.setUsuarioId(authUsuarioDogwalker.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.reclamacaoSugestaoService.buscarPorId(item.getId()))
			.thenReturn(item);
		
		when(this.reclamacaoSugestaoService.deletarPorId(item.getId()))
			.thenAnswer((t) -> {throw new Exception("bad request");});
		
		mockMvc.perform(delete("/api/v1/reclamacao-sugestao/{id}", item.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
}
