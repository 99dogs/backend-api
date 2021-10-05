package br.dogs.com.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.dogs.com.model.dto.safe2pay.callback.Callback;
import br.dogs.com.model.dto.safe2pay.callback.Origin;
import br.dogs.com.model.dto.safe2pay.callback.TransactionStatus;
import br.dogs.com.model.entities.Ticket;
import br.dogs.com.service.TicketService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class CallbackRestControllerTest {

	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@InjectMocks
	private CallbackRestController callbackRestController;
	
	@MockBean
	private TicketService ticketService;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void startMocks(){
		callbackRestController = context.getBean(CallbackRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(callbackRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(ticketService);
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCallbackSafe2Pay_TicketNaoExistente() throws Exception {
		
		Ticket ticket = new Ticket();
		Callback callback = new Callback();
		TransactionStatus transactionStatus = new TransactionStatus();
		Origin origin = new Origin();
		
		transactionStatus.setId(9999);
		origin.setSingleSaleHash("single-hash");
		
		callback.setOrigin(origin);
		callback.setTransactionStatus(transactionStatus);
		
		ticket.setFaturaId("faturaId");
		ticket.setFaturaUrl("faturaUrl");
		
		when(this.ticketService.buscarPorFaturaId(ticket.getFaturaId()))
			.thenReturn(null);
		
		String json = objectMapper.writeValueAsString(callback);
		
		mockMvc.perform(MockMvcRequestBuilders
			      .post("/api/v1/callback/safe2pay")
			      .contentType(MediaType.APPLICATION_JSON)
	              .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCallbackSafe2Pay_StatusNaoDefinido() throws Exception {
		
		Ticket ticket = new Ticket();
		Callback callback = new Callback();
		TransactionStatus transactionStatus = new TransactionStatus();
		Origin origin = new Origin();
		
		transactionStatus.setId(9999);
		origin.setSingleSaleHash("single-hash");
		
		callback.setOrigin(origin);
		callback.setTransactionStatus(transactionStatus);
		
		ticket.setId(1L);
		ticket.setFaturaId("single-hash");
		ticket.setFaturaUrl("faturaUrl");
		
		when(this.ticketService.buscarPorFaturaId(ticket.getFaturaId()))
			.thenReturn(ticket);
		
		String json = objectMapper.writeValueAsString(callback);
		
		mockMvc.perform(MockMvcRequestBuilders
			      .post("/api/v1/callback/safe2pay")
			      .contentType(MediaType.APPLICATION_JSON)
	              .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoCallbackSafe2Pay_ThrowException() throws Exception {
		
		Ticket ticket = new Ticket();
		Callback callback = new Callback();
		TransactionStatus transactionStatus = new TransactionStatus();
		Origin origin = new Origin();
		
		transactionStatus.setId(9999);
		origin.setSingleSaleHash("single-hash");
		
		callback.setOrigin(origin);
		callback.setTransactionStatus(transactionStatus);
		
		ticket.setId(1L);
		ticket.setFaturaId("single-hash");
		ticket.setFaturaUrl("faturaUrl");
		
		when(this.ticketService.buscarPorFaturaId(ticket.getFaturaId()))
			.thenAnswer((t)->{throw new Exception("bad request");});
		
		String json = objectMapper.writeValueAsString(callback);
		
		mockMvc.perform(MockMvcRequestBuilders
			      .post("/api/v1/callback/safe2pay")
			      .contentType(MediaType.APPLICATION_JSON)
	              .content(json))
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCallbackSafe2Pay_Pendente() throws Exception {
		
		Ticket ticket = new Ticket();
		Callback callback = new Callback();
		TransactionStatus transactionStatus = new TransactionStatus();
		Origin origin = new Origin();
		
		transactionStatus.setId(1);
		origin.setSingleSaleHash("single-hash");
		
		callback.setOrigin(origin);
		callback.setTransactionStatus(transactionStatus);
		
		ticket.setId(1L);
		ticket.setFaturaId("single-hash");
		ticket.setFaturaUrl("faturaUrl");
		ticket.setPendente(false);
		
		when(this.ticketService.buscarPorFaturaId(ticket.getFaturaId()))
			.thenReturn(ticket);
		
		when(this.ticketService.setarComoPendente(ticket.getId()))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(callback);
		
		mockMvc.perform(MockMvcRequestBuilders
			      .post("/api/v1/callback/safe2pay")
			      .contentType(MediaType.APPLICATION_JSON)
	              .content(json))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCallbackSafe2Pay_Pago() throws Exception {
		
		Ticket ticket = new Ticket();
		Callback callback = new Callback();
		TransactionStatus transactionStatus = new TransactionStatus();
		Origin origin = new Origin();
		
		transactionStatus.setId(3);
		origin.setSingleSaleHash("single-hash");
		
		callback.setOrigin(origin);
		callback.setTransactionStatus(transactionStatus);
		
		ticket.setId(1L);
		ticket.setFaturaId("single-hash");
		ticket.setFaturaUrl("faturaUrl");
		ticket.setPago(false);
		
		when(this.ticketService.buscarPorFaturaId(ticket.getFaturaId()))
			.thenReturn(ticket);
		
		when(this.ticketService.setarComoPago(ticket.getId()))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(callback);
		
		mockMvc.perform(MockMvcRequestBuilders
			      .post("/api/v1/callback/safe2pay")
			      .contentType(MediaType.APPLICATION_JSON)
	              .content(json))
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoCallbackSafe2Pay_Cancelado() throws Exception {
		
		Ticket ticket = new Ticket();
		Callback callback = new Callback();
		TransactionStatus transactionStatus = new TransactionStatus();
		Origin origin = new Origin();
		
		transactionStatus.setId(12);
		origin.setSingleSaleHash("single-hash");
		
		callback.setOrigin(origin);
		callback.setTransactionStatus(transactionStatus);
		
		ticket.setId(1L);
		ticket.setFaturaId("single-hash");
		ticket.setFaturaUrl("faturaUrl");
		ticket.setCancelado(false);
		
		when(this.ticketService.buscarPorFaturaId(ticket.getFaturaId()))
			.thenReturn(ticket);
		
		when(this.ticketService.setarComoCancelado(ticket.getId()))
			.thenReturn(true);
		
		String json = objectMapper.writeValueAsString(callback);
		
		mockMvc.perform(MockMvcRequestBuilders
			      .post("/api/v1/callback/safe2pay")
			      .contentType(MediaType.APPLICATION_JSON)
	              .content(json))
			      .andExpect(status().isOk());
		
	}
	
}
