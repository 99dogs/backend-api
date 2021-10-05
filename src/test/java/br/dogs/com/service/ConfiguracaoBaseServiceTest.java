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

import br.dogs.com.database.dao.ConfiguracaoBaseDao;
import br.dogs.com.model.entities.ConfiguracaoBase;

@SpringBootTest
@TestPropertySource("/test.properties")
public class ConfiguracaoBaseServiceTest {
	
	@Autowired
	private ConfiguracaoBaseService configuracaoBaseService;
	
	@MockBean
	private ConfiguracaoBaseDao configuracaoBaseDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(configuracaoBaseService);
		standaloneSetup(configuracaoBaseDao);
		
	}
	
	@Test
	public void deveBuscarTodos() throws Exception {
		
		List<ConfiguracaoBase> configs = new ArrayList<>();
		
		when(this.configuracaoBaseDao.buscarTodos())
			.thenReturn(configs);
		
		List<ConfiguracaoBase> expected = configuracaoBaseService.buscarTodos();
		
		assertEquals(expected, configs);
		
	}
	
	@Test
	public void deveCadastrar() throws Exception {
		
		ConfiguracaoBase config = new ConfiguracaoBase();
		
		when(this.configuracaoBaseDao.cadastrar(Mockito.any(ConfiguracaoBase.class)))
			.thenReturn(config);
		
		ConfiguracaoBase expected = configuracaoBaseService.cadastrar(config);
		
		assertEquals(expected, config);
		
	}
	
	@Test
	public void deveBuscarPorId() throws Exception {
		
		ConfiguracaoBase config = new ConfiguracaoBase();
		
		when(this.configuracaoBaseDao.buscarPorId(1L))
			.thenReturn(config);
		
		ConfiguracaoBase expected = configuracaoBaseService.buscarPorId(1L);
		
		assertEquals(expected, config);
		
	}
	
	@Test
	public void deveAlterar() throws Exception {
		
		ConfiguracaoBase config = new ConfiguracaoBase();
		
		when(this.configuracaoBaseDao.alterar(Mockito.any(ConfiguracaoBase.class)))
			.thenReturn(true);
		
		boolean expected = configuracaoBaseService.alterar(config);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveDeletar() throws Exception {
				
		when(this.configuracaoBaseDao.deletarPorId(1L))
			.thenReturn(true);
		
		boolean expected = configuracaoBaseService.deletarPorId(1L);
		
		assertEquals(expected, true);
		
	}
	
}
