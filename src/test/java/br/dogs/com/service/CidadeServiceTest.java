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

import br.dogs.com.database.dao.CidadeDao;
import br.dogs.com.model.entities.Cidade;

@SpringBootTest
@TestPropertySource("/test.properties")
public class CidadeServiceTest {
	
	@Autowired
	private CidadeService cidadeService;
	
	@MockBean
	private CidadeDao cidadeDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(cidadeService);
		standaloneSetup(cidadeDao);
		
	}
	
	@Test
	public void deveRetornarListCidade_QuandoBuscarPorEstado() throws Exception {
		
		List<Cidade> cidades = new ArrayList<>();
		
		when(this.cidadeDao.buscarPorEstado(1L))
			.thenReturn(cidades);
		
		List<Cidade> expected = cidadeService.buscarPorEstado(1L);
		
		assertEquals(expected, cidades);
		
	}
	
	@Test
	public void deveRetornarObjCidade_QuandoBuscarPorId() throws Exception {
		
		Cidade cidade = new Cidade();
		
		cidade.setId(1L);
		
		when(this.cidadeDao.buscarPorId(cidade.getId()))
			.thenReturn(cidade);
		
		Cidade expected = cidadeService.buscarPorId(cidade.getId());
		
		assertEquals(expected, cidade);
		
	}
	
}
