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

import br.dogs.com.database.dao.CachorroDao;
import br.dogs.com.model.entities.Cachorro;

@SpringBootTest
@TestPropertySource("/test.properties")
public class CachorroServiceTest {

	@Autowired
	private CachorroService cachorroService;
	
	@MockBean
	private CachorroDao cachorroDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(cachorroService);
		standaloneSetup(cachorroDao);
		
	}
	
	@Test
	public void deveCadastrar() throws Exception {
		
		Cachorro item = new Cachorro();
		
		when(this.cachorroDao.cadastrar(Mockito.any(Cachorro.class)))
			.thenReturn(item);
		
		Cachorro expected = cachorroService.cadastrar(item);
		
		assertEquals(expected, item);
		
	}
	
	@Test
	public void deveBuscarPorId() throws Exception {
		
		Cachorro item = new Cachorro();
		
		when(this.cachorroDao.buscarPorId(1L))
			.thenReturn(item);
		
		Cachorro expected = cachorroService.buscarPorId(1L);
		
		assertEquals(expected, item);
		
	}
	
	@Test
	public void deveBuscarTodos() throws Exception {
		
		List<Cachorro> items = new ArrayList<>();
		
		when(this.cachorroDao.buscarTodos(1L))
			.thenReturn(items);
		
		List<Cachorro> expected = cachorroService.buscarTodos(1L);
		
		assertEquals(expected, items);
		
	}
	
	@Test
	public void deveAlterar() throws Exception {
		
		Cachorro item = new Cachorro();
		
		when(this.cachorroDao.alterar(Mockito.any(Cachorro.class)))
			.thenReturn(true);
		
		boolean expected = cachorroService.alterar(item);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveDeletar() throws Exception {
				
		when(this.cachorroDao.deletarPorId(1L))
			.thenReturn(true);
		
		boolean expected = cachorroService.deletarPorId(1L);
		
		assertEquals(expected, true);
		
	}
	
}
