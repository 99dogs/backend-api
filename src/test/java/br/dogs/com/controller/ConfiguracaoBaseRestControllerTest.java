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
import br.dogs.com.model.entities.ConfiguracaoBase;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.ConfiguracaoBaseService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class ConfiguracaoBaseRestControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private ConfiguracaoBaseRestController configuracaoBaseRestController;
	
	@MockBean
	private ConfiguracaoBaseService configuracaoBaseService;
	
	private Usuario authUsuarioAdmin = new Usuario();
	
	private String tokenUsuarioAdmin;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void startMocks(){
		configuracaoBaseRestController = context.getBean(ConfiguracaoBaseRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(configuracaoBaseRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(configuracaoBaseService);
		
		authUsuarioAdmin.setId(1L);
		authUsuarioAdmin.setEmail("admin");
		authUsuarioAdmin.setTipo(TipoUsuario.ADMIN.name());
		
		tokenUsuarioAdmin = jwtTokenProvider.createToken(authUsuarioAdmin);
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTodos() throws Exception {
		
		List<ConfiguracaoBase> configs = new ArrayList<>();
		
		when(this.configuracaoBaseService.buscarTodos())
			.thenReturn(configs);
		
		mockMvc.perform(get("/api/v1/configuracao-base")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCadastrarConfiguracao() throws Exception {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		List<ConfiguracaoBase> configs = new ArrayList<>();
		
		configuracao.setId(1L);
		configuracao.setTaxaPlataforma(0);
		configuracao.setTempoPasseio(0);
		configuracao.setValorMinimoDeposito(0);
		configuracao.setValorTicket(1);
		
		when(this.configuracaoBaseService.buscarTodos())
			.thenReturn(configs);
		
		when(this.configuracaoBaseService.cadastrar(Mockito.any(ConfiguracaoBase.class)))
			.thenReturn(configuracao);
		
		String json = objectMapper.writeValueAsString(configuracao);
		
		mockMvc.perform(post("/api/v1/configuracao-base")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarConfiguracao_TaxaPlataformaMenorQueZero() throws Exception {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		List<ConfiguracaoBase> configs = new ArrayList<>();
		
		configuracao.setId(1L);
		configuracao.setTaxaPlataforma(-1);
		configuracao.setTempoPasseio(0);
		configuracao.setValorMinimoDeposito(0);
		configuracao.setValorTicket(1);
		
		when(this.configuracaoBaseService.buscarTodos())
			.thenReturn(configs);
		
		when(this.configuracaoBaseService.cadastrar(Mockito.any(ConfiguracaoBase.class)))
			.thenReturn(configuracao);
		
		String json = objectMapper.writeValueAsString(configuracao);
		
		mockMvc.perform(post("/api/v1/configuracao-base")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarConfiguracao_TempoPasseioMenorQueZero() throws Exception {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		List<ConfiguracaoBase> configs = new ArrayList<>();
		
		configuracao.setId(1L);
		configuracao.setTaxaPlataforma(0);
		configuracao.setTempoPasseio(-1);
		configuracao.setValorMinimoDeposito(0);
		configuracao.setValorTicket(1);
		
		when(this.configuracaoBaseService.buscarTodos())
			.thenReturn(configs);
		
		when(this.configuracaoBaseService.cadastrar(Mockito.any(ConfiguracaoBase.class)))
			.thenReturn(configuracao);
		
		String json = objectMapper.writeValueAsString(configuracao);
		
		mockMvc.perform(post("/api/v1/configuracao-base")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarConfiguracao_ValorMinimoDepositoMenorQueZero() throws Exception {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		List<ConfiguracaoBase> configs = new ArrayList<>();
		
		configuracao.setId(1L);
		configuracao.setTaxaPlataforma(0);
		configuracao.setTempoPasseio(0);
		configuracao.setValorMinimoDeposito(-1);
		configuracao.setValorTicket(1);
		
		when(this.configuracaoBaseService.buscarTodos())
			.thenReturn(configs);
		
		when(this.configuracaoBaseService.cadastrar(Mockito.any(ConfiguracaoBase.class)))
			.thenReturn(configuracao);
		
		String json = objectMapper.writeValueAsString(configuracao);
		
		mockMvc.perform(post("/api/v1/configuracao-base")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarConfiguracao_ValorTicketMenorQueHum() throws Exception {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		List<ConfiguracaoBase> configs = new ArrayList<>();
		
		configuracao.setId(1L);
		configuracao.setTaxaPlataforma(0);
		configuracao.setTempoPasseio(0);
		configuracao.setValorMinimoDeposito(0);
		configuracao.setValorTicket(0);
		
		when(this.configuracaoBaseService.buscarTodos())
			.thenReturn(configs);
		
		when(this.configuracaoBaseService.cadastrar(Mockito.any(ConfiguracaoBase.class)))
			.thenReturn(configuracao);
		
		String json = objectMapper.writeValueAsString(configuracao);
		
		mockMvc.perform(post("/api/v1/configuracao-base")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoAlterarConfiguracao() throws Exception {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		
		configuracao.setId(1L);
		configuracao.setTaxaPlataforma(0);
		configuracao.setTempoPasseio(0);
		configuracao.setValorMinimoDeposito(0);
		configuracao.setValorTicket(1);
		
		when(this.configuracaoBaseService.buscarPorId(configuracao.getId()))
			.thenReturn(configuracao);
		
		when(this.configuracaoBaseService.alterar(Mockito.any(ConfiguracaoBase.class)))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(configuracao);
		
		mockMvc.perform(put("/api/v1/configuracao-base/{id}", configuracao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRquest_QuandoAlterarConfiguracao_ConfiguracaoNaoExistente() throws Exception {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		
		configuracao.setId(1L);
		configuracao.setTaxaPlataforma(0);
		configuracao.setTempoPasseio(0);
		configuracao.setValorMinimoDeposito(0);
		configuracao.setValorTicket(1);
		
		when(this.configuracaoBaseService.buscarPorId(configuracao.getId()))
			.thenReturn(new ConfiguracaoBase());
		
		when(this.configuracaoBaseService.alterar(Mockito.any(ConfiguracaoBase.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(configuracao);
		
		mockMvc.perform(put("/api/v1/configuracao-base/{id}", configuracao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarBadRquest_QuandoAlterarConfiguracaoTaxaPlataformaMenorQueZero() throws Exception {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		
		configuracao.setId(1L);
		configuracao.setTaxaPlataforma(-1);
		configuracao.setTempoPasseio(0);
		configuracao.setValorMinimoDeposito(0);
		configuracao.setValorTicket(1);
		
		when(this.configuracaoBaseService.buscarPorId(configuracao.getId()))
			.thenReturn(configuracao);
		
		when(this.configuracaoBaseService.alterar(Mockito.any(ConfiguracaoBase.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(configuracao);
		
		mockMvc.perform(put("/api/v1/configuracao-base/{id}", configuracao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRquest_QuandoAlterarConfiguracaoTempoPasseioMenorQueZero() throws Exception {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		
		configuracao.setId(1L);
		configuracao.setTaxaPlataforma(0);
		configuracao.setTempoPasseio(-1);
		configuracao.setValorMinimoDeposito(0);
		configuracao.setValorTicket(1);
		
		when(this.configuracaoBaseService.buscarPorId(configuracao.getId()))
			.thenReturn(configuracao);
		
		when(this.configuracaoBaseService.alterar(Mockito.any(ConfiguracaoBase.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(configuracao);
		
		mockMvc.perform(put("/api/v1/configuracao-base/{id}", configuracao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRquest_QuandoAlterarConfiguracaoValorMinimoDepositoMenorQueZero() throws Exception {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		
		configuracao.setId(1L);
		configuracao.setTaxaPlataforma(0);
		configuracao.setTempoPasseio(0);
		configuracao.setValorMinimoDeposito(-1);
		configuracao.setValorTicket(1);
		
		when(this.configuracaoBaseService.buscarPorId(configuracao.getId()))
			.thenReturn(configuracao);
		
		when(this.configuracaoBaseService.alterar(Mockito.any(ConfiguracaoBase.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(configuracao);
		
		mockMvc.perform(put("/api/v1/configuracao-base/{id}", configuracao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRquest_QuandoAlterarConfiguracaoValorTicketMenorQueHum() throws Exception {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		
		configuracao.setId(1L);
		configuracao.setTaxaPlataforma(0);
		configuracao.setTempoPasseio(0);
		configuracao.setValorMinimoDeposito(0);
		configuracao.setValorTicket(0);
		
		when(this.configuracaoBaseService.buscarPorId(configuracao.getId()))
			.thenReturn(configuracao);
		
		when(this.configuracaoBaseService.alterar(Mockito.any(ConfiguracaoBase.class)))
			.thenReturn(false);
		
		String json = objectMapper.writeValueAsString(configuracao);
		
		mockMvc.perform(put("/api/v1/configuracao-base/{id}", configuracao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoDeletarConfiguracao() throws Exception {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		
		configuracao.setId(1L);
		configuracao.setTaxaPlataforma(0);
		configuracao.setTempoPasseio(0);
		configuracao.setValorMinimoDeposito(0);
		configuracao.setValorTicket(0);
		
		when(this.configuracaoBaseService.buscarPorId(configuracao.getId()))
			.thenReturn(configuracao);
		
		when(this.configuracaoBaseService.deletarPorId(configuracao.getId()))
			.thenReturn(true);
				
		mockMvc.perform(delete("/api/v1/configuracao-base/{id}", configuracao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoDeletarConfiguracao_ConfiguracaoNaoExistente() throws Exception {
		
		ConfiguracaoBase configuracao = new ConfiguracaoBase();
		
		configuracao.setId(1L);
		configuracao.setTaxaPlataforma(0);
		configuracao.setTempoPasseio(0);
		configuracao.setValorMinimoDeposito(0);
		configuracao.setValorTicket(0);
		
		when(this.configuracaoBaseService.buscarPorId(configuracao.getId()))
			.thenReturn(new ConfiguracaoBase());
		
		when(this.configuracaoBaseService.deletarPorId(configuracao.getId()))
			.thenReturn(false);
				
		mockMvc.perform(delete("/api/v1/configuracao-base/{id}", configuracao.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin))
			      .andDo(print())
			      .andExpect(status().isNotFound());
		
	}
	
}
