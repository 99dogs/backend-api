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

import br.dogs.com.database.dao.ConfiguracaoHorarioDao;
import br.dogs.com.model.entities.ConfiguracaoHorario;

@SpringBootTest
@TestPropertySource("/test.properties")
public class ConfiguracaoHorarioServiceTest {

	@Autowired
	private ConfiguracaoHorarioService configuracaoHorarioService;
	
	@MockBean
	private ConfiguracaoHorarioDao configuracaoHorarioDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(configuracaoHorarioService);
		standaloneSetup(configuracaoHorarioDao);
		
	}
	
	@Test
	public void deveBuscarTodos() throws Exception {
		
		List<ConfiguracaoHorario> items = new ArrayList<>();
		
		when(this.configuracaoHorarioDao.buscarTodos(1L))
			.thenReturn(items);
		
		List<ConfiguracaoHorario> expected = configuracaoHorarioService.buscarTodos(1L);
		
		assertEquals(expected, items);
		
	}
	
	@Test
	public void deveBuscarPorId() throws Exception {
		
		ConfiguracaoHorario item = new ConfiguracaoHorario();
		
		when(this.configuracaoHorarioDao.buscarPorId(1L))
			.thenReturn(item);
		
		ConfiguracaoHorario expected = configuracaoHorarioService.buscarPorId(1L);
		
		assertEquals(expected, item);
		
	}
	
	@Test
	public void deveCadastrar() throws Exception {
		
		ConfiguracaoHorario item = new ConfiguracaoHorario();
		
		when(this.configuracaoHorarioDao.cadastrar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(item);
		
		ConfiguracaoHorario expected = configuracaoHorarioService.cadastrar(item);
		
		assertEquals(expected, item);
		
	}
	
	@Test
	public void deveBuscarPorDiaSemana() throws Exception {
		
		ConfiguracaoHorario item = new ConfiguracaoHorario();
		
		when(this.configuracaoHorarioDao.buscarPorDiaSemana(1, 1L))
			.thenReturn(item);
		
		ConfiguracaoHorario expected = configuracaoHorarioService.buscarPorDiaSemana(1, 1L);
		
		assertEquals(expected, item);
		
	}
	
	@Test
	public void deveAlterar() throws Exception {
		
		ConfiguracaoHorario item = new ConfiguracaoHorario();
		
		when(this.configuracaoHorarioDao.alterar(Mockito.any(ConfiguracaoHorario.class)))
			.thenReturn(true);
		
		boolean expected = configuracaoHorarioService.alterar(item);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveDeletar() throws Exception {
				
		when(this.configuracaoHorarioDao.deletarPorId(1L))
			.thenReturn(true);
		
		boolean expected = configuracaoHorarioService.deletarPorId(1L);
		
		assertEquals(expected, true);
		
	}
		
}
