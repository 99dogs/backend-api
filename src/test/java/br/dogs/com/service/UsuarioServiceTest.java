package br.dogs.com.service;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;

import br.dogs.com.database.dao.UsuarioDao;
import br.dogs.com.model.dto.UsuarioAlterarDados;
import br.dogs.com.model.dto.UsuarioAutenticado;
import br.dogs.com.model.dto.UsuarioRegistro;
import br.dogs.com.model.dto.UsuarioSocialLogin;
import br.dogs.com.model.entities.Usuario;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UsuarioServiceTest {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@MockBean
	private UsuarioDao usuarioDao;
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(usuarioService);
		standaloneSetup(usuarioDao);
		
	}
	
	@Test
	public void deveAlterar() throws Exception {
		
		boolean expected = usuarioService.alterar(1L);
		
		assertEquals(expected, false);
		
	}
	
	@Test
	public void deveDeletarPorId() throws Exception {
		
		boolean expected = usuarioService.deletarPorId(1L);
		
		assertEquals(expected, false);
		
	}
	
	@Test
	public void deveBuscarPorId() throws Exception {
		
		Usuario usuario = new Usuario();
		
		when(this.usuarioDao.buscarPorId(1L))
			.thenReturn(usuario);
		
		Usuario expected = usuarioService.buscarPorId(1L);
		
		assertEquals(expected, usuario);
		
	}
	
	@Test
	public void deveBuscarPorEmail() throws Exception {
		
		Usuario usuario = new Usuario();
		
		when(this.usuarioDao.buscarPorEmail("email@email.com"))
			.thenReturn(usuario);
		
		Usuario expected = usuarioService.buscarPorEmail("email@email.com");
		
		assertEquals(expected, usuario);
		
	}
	
	@Test
	public void deveRegistrar_UsuarioJaCriado() throws Exception {
		
		Usuario usuario = new Usuario();
		UsuarioRegistro usuarioRegistro = new UsuarioRegistro();
		UsuarioAutenticado usuarioCriado = new UsuarioAutenticado();
		
		usuario.setId(1L);
		usuario.setEmail("email@email.com");
		
		usuarioRegistro.setEmail("email@email.com");
		
		usuarioCriado.setId(usuario.getId());
		
		when(this.usuarioDao.buscarPorEmail("email@email.com"))
			.thenReturn(usuario);
		
		UsuarioAutenticado expected = usuarioService.registrar(usuarioRegistro);
		
		assertEquals(expected.getId(), usuarioCriado.getId());
		
	}
	
	@Test
	public void deveRegistrar_NovoUsuario() throws Exception {
		
		UsuarioRegistro usuarioRegistro = new UsuarioRegistro();
		UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
		
		usuarioRegistro.setEmail("email@email.com");
		
		when(this.usuarioDao.buscarPorEmail(usuarioRegistro.getEmail()))
			.thenReturn(null);
		
		when(this.usuarioDao.registrar(Mockito.any(UsuarioRegistro.class)))
			.thenReturn(usuarioAutenticado);
		
		UsuarioAutenticado expected = usuarioService.registrar(usuarioRegistro);
		
		assertEquals(expected, usuarioAutenticado);
		
	}
	
	@Test
	public void deveLoadUserByUsername() throws Exception {
		
		Usuario usuario = new Usuario();
		
		usuario.setId(1L);
		usuario.setEmail("email@email.com");
		
		when(this.usuarioService.buscarPorEmail(usuario.getEmail()))
			.thenReturn(usuario);
		
		Usuario expected = (Usuario) usuarioService.loadUserByUsername(usuario.getEmail());
		
		assertEquals(expected, usuario);
		
	}
	
	@Test
	public void deveLoadUserByUsername_ThrowUsernameNotFoundException() throws Exception {
		
		Usuario usuario = new Usuario();
		
		usuario.setId(1L);
		usuario.setEmail("email@email.com");
		
		when(this.usuarioService.buscarPorEmail(usuario.getEmail()))
			.thenReturn(null);
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			usuarioService.loadUserByUsername(usuario.getEmail());
		});
		
	}
	
	@Test
	public void deveBuscarDogwalkers() throws Exception {
		
		List<Usuario> usuarios = new ArrayList<>();
		
		when(this.usuarioDao.buscarDogwalkers())
			.thenReturn(usuarios);
		
		List<Usuario> expected = usuarioService.buscarDogwalkers();
		
		assertEquals(expected, usuarios);
		
	}
	
	@Test
	public void deveBuscarTutores() throws Exception {
		
		List<Usuario> usuarios = new ArrayList<>();
		
		when(this.usuarioDao.buscarTutores())
			.thenReturn(usuarios);
		
		List<Usuario> expected = usuarioService.buscarTutores();
		
		assertEquals(expected, usuarios);
		
	}
	
	@Test
	public void deveBuscarTodos() throws Exception {
		
		List<Usuario> usuarios = new ArrayList<>();
		
		when(this.usuarioDao.buscarTodos())
			.thenReturn(usuarios);
		
		List<Usuario> expected = usuarioService.buscarTodos();
		
		assertEquals(expected, usuarios);
		
	}
	
	@Test
	public void deveAlterarDados() throws Exception {
		
		UsuarioAlterarDados usuario = new UsuarioAlterarDados();
		
		when(this.usuarioDao.alterarDados(Mockito.any(UsuarioAlterarDados.class)))
			.thenReturn(true);
		
		boolean expected = usuarioService.alterarDados(usuario);
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveBuscarPorEmailAndSocialId() throws Exception {
		
		Usuario usuario = new Usuario();
		
		when(this.usuarioDao.buscarPorEmailAndSocialId("email@email.com", "12312"))
			.thenReturn(usuario);
		
		Usuario expected = usuarioService.buscarPorEmailAndSocialId("email@email.com", "12312");
		
		assertEquals(expected, usuario);
		
	}
	
	@Test
	public void deveAtualizarFoto() throws Exception {
		
		when(this.usuarioDao.atualizarFoto(1L, "url"))
			.thenReturn(true);
		
		boolean expected = usuarioService.atualizarFoto(1L, "url");
		
		assertEquals(expected, true);
		
	}
	
	@Test
	public void deveRetornaQtdePasseiosEfetuados() throws Exception {
		
		when(this.usuarioDao.retornaQtdePasseiosEfetuados(1L))
			.thenReturn(0L);
		
		Long expected = usuarioService.retornaQtdePasseiosEfetuados(1L);
		
		assertEquals(expected, 0L);
			
	}
	
	@Test
	public void deveRegistrarSocialLogin_UsuarioJaCriado() throws Exception {
		
		UsuarioSocialLogin usuarioSocialLogin = new UsuarioSocialLogin();
		
		usuarioSocialLogin.setEmail("email");
		usuarioSocialLogin.setSocialId("socialId");
		
		Usuario usuario = new Usuario();
		
		usuario.setId(1L);
		
		UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
		usuarioAutenticado.setId(usuario.getId());
		
		when(this.usuarioDao.buscarPorEmailAndSocialId(usuarioSocialLogin.getEmail(), usuarioSocialLogin.getSocialId()))
			.thenReturn(usuario);
		
		when(this.usuarioDao.registrarSocialLogin(Mockito.any(UsuarioSocialLogin.class)))
			.thenReturn(usuarioAutenticado);
		
		UsuarioAutenticado expected = usuarioService.registrarSocialLogin(usuarioSocialLogin);
		
		assertEquals(expected.getId(), usuarioAutenticado.getId());
		
	}
	
	@Test
	public void deveRegistrarSocialLogin_UsuarioJaCriadoNull() throws Exception {
		
		UsuarioSocialLogin usuarioSocialLogin = new UsuarioSocialLogin();
		
		usuarioSocialLogin.setEmail("email");
		usuarioSocialLogin.setSocialId("socialId");
		
		UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
		
		when(this.usuarioDao.buscarPorEmailAndSocialId(usuarioSocialLogin.getEmail(), usuarioSocialLogin.getSocialId()))
			.thenReturn(null);
		
		when(this.usuarioDao.registrarSocialLogin(Mockito.any(UsuarioSocialLogin.class)))
			.thenReturn(usuarioAutenticado);
		
		UsuarioAutenticado expected = usuarioService.registrarSocialLogin(usuarioSocialLogin);
		
		assertEquals(expected, usuarioAutenticado);
		
	}
	
	@Test
	public void deveRegistrarSocialLogin_UsuarioJaCriadoIdNull() throws Exception {
		
		UsuarioSocialLogin usuarioSocialLogin = new UsuarioSocialLogin();
		
		usuarioSocialLogin.setEmail("email");
		usuarioSocialLogin.setSocialId("socialId");
		
		Usuario usuario = new Usuario();
		usuario.setId(null);
		
		UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();
		
		when(this.usuarioDao.buscarPorEmailAndSocialId(usuarioSocialLogin.getEmail(), usuarioSocialLogin.getSocialId()))
			.thenReturn(usuario);
		
		when(this.usuarioDao.registrarSocialLogin(Mockito.any(UsuarioSocialLogin.class)))
			.thenReturn(usuarioAutenticado);
		
		UsuarioAutenticado expected = usuarioService.registrarSocialLogin(usuarioSocialLogin);
		
		assertEquals(expected, usuarioAutenticado);
		
	}
	
}
