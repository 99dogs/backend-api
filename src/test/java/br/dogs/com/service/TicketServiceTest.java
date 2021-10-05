package br.dogs.com.service;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import br.dogs.com.database.dao.TicketDao;
import br.dogs.com.database.dao.UsuarioDao;
import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.dto.ResponseFatura;
import br.dogs.com.model.dto.TicketFatura;
import br.dogs.com.model.entities.Ticket;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.payment.Safe2PayPaymentService;

@SpringBootTest
@TestPropertySource("/test.properties")
public class TicketServiceTest {
	
	@Autowired
	private TicketService ticketService;
	
	@MockBean
	private TicketDao ticketDao;
	
	@MockBean
	private UsuarioDao usuarioDao;
	
	@MockBean
	private Safe2PayPaymentService safe2PayPaymentService;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(ticketService);
		standaloneSetup(ticketDao);
		standaloneSetup(usuarioDao);
		standaloneSetup(safe2PayPaymentService);
		
	}
	
	@Test
	public void deveRetornarListTicket_QuandoBuscarTodos() throws Exception {
		
		List<Ticket> tickets = new ArrayList<>();
		
		when(this.ticketDao.buscarTodos(1L))
			.thenReturn(tickets);
		
		List<Ticket> expected = ticketService.buscarTodos(1L);
		
		assertEquals(expected, tickets);
		
	}
	
	@Test
	public void deveRetornarObjTicket_QuandoBuscarPorid() throws Exception {
		
		Ticket ticket = new Ticket();
		
		when(this.ticketDao.buscarPorId(1L))
			.thenReturn(ticket);
		
		Ticket expected = ticketService.buscarPorId(1L);
		
		assertEquals(expected, ticket);
		
	}
	
	@Test
	public void deveRetornarObjTicket_QuandoCadastrar() throws Exception {
		
		Ticket ticket = new Ticket();
		
		when(this.ticketDao.cadastrar(Mockito.any(Ticket.class)))
			.thenReturn(ticket);
		
		Ticket expected = ticketService.cadastrar(ticket);
		
		assertEquals(expected, ticket);
		
	}
	
	@Test
	public void deveRetornarBoolean_QuandoDeletar() throws Exception {
		
		when(this.ticketDao.deletarPorId(1L))
			.thenReturn(true);
		
		boolean expected = ticketService.deletarPorId(1L);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveBuscarPorFaturaId() throws Exception {
		
		Ticket ticket = new Ticket();
		
		when(this.ticketDao.buscarPorFaturaId("faturaId"))
			.thenReturn(ticket);
		
		Ticket expected = ticketService.buscarPorFaturaId("faturaId");
		
		assertEquals(expected, ticket);
		
	}
	
	@Test
	public void deveSetarComoPendente() throws Exception {
		
		when(this.ticketDao.setarComoPendente(1L))
			.thenReturn(true);
		
		boolean expected = ticketService.setarComoPendente(1L);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveSetarComoPago() throws Exception {
		
		when(this.ticketDao.setarComoPago(1L))
			.thenReturn(true);
		
		boolean expected = ticketService.setarComoPago(1L);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveSetarComoCancelado() throws Exception {
		
		when(this.ticketDao.setarComoCancelado(1L))
			.thenReturn(true);
		
		boolean expected = ticketService.setarComoCancelado(1L);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveCreditarComprador() throws Exception {
		
		when(this.ticketDao.creditarComprador(1L))
			.thenReturn(true);
		
		boolean expected = ticketService.creditarComprador(1L);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveDebitarComprador() throws Exception {
		
		when(this.ticketDao.debitarComprador(1L))
			.thenReturn(true);
		
		boolean expected = ticketService.debitarComprador(1L);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveFaturar() throws Exception {
		
		Ticket ticket = new Ticket();
		Usuario usuario = new Usuario();
		TicketFatura ticketFatura = new TicketFatura();
		ResponseFatura responseFatura = new ResponseFatura();
		
		ticket.setId(1L);
		ticket.setFaturaId(null);
		ticket.setFaturaUrl(null);
		ticket.setUsuarioId(1L);
		
		ticketFatura.setTicketId(ticket.getId());
		ticketFatura.setCpfPagador("00000000000");
		
		responseFatura.setTemErro(false);
		responseFatura.setId("id");
		responseFatura.setUrl("url");
		
		when(this.ticketDao.buscarPorId(ticket.getId()))
			.thenReturn(ticket);
		
		when(this.usuarioDao.buscarPorId(ticket.getUsuarioId()))
			.thenReturn(usuario);
		
		when(this.safe2PayPaymentService.gerarFatura(Mockito.any(TicketFatura.class), Mockito.any(Ticket.class), Mockito.any(Usuario.class)))
			.thenReturn(responseFatura);
		
		when(this.ticketDao.alterar(Mockito.any(Ticket.class)))
			.thenReturn(true);
		
		when(this.ticketDao.buscarPorId(ticketFatura.getTicketId()))
			.thenReturn(ticket);
		
		ResponseData expected = ticketService.faturar(ticketFatura);
		
		assertEquals(expected.isTemErro(), false);
		
	}
	
	@Test
	public void deveFaturar_TicketFaturado() throws Exception {
		
		Ticket ticket = new Ticket();
		Usuario usuario = new Usuario();
		TicketFatura ticketFatura = new TicketFatura();
		ResponseFatura responseFatura = new ResponseFatura();
		
		ticket.setId(1L);
		ticket.setFaturaId("id");
		ticket.setFaturaUrl("url");
		ticket.setUsuarioId(1L);
		
		ticketFatura.setTicketId(ticket.getId());
		ticketFatura.setCpfPagador("00000000000");
		
		responseFatura.setTemErro(false);
		responseFatura.setId("id");
		responseFatura.setUrl("url");
		
		when(this.ticketDao.buscarPorId(ticket.getId()))
			.thenReturn(ticket);
		
		when(this.usuarioDao.buscarPorId(ticket.getUsuarioId()))
			.thenReturn(usuario);
		
		when(this.safe2PayPaymentService.gerarFatura(Mockito.any(TicketFatura.class), Mockito.any(Ticket.class), Mockito.any(Usuario.class)))
			.thenReturn(responseFatura);
		
		when(this.ticketDao.alterar(Mockito.any(Ticket.class)))
			.thenReturn(true);
		
		when(this.ticketDao.buscarPorId(ticketFatura.getTicketId()))
			.thenReturn(ticket);
		
		ResponseData expected = ticketService.faturar(ticketFatura);
		
		assertEquals(expected.isTemErro(), true);
		
	}
	
	@Test
	public void deveFaturar_ErroAoAtualizarTicket() throws Exception {
		
		Ticket ticket = new Ticket();
		Usuario usuario = new Usuario();
		TicketFatura ticketFatura = new TicketFatura();
		ResponseFatura responseFatura = new ResponseFatura();
		
		ticket.setId(1L);
		ticket.setFaturaId(null);
		ticket.setFaturaUrl(null);
		ticket.setUsuarioId(1L);
		
		ticketFatura.setTicketId(ticket.getId());
		ticketFatura.setCpfPagador("00000000000");
		
		responseFatura.setTemErro(false);
		responseFatura.setId("id");
		responseFatura.setUrl("url");
		
		when(this.ticketDao.buscarPorId(ticket.getId()))
			.thenReturn(ticket);
		
		when(this.usuarioDao.buscarPorId(ticket.getUsuarioId()))
			.thenReturn(usuario);
		
		when(this.safe2PayPaymentService.gerarFatura(Mockito.any(TicketFatura.class), Mockito.any(Ticket.class), Mockito.any(Usuario.class)))
			.thenReturn(responseFatura);
		
		when(this.safe2PayPaymentService.cancelarFatura(Mockito.any(Ticket.class)))
			.thenReturn(true);
		
		when(this.ticketDao.alterar(Mockito.any(Ticket.class)))
			.thenReturn(false);
		
		when(this.ticketDao.buscarPorId(ticketFatura.getTicketId()))
			.thenReturn(ticket);
		
		ResponseData expected = ticketService.faturar(ticketFatura);
		
		assertEquals(expected.isTemErro(), true);
		
	}
	
	@Test
	public void deveFaturar_ErroAoGerarFatura() throws Exception {
		
		Ticket ticket = new Ticket();
		Usuario usuario = new Usuario();
		TicketFatura ticketFatura = new TicketFatura();
		ResponseFatura responseFatura = new ResponseFatura();
		
		ticket.setId(1L);
		ticket.setFaturaId(null);
		ticket.setFaturaUrl(null);
		ticket.setUsuarioId(1L);
		
		ticketFatura.setTicketId(ticket.getId());
		ticketFatura.setCpfPagador("00000000000");
		
		responseFatura.setTemErro(true);
		responseFatura.setId("id");
		responseFatura.setUrl("url");
		
		when(this.ticketDao.buscarPorId(ticket.getId()))
			.thenReturn(ticket);
		
		when(this.usuarioDao.buscarPorId(ticket.getUsuarioId()))
			.thenReturn(usuario);
		
		when(this.safe2PayPaymentService.gerarFatura(Mockito.any(TicketFatura.class), Mockito.any(Ticket.class), Mockito.any(Usuario.class)))
			.thenReturn(responseFatura);
		
		when(this.safe2PayPaymentService.cancelarFatura(Mockito.any(Ticket.class)))
			.thenReturn(true);
		
		when(this.ticketDao.alterar(Mockito.any(Ticket.class)))
			.thenReturn(false);
		
		when(this.ticketDao.buscarPorId(ticketFatura.getTicketId()))
			.thenReturn(ticket);
		
		ResponseData expected = ticketService.faturar(ticketFatura);
		
		assertEquals(expected.isTemErro(), true);
		
	}
	
}