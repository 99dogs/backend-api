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

import br.dogs.com.database.dao.AvaliacaoDao;
import br.dogs.com.model.entities.Avaliacao;

@SpringBootTest
@TestPropertySource("/test.properties")
public class AvaliacaoServiceTest {
	
	@Autowired
	private AvaliacaoService avaliacaoService;
	
	@MockBean
	private AvaliacaoDao avaliacaoDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(avaliacaoService);
		standaloneSetup(avaliacaoDao);
		
	}
	
	@Test
	public void deveRetornarObjAvaliacao_QuandoBuscarPorId() throws Exception {
		
		Avaliacao avaliacao = new Avaliacao();
		
		avaliacao.setId(1L);
		
		when(this.avaliacaoDao.buscarPorId(avaliacao.getId()))
			.thenReturn(avaliacao);
		
		Avaliacao expected = avaliacaoService.buscarPorId(avaliacao.getId());
		
		assertEquals(expected, avaliacao);
		
	}
	
	@Test
	public void deveRetornarObjAvaliacao_QuandoCadastrar() throws Exception {
		
		Avaliacao avaliacao = new Avaliacao();
		
		avaliacao.setId(1L);
		
		when(this.avaliacaoDao.cadastrar(Mockito.any(Avaliacao.class)))
			.thenReturn(avaliacao);
		
		Avaliacao expected = avaliacaoService.cadastrar(avaliacao);
		
		assertEquals(expected, avaliacao);
		
	}
	
	@Test
	public void deveRetornarListAvaliacao_QuandoBuscarTodosPorDogwalker() throws Exception {
		
		List<Avaliacao> avaliacoes = new ArrayList<>();
		
		when(this.avaliacaoDao.buscarTodosPorDogwalker(1L))
			.thenReturn(avaliacoes);
		
		List<Avaliacao> expected = avaliacaoService.buscarTodosPorDogwalker(1L);
		
		assertEquals(expected, avaliacoes);
		
	}
	
	@Test
	public void deveRetornarBoolean_QuandoDeletar() throws Exception {
		
		when(this.avaliacaoDao.deletar(1L))
			.thenReturn(true);
		
		boolean expected = avaliacaoService.deletar(1L);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveRetornarBoolean_QuandoAtualizarMediaAvaliacao() throws Exception {
		
		when(this.avaliacaoDao.atualizarMediaAvaliacao(1L))
			.thenReturn(true);
		
		boolean expected = avaliacaoService.atualizarMediaAvaliacao(1L);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveRetornarObjAvaliacao_QuandoBuscarPorPasseioId() throws Exception {
		
		Avaliacao avaliacao = new Avaliacao();
		
		avaliacao.setId(1L);
		avaliacao.setPasseioId(2L);
		
		when(this.avaliacaoDao.buscarPorPasseioId(avaliacao.getPasseioId()))
			.thenReturn(avaliacao);
		
		Avaliacao expected = avaliacaoService.buscarPorPasseioId(avaliacao.getPasseioId());
		
		assertEquals(expected, avaliacao);
		
	}
	
}
