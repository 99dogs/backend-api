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

import br.dogs.com.database.dao.RacaDao;
import br.dogs.com.model.entities.Raca;

@SpringBootTest
@TestPropertySource("/test.properties")
public class RacaServiceTest {
	
	@Autowired
	private RacaService racaService;
	
	@MockBean
	private RacaDao racaDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(racaService);
		standaloneSetup(racaDao);
		
	}
	
	@Test
	public void deveRetornarListRaca_QuandoBuscarTodos() throws Exception {
		
		List<Raca> racas = new ArrayList<>();
		
		when(this.racaDao.findAll())
			.thenReturn(racas);
		
		List<Raca> expected = racaService.findAll();
		
		assertEquals(expected, racas);
		
	}
	
	@Test
	public void deveRetornarObjRaca_QuandoBuscarPorId() throws Exception {
		
		Raca raca = new Raca();
		raca.setId(1L);
		
		when(this.racaDao.findById(raca.getId()))
			.thenReturn(raca);
		
		Raca expected = racaService.findById(raca.getId());
		
		assertEquals(expected, raca);
		
	}
	
	@Test
	public void deveRetornarObjRaca_QuandoCadastrar() throws Exception {
		
		Raca raca = new Raca();
		raca.setId(1L);
		
		when(this.racaDao.create(Mockito.any(Raca.class)))
			.thenReturn(raca);
		
		Raca expected = racaService.create(raca);
		
		assertEquals(expected, raca);
		
	}
	
	@Test
	public void deveRetornarBoolean_QuandoAlterar() throws Exception {
		
		Raca raca = new Raca();
		raca.setId(1L);
		
		when(this.racaDao.update(Mockito.any(Raca.class)))
			.thenReturn(true);
		
		boolean expected = racaService.update(raca);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveRetornarBoolean_QuandoDeletar() throws Exception {
		
		Raca raca = new Raca();
		raca.setId(1L);
		
		when(this.racaDao.deleteById(raca.getId()))
			.thenReturn(true);
		
		boolean expected = racaService.deleteById(raca.getId());
		
		assertEquals(expected, true);
		
	}
	
}