package br.dogs.com.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.dogs.com.helper.PasseioStatus;
import br.dogs.com.helper.TipoUsuario;
import br.dogs.com.model.entities.Avaliacao;
import br.dogs.com.model.entities.Passeio;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.AvaliacaoService;
import br.dogs.com.service.PasseioService;
import br.dogs.com.service.UsuarioService;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class AvaliacaoRestControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext context;
		
	@InjectMocks
	private AvaliacaoRestController avaliacaoRestController;
	
	@MockBean
	private AvaliacaoService avaliacaoService;

	@MockBean
	private PasseioService passeioService;
	
	@MockBean
	private UsuarioService usuarioService;
		
	@Autowired
	FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	private Usuario authUsuarioTutor = new Usuario();
	
	private Usuario authUsuarioDogwalker = new Usuario();
	
	private String tokenUsuarioTutor;

	private String tokenUsuarioDogwalker;
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void startMocks(){
		avaliacaoRestController = context.getBean(AvaliacaoRestController.class);

		 mockMvc = MockMvcBuilders
		            .standaloneSetup(avaliacaoRestController)
		            .alwaysDo(print())
		            .apply(springSecurity(springSecurityFilterChain))
		            .build();

	    MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void setUp() {
		
		standaloneSetup(avaliacaoService);
		standaloneSetup(passeioService);
		standaloneSetup(usuarioService);
		
		authUsuarioTutor.setId(1L);
		authUsuarioTutor.setEmail("tutor");
		authUsuarioTutor.setTipo(TipoUsuario.TUTOR.name());
		
		authUsuarioDogwalker.setId(1L);
		authUsuarioDogwalker.setEmail("dogwalker");
		authUsuarioDogwalker.setTipo(TipoUsuario.DOGWALKER.name());
		
		tokenUsuarioTutor     = jwtTokenProvider.createToken(authUsuarioTutor);
		tokenUsuarioDogwalker = jwtTokenProvider.createToken(authUsuarioDogwalker);
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarAvaliacoesPorDogwalker_DogwalkerCorreto() throws Exception {
		
		Usuario dogwalker = new Usuario();
		
		dogwalker.setId(21L);
		dogwalker.setEmail("dogwalker");
		dogwalker.setTipo(TipoUsuario.DOGWALKER.name());
		
		List<Avaliacao> avaliacoes = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername("dogwalker"))
			.thenReturn(authUsuarioDogwalker);
		
		when(this.usuarioService.buscarPorId(21L))
			.thenReturn(dogwalker);
		
		when(this.avaliacaoService.buscarTodosPorDogwalker(21L))
			.thenReturn(avaliacoes);
		
		mockMvc.perform(MockMvcRequestBuilders
			      .get("/api/v1/avaliacao/dogwalker/{id}", 21)
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioDogwalker)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarAvaliacoesPorDogwalker_UsuarioIncorreto() throws Exception {
		
		Usuario usuario = new Usuario();
		
		usuario.setId(22L);
		usuario.setEmail("tutor");
		usuario.setTipo(TipoUsuario.TUTOR.name());
		
		List<Avaliacao> avaliacoes = new ArrayList<>();
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(22L))
			.thenReturn(usuario);
		
		when(this.avaliacaoService.buscarTodosPorDogwalker(22L))
			.thenReturn(avaliacoes);
		
		mockMvc.perform(MockMvcRequestBuilders
			      .get("/api/v1/avaliacao/dogwalker/{id}", 22)
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarAvaliacoesPorDogwalker_UsuarioNaoExistente() throws Exception {
		
		Usuario usuario = new Usuario();
				
		usuario.setId(0L);
		usuario.setEmail("tutor");
		usuario.setTipo(TipoUsuario.TUTOR.name());
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.usuarioService.buscarPorId(22L))
			.thenReturn(usuario);
		
		mockMvc.perform(MockMvcRequestBuilders
			      .get("/api/v1/avaliacao/dogwalker/{id}", 22L)
			      .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor)
			      .accept(MediaType.APPLICATION_JSON))
			      .andDo(print())
			      .andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornaSucesso_QuandoCadastrarUmaAvaliacao() throws Exception {
		
		Avaliacao avaliacao = new Avaliacao();
		
		avaliacao.setId(10L);
		avaliacao.setNota(4);
		avaliacao.setDescricao("test descricao");
		avaliacao.setPasseioId(10L);
		
		Passeio passeio = new Passeio();
		
		passeio.setId(10L);
		passeio.setStatus(PasseioStatus.Finalizado.toString());
		passeio.setTutorId(20L);
		
		Usuario usuario = new Usuario();
		
		usuario.setId(20L);
		usuario.setEmail("tutor");
		usuario.setTipo(TipoUsuario.TUTOR.name());
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(usuario);
		
		when(this.passeioService.buscarPorId(avaliacao.getPasseioId()))
			.thenReturn(passeio);
		
		when(this.avaliacaoService.cadastrar(Mockito.any(Avaliacao.class)))
			.thenReturn(avaliacao);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(avaliacao);
		
		String token = jwtTokenProvider.createToken(usuario);
		
		mockMvc.perform(post("/api/v1/avaliacao")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoDeletarUmaAvaliacao() throws Exception {
		
		Avaliacao avaliacao = new Avaliacao();
		Passeio passeio = new Passeio();
		
		avaliacao.setId(1L);
		avaliacao.setPasseioId(2L);
		
		passeio.setId(2L);
		passeio.setTutorId(authUsuarioTutor.getId());
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.avaliacaoService.buscarPorId(1L))
			.thenReturn(avaliacao);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.avaliacaoService.deletar(avaliacao.getId()))
			.thenReturn(true);
		
		when(this.avaliacaoService.atualizarMediaAvaliacao(passeio.getId()))
			.thenReturn(true);
		
		mockMvc.perform(delete("/api/v1/avaliacao/{id}", avaliacao.getId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor))
        		.andExpect(status().isOk());
		
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoDeletarUmaAvaliacao_UsuarioDivergente() throws Exception {
		
		Avaliacao avaliacao = new Avaliacao();
		Passeio passeio = new Passeio();
		
		avaliacao.setId(1L);
		avaliacao.setPasseioId(2L);
		
		passeio.setId(2L);
		passeio.setTutorId(999L);
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.avaliacaoService.buscarPorId(1L))
			.thenReturn(avaliacao);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		when(this.avaliacaoService.deletar(avaliacao.getId()))
			.thenReturn(true);
		
		mockMvc.perform(delete("/api/v1/avaliacao/{id}", avaliacao.getId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor))
        		.andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarPorPasseioId() throws Exception {
		
		Avaliacao avaliacao = new Avaliacao();
		Passeio passeio = new Passeio();
		
		avaliacao.setId(1L);
		avaliacao.setPasseioId(2L);
		
		passeio.setId(2L);
		passeio.setTutorId(999L);
		
		when(this.usuarioService.loadUserByUsername("tutor"))
			.thenReturn(authUsuarioTutor);
		
		when(this.avaliacaoService.buscarPorPasseioId(avaliacao.getPasseioId()))
			.thenReturn(avaliacao);
		
		when(this.passeioService.buscarPorId(passeio.getId()))
			.thenReturn(passeio);
		
		mockMvc.perform(get("/api/v1/avaliacao/passeio/{id}", avaliacao.getPasseioId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenUsuarioTutor))
        		.andExpect(status().isBadRequest());
		
	}
	
}
