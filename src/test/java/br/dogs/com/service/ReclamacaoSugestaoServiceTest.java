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

import br.dogs.com.database.dao.ReclamacaoSugestaoDao;
import br.dogs.com.model.entities.ReclamacaoSugestao;

@SpringBootTest
@TestPropertySource("/test.properties")
public class ReclamacaoSugestaoServiceTest {
	
	@Autowired
	private ReclamacaoSugestaoService reclamacaoSugestaoService;
	
	@MockBean
	private ReclamacaoSugestaoDao reclamacaoSugestaoDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(reclamacaoSugestaoService);
		standaloneSetup(reclamacaoSugestaoDao);
		
	}
	
	@Test
	public void deveRetornarListReclamacaoSugestao_QuandoBuscarTodos() throws Exception {
		
		List<ReclamacaoSugestao> items = new ArrayList<>();
		
		when(this.reclamacaoSugestaoDao.bsucarTodos(1L))
			.thenReturn(items);
		
		List<ReclamacaoSugestao> expected = reclamacaoSugestaoService.buscarTodos(1L);
		
		assertEquals(expected, items);
		
	}
	
	@Test
	public void deveRetornarObjReclamacaoSugestao_QuandoCadastrar() throws Exception {
		
		ReclamacaoSugestao item = new ReclamacaoSugestao();
		item.setId(1L);
		
		when(this.reclamacaoSugestaoDao.cadastrar(Mockito.any(ReclamacaoSugestao.class)))
			.thenReturn(item);
		
		ReclamacaoSugestao expected = reclamacaoSugestaoService.cadastrar(item);
		
		assertEquals(expected, item);
		
	}
	
	@Test
	public void deveRetornarObjReclamacaoSugestao_QuandoBuscarPorId() throws Exception {
		
		ReclamacaoSugestao item = new ReclamacaoSugestao();
		item.setId(1L);
		
		when(this.reclamacaoSugestaoDao.buscarPorId(item.getId()))
			.thenReturn(item);
		
		ReclamacaoSugestao expected = reclamacaoSugestaoService.buscarPorId(item.getId());
		
		assertEquals(expected, item);
		
	}
	
	@Test
	public void deveRetornarBoolean_QuandDeletar() throws Exception {
				
		when(this.reclamacaoSugestaoDao.deletarPorId(1L))
			.thenReturn(true);
		
		boolean expected = reclamacaoSugestaoService.deletarPorId(1L);
		
		assertEquals(expected, true);
		
	}
	
}
