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

import br.dogs.com.database.dao.EstadoDao;
import br.dogs.com.model.entities.Estado;

@SpringBootTest
@TestPropertySource("/test.properties")
public class EstadoServiceTest {
	
	@Autowired
	private EstadoService estadoService;
	
	@MockBean
	private EstadoDao estadoDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(estadoService);
		standaloneSetup(estadoDao);
		
	}
	
	@Test
	public void deveRetornarListEstado_QuandoBuscarTodos() throws Exception {
		
		List<Estado> estados = new ArrayList<>();
		
		when(this.estadoDao.buscarTodos())
			.thenReturn(estados);
		
		List<Estado> expected = estadoService.buscarTodos();
		
		assertEquals(expected, estados);
		
	}
	
	@Test
	public void deveRetornarObjEstado_QuandoBuscarPorId() throws Exception {
		
		Estado estado = new Estado();
		
		estado.setId(1L);
		
		when(this.estadoDao.buscarPorId(estado.getId()))
			.thenReturn(estado);
		
		Estado expected = estadoService.buscarPorId(estado.getId());
		
		assertEquals(expected, estado);
		
	}
	
}
