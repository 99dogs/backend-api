package br.dogs.com.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import br.dogs.com.model.dto.UsuarioAlterarDados;
import br.dogs.com.model.dto.UsuarioAutenticado;
import br.dogs.com.model.dto.UsuarioRegistro;
import br.dogs.com.model.dto.UsuarioSocialLogin;
import br.dogs.com.model.entities.Usuario;

public interface UsuarioService extends UserDetailsService {
	
	boolean alterar(Long id);
	
	boolean deletarPorId(Long id);
	
	public UsuarioAutenticado registrar(UsuarioRegistro entity);
	
	public Usuario buscarPorId(Long id);
	
	public Usuario buscarPorEmail(String email);
	
	public List<Usuario> buscarDogwalkers();
	
	public List<Usuario> buscarTutores();
	
	public List<Usuario> buscarTodos();
	
	public boolean alterarDados(UsuarioAlterarDados dados);
	
	public Usuario buscarPorEmailAndSocialId(String email, String socialId);
	
	public UsuarioAutenticado registrarSocialLogin(UsuarioSocialLogin entity);
	
	public boolean atualizarFoto(Long id, String url);
	
	public Long retornaQtdePasseiosEfetuados(Long id);
	
}
