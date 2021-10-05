package br.dogs.com.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.dogs.com.model.dto.ResponseData;
import br.dogs.com.model.dto.UsuarioAlterarDados;
import br.dogs.com.model.dto.UsuarioAutenticado;
import br.dogs.com.model.dto.UsuarioLogin;
import br.dogs.com.model.dto.UsuarioRegistro;
import br.dogs.com.model.dto.UsuarioSocialLogin;
import br.dogs.com.model.entities.Cidade;
import br.dogs.com.model.entities.Estado;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.security.jwt.JwtTokenProvider;
import br.dogs.com.service.CidadeService;
import br.dogs.com.service.EstadoService;
import br.dogs.com.service.UsuarioService;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/usuario")
public class UsuarioRestController {
		
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private EstadoService estadoService;
	
	@Autowired
	private CidadeService cidadeService;
	
	@Value("${endpoint.api}")
	private String endpointApi; 
	
	@Value("${path_upload_images}")
	private String pathImages;
	
	@ApiOperation("Endpoint para se cadastrar na plataforma.")
	@RequestMapping(value="/registrar", method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> registrar(@RequestBody UsuarioRegistro usuarioRegistro){
		
		String senha = usuarioRegistro.getSenha();
		String email = usuarioRegistro.getEmail();
		
		UsuarioAutenticado responseUsuarioCriado = new UsuarioAutenticado();
		ResponseData responseData = new ResponseData();
		
		try {
			
			if(senha.isEmpty() || email.isEmpty()) {
				throw new Exception("É necessário informar o e-mail e senha.");
			}
			
			if(usuarioRegistro.getTipo().isEmpty()) {
				throw new Exception("É necessário o tipo do usuário");
			}
			
			Usuario usuarioExistente = usuarioService.buscarPorEmail(email);
			if(usuarioExistente.getId() != null) {
				
				responseUsuarioCriado.setId(usuarioExistente.getId());
				
				responseData.setMensagem("Usuário já possui cadastro.");
				
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			}
			
			usuarioRegistro.setSenha(passwordEncoder.encode(senha));
			responseUsuarioCriado = usuarioService.registrar(usuarioRegistro);
			
			if(responseUsuarioCriado != null) {
				
				Usuario novoUsuario = usuarioService.buscarPorEmail(email);
				
				responseUsuarioCriado.setId(novoUsuario.getId());
				responseUsuarioCriado.setToken(jwtTokenProvider.createToken(novoUsuario));
								
				return ResponseEntity.ok(responseUsuarioCriado);
				
			}else {
				throw new Exception("Ocorreu um problema ao cadastrar o usuário.");
			}
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem("Exception");
			responseData.setConteudo(e.getMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para autenticar e obter o token de acesso.")
	@RequestMapping(value="/login", method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<UsuarioAutenticado> login(@RequestBody UsuarioLogin usuarioLogin){
		
		try {
			
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuarioLogin.getEmail(), usuarioLogin.getSenha()));
			
			UsuarioAutenticado usuarioCriado = new UsuarioAutenticado();
			Usuario usuario = usuarioService.buscarPorEmail(usuarioLogin.getEmail());
			
			if(usuario != null) {
				
				usuarioCriado.setId(usuario.getId());
				usuarioCriado.setToken(jwtTokenProvider.createToken(usuario));
				
			}else {
				throw new UsernameNotFoundException("Login incorreto");
			}
			
			return ResponseEntity.ok(usuarioCriado);
			
		} catch (AuthenticationException e) {
			throw new BadCredentialsException("Login incorreto");
		}
		
	}
	
	@ApiOperation("Retorna a listagem de dog walkers cadastrados na plataforma.")
	@RequestMapping(value="/dogwalker", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarDogwalkers(){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			List<Usuario> dogwalkers = usuarioService.buscarDogwalkers();
						
			return ResponseEntity.ok(dogwalkers);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem("Exception");
			responseData.setConteudo(e.getMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Retorna a listagem de tutores cadastrados na plataforma.")
	@RequestMapping(value="/tutor", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTutores(){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			List<Usuario> tutores = usuarioService.buscarTutores();
						
			return ResponseEntity.ok(tutores);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem("Exception");
			responseData.setConteudo(e.getMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Retorna a listagem de todos os usuários cadastrados na plataforma.")
	@RequestMapping(value="/todos", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarTodos(){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			List<Usuario> usuarios = usuarioService.buscarTodos();
						
			return ResponseEntity.ok(usuarios);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem("Exception");
			responseData.setConteudo(e.getMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para alterar as informações de um usuário específico.")
	@RequestMapping(value="/dados", method = RequestMethod.PUT, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> alterar(@RequestBody UsuarioAlterarDados dados){
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseData responseData = new ResponseData();
		
		try {
			
			dados.setCep(dados.getCep().replace("-", ""));
			dados.setCep(dados.getCep().substring(0, dados.getCep().length() - 1));
			
			dados.setTelefone(dados.getTelefone().replace("(", ""));
			dados.setTelefone(dados.getTelefone().replace(")", ""));
			dados.setTelefone(dados.getTelefone().replace(" ", ""));
			
			if(dados.getNome() == null || dados.getNome().isEmpty()) {
				throw new Exception("Campo nome não pode ser vazio.");
			}
			
			if(dados.getEstadoId() == null || dados.getEstadoId() == 0) {
				throw new Exception("Campo estadoId não pode ser vazio ou igual a zero.");
			}
			
			if(dados.getCidadeId() == null || dados.getCidadeId() == 0) {
				throw new Exception("Campo cidadeId não pode ser vazio ou igual a zero.");
			}
			
			if(dados.getCep() != null && dados.getCep().length() > 8) {
				throw new Exception("Campo cep deve possuir 8 dígitos.");
			}
			
			Estado estado = estadoService.buscarPorId(dados.getEstadoId());
			
			if(estado == null || estado.getId() == null){
				throw new Exception("Estado informado não encontrado.");
			}
			
			if(estado.isAtivo() == false) {
				throw new Exception("Estado informado está inativo.");
			}
			
			Cidade cidade = cidadeService.buscarPorId(dados.getCidadeId());
			
			if(cidade == null || cidade.getId() == null){
				throw new Exception("Cidade informada não encontrada.");
			}
			
			if(cidade.isAtivo() == false) {
				throw new Exception("Cidade informada está inativa.");
			}
						
			Usuario usuarioExistente = usuarioService.buscarPorId(usuario.getId());
			
			if(usuarioExistente == null || usuarioExistente.getId() == null) {
				responseData.setTemErro(true);
				responseData.setMensagem("Usuário não encontrado");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
						
			dados.setUsuarioId(usuario.getId());
			boolean usuarioAlterado = usuarioService.alterarDados(dados);
			
			if(usuarioAlterado == false) {
				throw new Exception("Ocorre um problema ao alterar os dados..");
			}
						
			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}
		
	}
	
	@ApiOperation("Endpoint para buscar os dados do usuário.")
	@RequestMapping(value="/me", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarMeusDados(){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			Usuario dadosUsuario = usuarioService.buscarPorId(usuario.getId());

			if(dadosUsuario == null || dadosUsuario.getId() == null || dadosUsuario.getId() == 0) {
				responseData.setTemErro(true);
				responseData.setMensagem("Usuário não encontrado.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
			}
						
			return ResponseEntity.ok(dadosUsuario);
			
		} catch (Exception e) {
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}		
		
	}
	
	@ApiOperation("Retorna os dados do dog walker por id.")
	@RequestMapping(value="/dogwalker/{id}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> buscarDogwalkerPorId(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			Usuario usuario = usuarioService.buscarPorId(id);
						
			return ResponseEntity.ok(usuario);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem("Exception");
			responseData.setConteudo(e.getMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para efetuar login via conta social e obter o token de acesso.")
	@RequestMapping(value="/social-login", method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> socialLogin(@RequestBody UsuarioSocialLogin usuarioSocialLogin){
		
		try {
			
			if(usuarioSocialLogin.getNome() == null || usuarioSocialLogin.getNome().isEmpty()) {
				throw new Exception("É necessário informar o nome.");
			}
			
			if(usuarioSocialLogin.getEmail() == null || usuarioSocialLogin.getEmail().isEmpty()) {
				throw new Exception("É necessário informar o e-mail.");
			}
			
			if(usuarioSocialLogin.getSocialId() == null || usuarioSocialLogin.getSocialId().isEmpty()) {
				throw new Exception("É necessário informar o social id.");
			}
			
			if(usuarioSocialLogin.getTipo() == null || usuarioSocialLogin.getTipo().isEmpty()) {
				throw new Exception("É necessário informar o tipo do usuario.");
			}
			
			UsuarioAutenticado usuarioCriado = new UsuarioAutenticado();
			usuarioCriado = usuarioService.registrarSocialLogin(usuarioSocialLogin);
			
			if(usuarioCriado.getId() != null) {
				
				Usuario usuario = usuarioService.buscarPorId(usuarioCriado.getId());
				usuarioCriado.setToken(jwtTokenProvider.createToken(usuario));
				
				return ResponseEntity.ok(usuarioCriado);
				
			}
			
			throw new Exception("Ocorreu um problema ao tentar realizar o login via rede social.");
			
		} catch (Exception e) {
			
			ResponseData responseData = new ResponseData();
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
	@ApiOperation("Endpoint para atualizar a foto do usuário.")
	@RequestMapping(value="/upload/foto/{id}", method = RequestMethod.POST, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> uploadFoto(@RequestParam MultipartFile foto, @PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		try {
			
			Usuario usuarioExistente = usuarioService.buscarPorId(id);
			
			if(usuarioExistente.getId() == null || usuarioExistente.getId() == 0) {
				throw new Exception("Usuário não encontrado.");
			}
			
			if(usuarioExistente.getId() != usuario.getId()) {
				throw new Exception("Você não possui permissão para acessar esse objeto.");
			}
			
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(id.toString().getBytes());
			String photoId = new String(Hex.encode(hash));
			
			String photoName = photoId +".png";
			
			String fileLocation = new File(pathImages).getAbsolutePath() + "/" + photoName;

			FileOutputStream output = new FileOutputStream(fileLocation);

			output.write(foto.getBytes());
			output.close();
			
			String fotoUrl = endpointApi + "/api/v1/usuario/image/" + id;
			
			boolean alterado = usuarioService.atualizarFoto(id, fotoUrl);
			
			if(alterado == false) {
				throw new Exception("Ocorreu um problema ao atualizar a foto do usuário.");
			}
			
			Map<String, String> response = new HashMap<>();
			response.put("url", fotoUrl);
			
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
		}
		
	}
	
	@ApiOperation("Retorna a imagem do usuário.")
	@GetMapping(value = "/image/{id}", produces = {MediaType.IMAGE_PNG_VALUE,MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<Resource> image(@PathVariable Long id) throws IOException {
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(id.toString().getBytes());
			String photoId = new String(Hex.encode(hash));
			
	        final ByteArrayResource inputStream = new ByteArrayResource(Files.readAllBytes(Paths.get(
	        		pathImages + "/" + photoId + ".png"
	        )));
	        return ResponseEntity
	                .status(HttpStatus.OK)
	                .contentLength(inputStream.contentLength())
	                .body(inputStream);
		} catch (Exception e) {
			final ByteArrayResource inputStream = new ByteArrayResource(Files.readAllBytes(Paths.get(
	        		pathImages + "/user-notfound.png"
	        )));
	        return ResponseEntity
	                .status(HttpStatus.OK)
	                .contentLength(inputStream.contentLength())
	                .body(inputStream);
		}

    }
	
	@ApiOperation("Retorna a quantidade de passeios efetuados por um dog walker.")
	@RequestMapping(value="/dogwalker/passeios-efetuados/{id}", method = RequestMethod.GET, produces = {"application/json; charset=utf-8"})
	public ResponseEntity<Object> retornaQtdePasseiosEfetuados(@PathVariable Long id){
		
		ResponseData responseData = new ResponseData();
		
		try {
			
			Long quantidade = usuarioService.retornaQtdePasseiosEfetuados(id);
						
			return ResponseEntity.ok(quantidade);
			
		} catch (Exception e) {
			
			responseData.setTemErro(true);
			responseData.setMensagem(e.getMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
			
		}
		
	}
	
}
