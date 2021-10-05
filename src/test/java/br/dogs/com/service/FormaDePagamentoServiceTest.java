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

import br.dogs.com.database.dao.FormaDePagamentoDao;
import br.dogs.com.model.entities.FormaDePagamento;

@SpringBootTest
@TestPropertySource("/test.properties")
public class FormaDePagamentoServiceTest {
	
	@Autowired
	private FormaDePagamentoService formaDePagamentoService;
	
	@MockBean
	private FormaDePagamentoDao formaDePagamentoDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(formaDePagamentoService);
		standaloneSetup(formaDePagamentoDao);
		
	}
	
	@Test
	public void deveBuscarTodos() throws Exception {
		
		List<FormaDePagamento> list = new ArrayList<>();
		
		when(this.formaDePagamentoService.buscarTodos())
			.thenReturn(list);
		
		List<FormaDePagamento> expected = formaDePagamentoService.buscarTodos();
		
		assertEquals(expected, list);
		
	}
	
	@Test
	public void deveBuscarPorId() throws Exception {
		
		FormaDePagamento item = new FormaDePagamento();
		
		when(this.formaDePagamentoService.buscarPorId(1L))
			.thenReturn(item);
		
		FormaDePagamento expected = formaDePagamentoService.buscarPorId(1L);
		
		assertEquals(expected, item);
		
	}
	
	@Test
	public void deveCadastrar() throws Exception {
		
		FormaDePagamento item = new FormaDePagamento();
		
		when(this.formaDePagamentoService.cadastrar(Mockito.any(FormaDePagamento.class)))
			.thenReturn(item);
		
		FormaDePagamento expected = formaDePagamentoService.cadastrar(item);
		
		assertEquals(expected, item);
		
	}
	
	@Test
	public void deveAlterar() throws Exception {
		
		FormaDePagamento item = new FormaDePagamento();
		
		when(this.formaDePagamentoService.alterar(Mockito.any(FormaDePagamento.class)))
			.thenReturn(true);
		
		boolean expected = formaDePagamentoService.alterar(item);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveDeletarPorId() throws Exception {
				
		when(this.formaDePagamentoService.deletarPorId(1L))
			.thenReturn(true);
		
		boolean expected = formaDePagamentoService.deletarPorId(1L);
		
		assertEquals(expected, true);
		
	}
	
}
