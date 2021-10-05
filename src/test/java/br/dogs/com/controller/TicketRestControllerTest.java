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
import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.dto.TicketFatura;
import br.dogs.com.model.entities.FormaDePagamento;
import br.dogs.com.model.entities.Ticket;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.FormaDePagamentoService;
import br.dogs.com.service.TicketService;
import br.dogs.com.service.UsuarioService;
import br.dogs.com.service.payment.Safe2PayPaymentService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class TicketRestControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private TicketRestController ticketRestController;
	
	@MockBean
	private TicketService ticketService;
	
	@MockBean
	private FormaDePagamentoService formaDePagamentoService;
	
	@MockBean
	private Safe2PayPaymentService safe2PayPaymentService;
	
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
		ticketRestController = context.getBean(TicketRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(ticketRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(ticketService);
		standaloneSetup(formaDePagamentoService);
		standaloneSetup(safe2PayPaymentService);
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
	public void deveRetornarSucesso_QuandoBuscarTodos_UsuarioTutor() throws Exception {
		
		List<Ticket> tickets = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarTodos(authUsuarioTutor.getId()))
			.thenReturn(tickets);
		
		mockMvc.perform(get("/api/v1/ticket")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarTodos_UsuarioAdmin() throws Exception {
		
		List<Ticket> tickets = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername(authUsuarioAdmin.getEmail()))
			.thenReturn(authUsuarioAdmin);
		
		when(this.ticketService.buscarTodos(authUsuarioAdmin.getId()))
			.thenReturn(tickets);
		
		mockMvc.perform(get("/api/v1/ticket")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioAdmin)
			      .accept(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarTodos_ThrowException() throws Exception {
				
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarTodos(authUsuarioTutor.getId()))
			.thenAnswer((t) -> {throw new Exception("bad request");});
		
		mockMvc.perform(get("/api/v1/ticket")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoBuscarTodos_UsuarioNaoPermitido() throws Exception {
		
		List<Ticket> tickets = new ArrayList<>();
		
		when(this.ticketService.buscarTodos(authUsuarioTutor.getId()))
			.thenReturn(tickets);
		
		mockMvc.perform(get("/api/v1/ticket")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarPorId() throws Exception {
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticket.getId()))
			.thenReturn(ticket);
		
		mockMvc.perform(get("/api/v1/ticket/{id}", ticket.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoBuscarPorId_TicketNaoExistente() throws Exception {
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticket.getId()))
			.thenReturn(null);
		
		mockMvc.perform(get("/api/v1/ticket/{id}", ticket.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoBuscarPorId_UsuarioDivergente() throws Exception {
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setUsuarioId(999L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticket.getId()))
			.thenReturn(ticket);
		
		mockMvc.perform(get("/api/v1/ticket/{id}", ticket.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarPorId_ThrowException() throws Exception {
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setUsuarioId(999L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticket.getId()))
			.thenAnswer((t) -> {throw new Exception("bad request");});
		
		mockMvc.perform(get("/api/v1/ticket/{id}", ticket.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCadastrarTicket() throws Exception {
		
		Ticket ticket = new Ticket();
		FormaDePagamento formaPagamento = new FormaDePagamento();
		
		formaPagamento.setId(2L);
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setFormaDePagamentoId(formaPagamento.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.formaDePagamentoService.buscarPorId(formaPagamento.getId()))
			.thenReturn(formaPagamento);
		
		when(this.ticketService.cadastrar(Mockito.any(Ticket.class)))
			.thenReturn(ticket);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("quantidade", ticket.getQuantidade());
		dados.put("unitario", ticket.getUnitario());
		dados.put("total", ticket.getTotal());
		dados.put("formaDePagamentoId", ticket.getFormaDePagamentoId());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/ticket")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarTicket_QuantidadeMenorQueHum() throws Exception {
		
		Ticket ticket = new Ticket();
		FormaDePagamento formaPagamento = new FormaDePagamento();
		
		formaPagamento.setId(2L);
		
		ticket.setId(1L);
		ticket.setQuantidade(0);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setFormaDePagamentoId(formaPagamento.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.formaDePagamentoService.buscarPorId(formaPagamento.getId()))
			.thenReturn(formaPagamento);
		
		when(this.ticketService.cadastrar(Mockito.any(Ticket.class)))
			.thenReturn(ticket);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("quantidade", ticket.getQuantidade());
		dados.put("unitario", ticket.getUnitario());
		dados.put("total", ticket.getTotal());
		dados.put("formaDePagamentoId", ticket.getFormaDePagamentoId());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/ticket")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarTicket_UnitarioMenorQueHum() throws Exception {
		
		Ticket ticket = new Ticket();
		FormaDePagamento formaPagamento = new FormaDePagamento();
		
		formaPagamento.setId(2L);
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(0);
		ticket.setTotal(1);
		ticket.setFormaDePagamentoId(formaPagamento.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.formaDePagamentoService.buscarPorId(formaPagamento.getId()))
			.thenReturn(formaPagamento);
		
		when(this.ticketService.cadastrar(Mockito.any(Ticket.class)))
			.thenReturn(ticket);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("quantidade", ticket.getQuantidade());
		dados.put("unitario", ticket.getUnitario());
		dados.put("total", ticket.getTotal());
		dados.put("formaDePagamentoId", ticket.getFormaDePagamentoId());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/ticket")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarTicket_TotalMenorQueHum() throws Exception {
		
		Ticket ticket = new Ticket();
		FormaDePagamento formaPagamento = new FormaDePagamento();
		
		formaPagamento.setId(2L);
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(0);
		ticket.setFormaDePagamentoId(formaPagamento.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.formaDePagamentoService.buscarPorId(formaPagamento.getId()))
			.thenReturn(formaPagamento);
		
		when(this.ticketService.cadastrar(Mockito.any(Ticket.class)))
			.thenReturn(ticket);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("quantidade", ticket.getQuantidade());
		dados.put("unitario", ticket.getUnitario());
		dados.put("total", ticket.getTotal());
		dados.put("formaDePagamentoId", ticket.getFormaDePagamentoId());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/ticket")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoCadastrarTicket_FormaPagamentoNaoExistente() throws Exception {
		
		Ticket ticket = new Ticket();
		FormaDePagamento formaPagamento = new FormaDePagamento();
		
		formaPagamento.setId(2L);
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setFormaDePagamentoId(formaPagamento.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.formaDePagamentoService.buscarPorId(formaPagamento.getId()))
			.thenReturn(null);
		
		when(this.ticketService.cadastrar(Mockito.any(Ticket.class)))
			.thenReturn(ticket);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("quantidade", ticket.getQuantidade());
		dados.put("unitario", ticket.getUnitario());
		dados.put("total", ticket.getTotal());
		dados.put("formaDePagamentoId", ticket.getFormaDePagamentoId());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/ticket")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarTicket_ErroAoCadastrarTicket() throws Exception {
		
		Ticket ticket = new Ticket();
		FormaDePagamento formaPagamento = new FormaDePagamento();
		
		formaPagamento.setId(2L);
		
		ticket.setId(0L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setFormaDePagamentoId(formaPagamento.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.formaDePagamentoService.buscarPorId(formaPagamento.getId()))
			.thenReturn(formaPagamento);
		
		when(this.ticketService.cadastrar(Mockito.any(Ticket.class)))
			.thenReturn(ticket);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("quantidade", ticket.getQuantidade());
		dados.put("unitario", ticket.getUnitario());
		dados.put("total", ticket.getTotal());
		dados.put("formaDePagamentoId", ticket.getFormaDePagamentoId());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/ticket")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCadastrarTicket_ThrowException() throws Exception {
		
		Ticket ticket = new Ticket();
		FormaDePagamento formaPagamento = new FormaDePagamento();
		
		formaPagamento.setId(2L);
		
		ticket.setId(0L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setFormaDePagamentoId(formaPagamento.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.formaDePagamentoService.buscarPorId(formaPagamento.getId()))
			.thenReturn(formaPagamento);
		
		when(this.ticketService.cadastrar(Mockito.any(Ticket.class)))
			.thenAnswer((t)-> {throw new Exception("bad request");});
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("quantidade", ticket.getQuantidade());
		dados.put("unitario", ticket.getUnitario());
		dados.put("total", ticket.getTotal());
		dados.put("formaDePagamentoId", ticket.getFormaDePagamentoId());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/ticket")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoDeletarUmTicket() throws Exception {
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticket.getId()))
			.thenReturn(ticket);
		
		when(this.ticketService.deletarPorId(ticket.getId()))
			.thenReturn(true);
				
		mockMvc.perform(delete("/api/v1/ticket/{id}", ticket.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarNotFound_QuandoDeletarUmTicket_TicketNaoExistente() throws Exception {
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticket.getId()))
			.thenReturn(null);
		
		when(this.ticketService.deletarPorId(ticket.getId()))
			.thenReturn(false);
				
		mockMvc.perform(delete("/api/v1/ticket/{id}", ticket.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoDeletarUmTicket_UsuarioDivergente() throws Exception {
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setUsuarioId(999L);
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticket.getId()))
			.thenReturn(ticket);
		
		when(this.ticketService.deletarPorId(ticket.getId()))
			.thenReturn(false);
				
		mockMvc.perform(delete("/api/v1/ticket/{id}", ticket.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoDeletarUmTicket_ErroAoDeletarOTicket() throws Exception {
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticket.getId()))
			.thenReturn(ticket);
		
		when(this.ticketService.deletarPorId(ticket.getId()))
			.thenReturn(false);
				
		mockMvc.perform(delete("/api/v1/ticket/{id}", ticket.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoDeletarUmTicket_ThrowException() throws Exception {
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setUsuarioId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticket.getId()))
			.thenReturn(ticket);
		
		when(this.ticketService.deletarPorId(ticket.getId()))
			.thenAnswer((t) -> {throw new Exception("bad request");});
				
		mockMvc.perform(delete("/api/v1/ticket/{id}", ticket.getId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoFaturarTicket() throws Exception {
		
		ResponseData responseData = new ResponseData();
		
		responseData.setTemErro(false);
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setUsuarioId(authUsuarioTutor.getId());
		
		TicketFatura ticketFatura = new TicketFatura();
		
		ticketFatura.setTicketId(ticket.getId());
		ticketFatura.setCpfPagador("00000000000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticketFatura.getTicketId()))
			.thenReturn(ticket);
		
		when(this.ticketService.faturar(Mockito.any(TicketFatura.class)))
			.thenReturn(responseData);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("ticketId", ticketFatura.getTicketId());
		dados.put("cpfPagador", ticketFatura.getCpfPagador());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/ticket/faturar")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoFaturarTicket_TicketIdIncorreto() throws Exception {
		
		ResponseData responseData = new ResponseData();
		
		responseData.setTemErro(false);
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setUsuarioId(authUsuarioTutor.getId());
		
		TicketFatura ticketFatura = new TicketFatura();
		
		ticketFatura.setTicketId(0L);
		ticketFatura.setCpfPagador("00000000000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticketFatura.getTicketId()))
			.thenReturn(ticket);
		
		when(this.ticketService.faturar(Mockito.any(TicketFatura.class)))
			.thenReturn(responseData);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("ticketId", ticketFatura.getTicketId());
		dados.put("cpfPagador", ticketFatura.getCpfPagador());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/ticket/faturar")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoFaturarTicket_CpfPagadorVazio() throws Exception {
		
		ResponseData responseData = new ResponseData();
		
		responseData.setTemErro(false);
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setUsuarioId(authUsuarioTutor.getId());
		
		TicketFatura ticketFatura = new TicketFatura();
		
		ticketFatura.setTicketId(ticket.getId());
		ticketFatura.setCpfPagador("");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticketFatura.getTicketId()))
			.thenReturn(ticket);
		
		when(this.ticketService.faturar(Mockito.any(TicketFatura.class)))
			.thenReturn(responseData);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("ticketId", ticketFatura.getTicketId());
		dados.put("cpfPagador", ticketFatura.getCpfPagador());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/ticket/faturar")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarUnauthorized_QuandoFaturarTicket_UsuarioDivergente() throws Exception {
		
		ResponseData responseData = new ResponseData();
		
		responseData.setTemErro(false);
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setUsuarioId(999L);
		
		TicketFatura ticketFatura = new TicketFatura();
		
		ticketFatura.setTicketId(ticket.getId());
		ticketFatura.setCpfPagador("000000000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticketFatura.getTicketId()))
			.thenReturn(ticket);
		
		when(this.ticketService.faturar(Mockito.any(TicketFatura.class)))
			.thenReturn(responseData);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("ticketId", ticketFatura.getTicketId());
		dados.put("cpfPagador", ticketFatura.getCpfPagador());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/ticket/faturar")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isUnauthorized());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoFaturarTicket_ErroAoFaturarTicket() throws Exception {
		
		ResponseData responseData = new ResponseData();
		
		responseData.setTemErro(true);
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setUsuarioId(authUsuarioTutor.getId());
		
		TicketFatura ticketFatura = new TicketFatura();
		
		ticketFatura.setTicketId(ticket.getId());
		ticketFatura.setCpfPagador("000000000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticketFatura.getTicketId()))
			.thenReturn(ticket);
		
		when(this.ticketService.faturar(Mockito.any(TicketFatura.class)))
			.thenReturn(responseData);
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("ticketId", ticketFatura.getTicketId());
		dados.put("cpfPagador", ticketFatura.getCpfPagador());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/ticket/faturar")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoFaturarTicket_ThrowException() throws Exception {
		
		ResponseData responseData = new ResponseData();
		
		responseData.setTemErro(false);
		
		Ticket ticket = new Ticket();
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setUsuarioId(authUsuarioTutor.getId());
		
		TicketFatura ticketFatura = new TicketFatura();
		
		ticketFatura.setTicketId(ticket.getId());
		ticketFatura.setCpfPagador("000000000");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorId(ticketFatura.getTicketId()))
			.thenReturn(ticket);
		
		when(this.ticketService.faturar(Mockito.any(TicketFatura.class)))
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		Map<String, Object> dados = new HashMap<>();
		
		dados.put("ticketId", ticketFatura.getTicketId());
		dados.put("cpfPagador", ticketFatura.getCpfPagador());
		
		String json = objectMapper.writeValueAsString(dados);
		
		mockMvc.perform(post("/api/v1/ticket/faturar")
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCancelarFatura() throws Exception {
		
		Ticket ticket = new Ticket();
		
		ticket.setId(2L);
		ticket.setFaturaId("12984912849");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorFaturaId(ticket.getFaturaId()))
			.thenReturn(ticket);
		
		when(this.safe2PayPaymentService.cancelarFatura(ticket))
			.thenReturn(true);
		
		mockMvc.perform(delete("/api/v1/ticket/cancelar/{faturaId}", ticket.getFaturaId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCancelarFatura_ErroAoCancelarFatura() throws Exception {
		
		Ticket ticket = new Ticket();
		
		ticket.setId(2L);
		ticket.setFaturaId("12984912849");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorFaturaId(ticket.getFaturaId()))
			.thenReturn(ticket);
		
		when(this.safe2PayPaymentService.cancelarFatura(ticket))
			.thenReturn(false);
		
		mockMvc.perform(delete("/api/v1/ticket/cancelar/{faturaId}", ticket.getFaturaId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCancelarFatura_ThrowException() throws Exception {
		
		Ticket ticket = new Ticket();
		
		ticket.setId(2L);
		ticket.setFaturaId("12984912849");
		
		when(this.usuarioService.loadUserByUsername(authUsuarioTutor.getEmail()))
			.thenReturn(authUsuarioTutor);
		
		when(this.ticketService.buscarPorFaturaId(ticket.getFaturaId()))
			.thenReturn(ticket);
		
		when(this.safe2PayPaymentService.cancelarFatura(ticket))
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		mockMvc.perform(delete("/api/v1/ticket/cancelar/{faturaId}", ticket.getFaturaId())
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());
		
	}
	
}
