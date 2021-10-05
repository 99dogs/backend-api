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

import br.dogs.com.database.dao.QualificacaoDao;
import br.dogs.com.model.entities.Qualificacao;

@SpringBootTest
@TestPropertySource("/test.properties")
public class QualificacaoServiceTest {
	
	@Autowired
	private QualificacaoService qualificacaoService;
	
	@MockBean
	private QualificacaoDao qualificacaoDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(qualificacaoService);
		standaloneSetup(qualificacaoDao);
		
	}
	
	@Test
	public void deveCadastrar() throws Exception {
		
		Qualificacao item = new Qualificacao();
		
		when(this.qualificacaoDao.cadastrar(Mockito.any(Qualificacao.class)))
			.thenReturn(item);
		
		Qualificacao expected = qualificacaoService.cadastrar(item);
		
		assertEquals(expected, item);
		
	}
	
	@Test
	public void deveBuscarTodos() throws Exception {
		
		List<Qualificacao> items = new ArrayList<>();
		
		when(this.qualificacaoDao.buscarTodos(1L))
			.thenReturn(items);
		
		List<Qualificacao> expected = qualificacaoService.buscarTodos(1L);
		
		assertEquals(expected, items);
		
	}
	
	@Test
	public void deveBuscarPorId() throws Exception {
		
		Qualificacao item = new Qualificacao();
		
		when(this.qualificacaoDao.buscarPorId(1L))
			.thenReturn(item);
		
		Qualificacao expected = qualificacaoService.buscarPorId(1L);
		
		assertEquals(expected, item);
		
	}
	
	@Test
	public void deveAlterar() throws Exception {
		
		Qualificacao item = new Qualificacao();
		
		when(this.qualificacaoDao.alterar(Mockito.any(Qualificacao.class)))
			.thenReturn(true);
		
		boolean expected = qualificacaoService.alterar(item);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveDeletar() throws Exception {
				
		when(this.qualificacaoDao.deletarPorId(1L))
			.thenReturn(true);
		
		boolean expected = qualificacaoService.deletarPorId(1L);
		
		assertEquals(expected, true);
		
	}
	
}
