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
import br.dogs.com.model.entities.FormaDePagamento;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.FormaDePagamentoService;
import br.dogs.com.service.UsuarioService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class FormaDePagamentoRestControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private FormaDePagamentoRestController formaDePagamentoRestController;
	
	@MockBean
	private FormaDePagamentoService formaDePagamentoService;
	
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
		formaDePagamentoRestController = context.getBean(FormaDePagamentoRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(formaDePagamentoRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(formaDePagamentoService);
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
	public void deveRetornarSucesso_QuandoBuscarTodos_UsuarioAdmin() throws Exception {
		
		List<FormaDePagamento> pagamentos = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.buscarTodos())
			.thenReturn(pagamentos);
		
		mockMvc.perform(get("/api/v1/forma-de-pagamento")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTodos_UsuarioTutor() throws Exception {
		
		List<FormaDePagamento> pagamentos = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.formaDePagamentoService.buscarTodos())
			.thenReturn(pagamentos);
		
		mockMvc.perform(get("/api/v1/forma-de-pagamento")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarForbidden_QuandoBuscarTodos_UsuarioDogwalker() throws Exception {
		
		List<FormaDePagamento> pagamentos = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioDogwalker.getEmail()))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.formaDePagamentoService.buscarTodos())
			.thenReturn(pagamentos);
		
		mockMvc.perform(get("/api/v1/forma-de-pagamento")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isForbidden());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarPorId() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.buscarPorId(pagamento.getId()))
			.thenReturn(pagamento);
		
		mockMvc.perform(get("/api/v1/forma-de-pagamento/{id}", pagamento.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoBuscarPorId_FormaDePagamentoNaoExistente() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.buscarPorId(pagamento.getId()))
			.thenReturn(new FormaDePagamento());
		
		mockMvc.perform(get("/api/v1/forma-de-pagamento/{id}", pagamento.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCadastrarUmaFormaDePagamento() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.cadastrar(Mockito.any(FormaDePagamento.class)))
			.thenReturn(pagamento);
		
		String json = objectMapper.writeValueAsString(pagamento);
		
		mockMvc.perform(post("/api/v1/forma-de-pagamento")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoCadastrarUmaFormaDePagamento_UsuarioTutor() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.cadastrar(Mockito.any(FormaDePagamento.class)))
			.thenReturn(pagamento);
		
		String json = objectMapper.writeValueAsString(pagamento);
		
		mockMvc.perform(post("/api/v1/forma-de-pagamento")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoCadastrarUmaFormaDePagamento_UsuarioDogwalker() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.cadastrar(Mockito.any(FormaDePagamento.class)))
			.thenReturn(pagamento);
		
		String json = objectMapper.writeValueAsString(pagamento);
		
		mockMvc.perform(post("/api/v1/forma-de-pagamento")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarUmaFormaDePagamento_ComNomeVazio() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.cadastrar(Mockito.any(FormaDePagamento.class)))
			.thenReturn(pagamento);
		
		String json = objectMapper.writeValueAsString(pagamento);
		
		mockMvc.perform(post("/api/v1/forma-de-pagamento")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarUmaFormaDePagamento_ComTipoVazio() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.cadastrar(Mockito.any(FormaDePagamento.class)))
			.thenReturn(pagamento);
		
		String json = objectMapper.writeValueAsString(pagamento);
		
		mockMvc.perform(post("/api/v1/forma-de-pagamento")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoAlterarUmaFormaDePagamento() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.buscarPorId(pagamento.getId()))
			.thenReturn(pagamento);
		
		when(this.formaDePagamentoService.alterar(Mockito.any(FormaDePagamento.class)))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(pagamento);
		
		mockMvc.perform(put("/api/v1/forma-de-pagamento/{id}", pagamento.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarUnathorized_QuandoAlterarUmaFormaDePagamento_UsuarioTutor() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.buscarPorId(pagamento.getId()))
			.thenReturn(null);
		
		when(this.formaDePagamentoService.alterar(Mockito.any(FormaDePagamento.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(pagamento);
		
		mockMvc.perform(put("/api/v1/forma-de-pagamento/{id}", pagamento.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarUnathorized_QuandoAlterarUmaFormaDePagamento_UsuarioDogwalker() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.buscarPorId(pagamento.getId()))
			.thenReturn(null);
		
		when(this.formaDePagamentoService.alterar(Mockito.any(FormaDePagamento.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(pagamento);
		
		mockMvc.perform(put("/api/v1/forma-de-pagamento/{id}", pagamento.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoAlterarUmaFormaDePagamento_ItemNaoExistente() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.buscarPorId(pagamento.getId()))
			.thenReturn(null);
		
		when(this.formaDePagamentoService.alterar(Mockito.any(FormaDePagamento.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(pagamento);
		
		mockMvc.perform(put("/api/v1/forma-de-pagamento/{id}", pagamento.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarUmaFormaDePagamento_ComNomevazio() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.buscarPorId(pagamento.getId()))
			.thenReturn(pagamento);
		
		when(this.formaDePagamentoService.alterar(Mockito.any(FormaDePagamento.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(pagamento);
		
		mockMvc.perform(put("/api/v1/forma-de-pagamento/{id}", pagamento.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoAlterarUmaFormaDePagamento_ComTipovazio() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.buscarPorId(pagamento.getId()))
			.thenReturn(pagamento);
		
		when(this.formaDePagamentoService.alterar(Mockito.any(FormaDePagamento.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(pagamento);
		
		mockMvc.perform(put("/api/v1/forma-de-pagamento/{id}", pagamento.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoDeletarUmaFormaDePagamento() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.buscarPorId(pagamento.getId()))
			.thenReturn(pagamento);
		
		when(this.formaDePagamentoService.deletarPorId(pagamento.getId()))
			.thenReturn(true);
				
		mockMvc.perform(delete("/api/v1/forma-de-pagamento/{id}", pagamento.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoDeletarUmaFormaDePagamento_UsuarioTutor() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.buscarPorId(pagamento.getId()))
			.thenReturn(pagamento);
		
		when(this.formaDePagamentoService.deletarPorId(pagamento.getId()))
			.thenReturn(false);
				
		mockMvc.perform(delete("/api/v1/forma-de-pagamento/{id}", pagamento.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoDeletarUmaFormaDePagamento_UsuarioDogwalker() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.buscarPorId(pagamento.getId()))
			.thenReturn(pagamento);
		
		when(this.formaDePagamentoService.deletarPorId(pagamento.getId()))
			.thenReturn(false);
				
		mockMvc.perform(delete("/api/v1/forma-de-pagamento/{id}", pagamento.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker))
			      .andDo(print())
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoDeletarUmaFormaDePagamento_ItemNaoExistente() throws Exception {
		
		FormaDePagamento pagamento = new FormaDePagamento();
		
		pagamento.setId(1L);
		pagamento.setNome("Boleto");
		pagamento.setTipo("1");
		pagamento.setAtivo(true);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.formaDePagamentoService.buscarPorId(pagamento.getId()))
			.thenReturn(null);
		
		when(this.formaDePagamentoService.deletarPorId(pagamento.getId()))
			.thenReturn(false);
				
		mockMvc.perform(delete("/api/v1/forma-de-pagamento/{id}", pagamento.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
}
