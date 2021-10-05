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

import br.dogs.com.database.dao.PasseioDao;
import br.dogs.com.helper.PasseioStatus;
import br.dogs.com.model.dto.PasseioLatLong;
import br.dogs.com.model.entities.Passeio;

@SpringBootTest
@TestPropertySource("/test.properties")
public class PasseioServiceTest {

	@Autowired
	private PasseioService passeioService;
	
	@MockBean
	private PasseioDao passeioDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(passeioService);
		standaloneSetup(passeioDao);
		
	}
	
	@Test
	public void deveSolicitar() throws Exception {
		
		Passeio passeio = new Passeio();
		
		when(this.passeioDao.solicitar(Mockito.any(Passeio.class)))
			.thenReturn(passeio);
		
		Passeio expected = passeioService.solicitar(passeio);
		
		assertEquals(expected, passeio);
		
	}
	
	@Test
	public void deveBuscarPorId() throws Exception {
		
		Passeio passeio = new Passeio();
		
		when(this.passeioDao.buscarPorId(1L))
			.thenReturn(passeio);
		
		Passeio expected = passeioService.buscarPorId(1L);
		
		assertEquals(expected, passeio);
		
	}
	
	@Test
	public void deveAlterarStatus() throws Exception {
		
		when(this.passeioDao.alterarStatus(1L, PasseioStatus.Andamento.name()))
			.thenReturn(true);
		
		boolean expected = passeioService.alterarStatus(1L, PasseioStatus.Andamento.name());
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveBuscarTodos() throws Exception {
		
		List<Passeio> passeios = new ArrayList<>();
		
		when(this.passeioDao.buscarTodos(1L))
			.thenReturn(passeios);
		
		List<Passeio> expected = passeioService.buscarTodos(1L);
		
		assertEquals(expected, passeios);
		
	}
	
	@Test
	public void deveRegistrarLatLong() throws Exception {
		
		PasseioLatLong latLong = new PasseioLatLong();
		
		when(this.passeioDao.registrarLatLong(Mockito.any(PasseioLatLong.class)))
			.thenReturn(true);
		
		boolean expected = passeioService.registrarLatLong(latLong);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveRetornarPosicaoAtual() throws Exception {
		
		PasseioLatLong latLong = new PasseioLatLong();
		
		when(this.passeioDao.posicaoAtual(1L))
			.thenReturn(latLong);
		
		PasseioLatLong expected = passeioService.posicaoAtual(1L);
		
		assertEquals(expected, latLong);
		
	}
	
	@Test
	public void deveRetornarPosicaoPosicaoCompleta() throws Exception {
		
		List<PasseioLatLong> list = new ArrayList<>();
		
		when(this.passeioDao.posicaoCompleta(1L))
			.thenReturn(list);
		
		List<PasseioLatLong> expected = passeioService.posicaoCompleta(1L);
		
		assertEquals(expected, list);
		
	}
	
}
