package br.dogs.com.service;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import br.dogs.com.database.dao.DepositoDao;
import br.dogs.com.model.entities.Deposito;

@SpringBootTest
@TestPropertySource("/test.properties")
public class DepositoServiceTest {
	
	@Autowired
	private DepositoService depositoService;
	
	@MockBean
	private DepositoDao depositoDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(depositoService);
		standaloneSetup(depositoDao);
		
	}
	
	@Test
	public void deveRetornarObjDeposito_QuandoSolicitar() throws Exception {
		
		Deposito deposito = new Deposito();
		deposito.setId(1L);
		deposito.setUsuarioId(2L);
		
		when(this.depositoDao.solicitar(deposito.getUsuarioId()))
			.thenReturn(deposito);
		
		Deposito expected = depositoService.solicitar(deposito.getUsuarioId());
		
		assertEquals(expected, deposito);
		
	}
	
	@Test
	public void deveRetornarObjDeposito_QuandoBuscarPorId() throws Exception {
		
		Deposito deposito = new Deposito();
		deposito.setId(1L);
		deposito.setUsuarioId(2L);
		
		when(this.depositoDao.buscarPorId(deposito.getId()))
			.thenReturn(deposito);
		
		Deposito expected = depositoService.buscarPorId(deposito.getId());
		
		assertEquals(expected, deposito);
		
	}
	
	@Test
	public void deveRetornarListDeposito_QuandoBuscarTodos() throws Exception {
		
		List<Deposito> depositos = new ArrayList<>();
		
		Deposito deposito = new Deposito();
		deposito.setId(1L);
		deposito.setUsuarioId(2L);
		
		depositos.add(deposito);
		
		when(this.depositoDao.buscarTodos(deposito.getUsuarioId()))
			.thenReturn(depositos);
		
		List<Deposito> expected = depositoService.buscarTodos(deposito.getUsuarioId());
		
		assertEquals(expected, depositos);
		
	}
	
}
