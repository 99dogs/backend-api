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

import br.dogs.com.database.dao.PorteDao;
import br.dogs.com.model.entities.Porte;

@SpringBootTest
@TestPropertySource("/test.properties")
public class PorteServiceTest {
	
	@Autowired
	private PorteService porteService;
	
	@MockBean
	private PorteDao porteDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(porteService);
		standaloneSetup(porteDao);
		
	}
	
	@Test
	public void deveRetornarListPorte_QuandoBuscarTodos() throws Exception {
		
		List<Porte> portes = new ArrayList<>();
		
		when(this.porteDao.buscarTodos())
			.thenReturn(portes);
		
		List<Porte> expected = porteService.buscarTodos();
		
		assertEquals(expected, portes);
		
	}
	
	@Test
	public void deveRetornarObjPorte_QuandoBuscarPorId() throws Exception {
		
		Porte porte = new Porte();
		porte.setId(1L);
		
		when(this.porteDao.buscarPorId(porte.getId()))
			.thenReturn(porte);
		
		Porte expected = porteService.buscarPorId(porte.getId());
		
		assertEquals(expected, porte);
		
	}
	
	@Test
	public void deveRetornarObjPorte_QuandoCadastrar() throws Exception {
		
		Porte porte = new Porte();
		porte.setId(1L);
		
		when(this.porteDao.cadastrar(Mockito.any(Porte.class)))
			.thenReturn(porte);
		
		Porte expected = porteService.cadastrar(porte);
		
		assertEquals(expected, porte);
		
	}
	
	@Test
	public void deveRetornarBoolean_QuandoAlterar() throws Exception {
		
		Porte porte = new Porte();
		porte.setId(1L);
		
		when(this.porteDao.alterar(Mockito.any(Porte.class)))
			.thenReturn(true);
		
		boolean expected = porteService.alterar(porte);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveRetornarBoolean_QuandoDeletar() throws Exception {
		
		Porte porte = new Porte();
		porte.setId(1L);
		
		when(this.porteDao.deletarPorId(porte.getId()))
			.thenReturn(true);
		
		boolean expected = porteService.deletarPorId(porte.getId());
		
		assertEquals(expected, true);
		
	}
	
}
