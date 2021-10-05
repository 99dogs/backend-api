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

import br.dogs.com.database.dao.SaldoDao;
import br.dogs.com.helper.PasseioStatus;
import br.dogs.com.model.entities.ConfiguracaoBase;
import br.dogs.com.model.entities.Passeio;
import br.dogs.com.model.entities.Saldo;

@SpringBootTest
@TestPropertySource("/test.properties")
public class SaldoServiceTest {
	
	@Autowired
	private SaldoService saldoService;
	
	@MockBean
	private ConfiguracaoBaseService configuracaoBaseService;
	
	@MockBean
	private SaldoDao saldoDao;
	
	@MockBean
	private PasseioService passeioService;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(saldoService);
		standaloneSetup(configuracaoBaseService);
		standaloneSetup(saldoDao);
		standaloneSetup(passeioService);
		
	}
	
	@Test
	public void deveRetornarFalse_QuandoCreditarSaldo_PasseioIdZerado() throws Exception {
		
		Passeio passeio = new Passeio();
		passeio.setId(0L);
		
		when(this.passeioService.buscarPorId(1L))
			.thenReturn(passeio);
		
		boolean expected = saldoService.creditarSaldo(1L);
		
		assertEquals(expected, false);
		
	}
	
	@Test
	public void deveRetornarFalse_QuandoCreditarSaldo_PasseioIdNulo() throws Exception {
		
		Passeio passeio = new Passeio();
		passeio.setId(null);
		
		when(this.passeioService.buscarPorId(1L))
			.thenReturn(passeio);
		
		boolean expected = saldoService.creditarSaldo(1L);
		
		assertEquals(expected, false);
		
	}
	
	@Test
	public void deveRetornarFalse_QuandoCreditarSaldo_StatusDivergente() throws Exception {
		
		Passeio passeio = new Passeio();
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Andamento.name());
		
		when(this.passeioService.buscarPorId(1L))
			.thenReturn(passeio);
		
		boolean expected = saldoService.creditarSaldo(1L);
		
		assertEquals(expected, false);
		
	}
	
	@Test
	public void deveRetornarFalse_QuandoCreditarSaldo_ConfiguracaoBaseNaoExistente() throws Exception {
		
		Passeio passeio = new Passeio();
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Finalizado.name());
		
		when(this.passeioService.buscarPorId(1L))
			.thenReturn(passeio);
		
		when(this.configuracaoBaseService.buscarTodos())
			.thenReturn(new ArrayList<>());
		
		boolean expected = saldoService.creditarSaldo(1L);
		
		assertEquals(expected, false);
		
	}
	
	@Test
	public void deveRetornarTrue_QuandoCreditarSaldo() throws Exception {
		
		Passeio passeio = new Passeio();
		passeio.setId(1L);
		passeio.setStatus(PasseioStatus.Finalizado.name());
		passeio.setDogwalkerId(2L);
		
		List<ConfiguracaoBase> configs = new ArrayList<>();
		ConfiguracaoBase config = new ConfiguracaoBase();
		
		config.setTaxaPlataforma(1);
		config.setValorTicket(1);
		
		configs.add(config);
		
		when(this.passeioService.buscarPorId(1L))
			.thenReturn(passeio);
		
		when(this.configuracaoBaseService.buscarTodos())
			.thenReturn(configs);
		
		when(this.saldoDao.creditarSaldo(Mockito.any(Saldo.class)))
			.thenReturn(true);
		
		boolean expected = saldoService.creditarSaldo(1L);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveRetornarUltimoSaldo() throws Exception {
		
		double expected = saldoService.retornaUltimoSaldo(1L);
		
		assertEquals(expected, 0);
		
	}
	
	@Test
	public void deveBuscarTodos() throws Exception {
		
		List<Saldo> saldos = new ArrayList<>();
		
		when(this.saldoDao.buscarTodos(1L))
			.thenReturn(saldos);
		
		List<Saldo> expected = saldoService.buscarTodos(1L);
		
		assertEquals(expected, saldos);
		
	}
	
	@Test
	public void deveBuscarPorDeposito() throws Exception {
		
		List<Saldo> saldos = new ArrayList<>();
		
		when(this.saldoDao.buscarPorDeposito(1L))
			.thenReturn(saldos);
		
		List<Saldo> expected = saldoService.buscarPorDeposito(1L);
		
		assertEquals(expected, saldos);
		
	}
	
}
