package br.dogs.com.service.payment;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import br.dogs.com.model.dto.ResponseFatura;
import br.dogs.com.model.dto.TicketFatura;
import br.dogs.com.model.dto.safe2pay.ResponseSafe2Pay;
import br.dogs.com.model.entities.Cidade;
import br.dogs.com.model.entities.Estado;
import br.dogs.com.model.entities.FormaDePagamento;
import br.dogs.com.model.entities.Ticket;
import br.dogs.com.model.entities.Usuario;

@SpringBootTest
@TestPropertySource("/test.properties")
public class Safe2PayPaymentServiceTest {
	
	@Mock
    private RestTemplate restTemplate;
	
	@Autowired
	private Safe2PayPaymentService safe2PayPaymentService;
		
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(safe2PayPaymentService);
		
	}
	
	@Test
	public void deveFalharAoGerarFatura() throws Exception {
		
		when(restTemplate.getForEntity(
			"https://api.safe2pay.com.br/v2/SingleSale/Add", ResponseSafe2Pay.class
		)).thenReturn(new ResponseEntity<ResponseSafe2Pay>(new ResponseSafe2Pay(), HttpStatus.OK));
		
		TicketFatura ticketFatura = new TicketFatura();
		Ticket ticket = new Ticket();
		FormaDePagamento formaPagamento = new FormaDePagamento();
		Usuario usuario = new Usuario();
		Cidade cidade = new Cidade();
		Estado estado = new Estado();
		
		formaPagamento.setId(1L);
		formaPagamento.setTipo("6");
		
		ticket.setId(1L);
		ticket.setQuantidade(1);
		ticket.setUnitario(1);
		ticket.setTotal(1);
		ticket.setFormaDePagamento(formaPagamento);
		
		ticketFatura.setTicketId(ticket.getId());
		ticketFatura.setCpfPagador("00000000000");
		
		cidade.setId(1L);
		cidade.setNome("cidade test");
		
		estado.setId(1L);
		estado.setNome("estado test");
		
		usuario.setId(1L);
		usuario.setNome("usuario test");
		usuario.setRua("rua test");
		usuario.setBairro("bairro test");
		usuario.setCep("00000000");
		usuario.setNumero("0");
		usuario.setCidade(cidade);
		usuario.setEstado(estado);
		
		ResponseFatura expected = safe2PayPaymentService.gerarFatura(ticketFatura, ticket, usuario);
		
		assertEquals(expected.isTemErro(), true);
		
	}
	
}
