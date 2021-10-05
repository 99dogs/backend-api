package br.dogs.com.service;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import br.dogs.com.database.dao.HorarioDao;

@SpringBootTest
@TestPropertySource("/test.properties")
public class HorarioServiceTest {
	
	@Autowired
	private HorarioService horarioService;
	
	@MockBean
	private HorarioDao horarioDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(horarioService);
		standaloneSetup(horarioDao);
		
	}
	
	@Test
	public void deveRetornarBoolean_QuandoVerificarDisponibilidade() throws Exception {
		
		when(this.horarioDao.verificarDisponibilidade("0000-00-00T00:00:00", 1L))
			.thenReturn(true);
		
		boolean expected = horarioService.verificarDisponibilidade("0000-00-00T00:00:00", 1L);
		
		assertEquals(expected, true);
		
	}
	
}
