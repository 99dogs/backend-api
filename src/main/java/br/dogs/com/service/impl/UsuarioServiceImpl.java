package br.dogs.com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.dogs.com.database.dao.UsuarioDao;
import br.dogs.com.model.dto.UsuarioAlterarDados;
import br.dogs.com.model.dto.UsuarioAutenticado;
import br.dogs.com.model.dto.UsuarioRegistro;
import br.dogs.com.model.dto.UsuarioSocialLogin;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	@Autowired
	private UsuarioDao usuarioDao;

	@Override
	public boolean alterar(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deletarPorId(Long id) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Usuario buscarPorId(Long id) {
		return usuarioDao.buscarPorId(id);
	}
	
	@Override
	public Usuario buscarPorEmail(String email) {
		
		Usuario usuario = usuarioDao.buscarPorEmail(email);		
		return usuario;
		
	}

	@Override
	public UsuarioAutenticado registrar(UsuarioRegistro entity) {
		
		Usuario usuarioExistente = buscarPorEmail(entity.getEmail());

		if(usuarioExistente != null && usuarioExistente.getId() != null) {
			UsuarioAutenticado usuarioCriado = new UsuarioAutenticado();
			usuarioCriado.setId(usuarioExistente.getId());
			return usuarioCriado;
		}
		
		return usuarioDao.registrar(entity);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Usuario usuario = buscarPorEmail(username);
		
		if(usuario !=  null) {
			return usuario;
		}else {
			throw new UsernameNotFoundException("Login inválido");
		}
		
	}

	@Override
	public List<Usuario> buscarDogwalkers() {
		return usuarioDao.buscarDogwalkers();
	}

	@Override
	public List<Usuario> buscarTutores() {
		return usuarioDao.buscarTutores();
	}

	@Override
	public List<Usuario> buscarTodos() {
		return usuarioDao.buscarTodos();
	}

	@Override
	public boolean alterarDados(UsuarioAlterarDados dados) {
		return usuarioDao.alterarDados(dados);
	}

	@Override
	public Usuario buscarPorEmailAndSocialId(String email, String socialId) {
		Usuario usuario = usuarioDao.buscarPorEmailAndSocialId(email, socialId);		
		return usuario;
	}

	@Override
	public UsuarioAutenticado registrarSocialLogin(UsuarioSocialLogin entity) {
		
		Usuario usuarioExistente = buscarPorEmailAndSocialId(entity.getEmail(), entity.getSocialId());

		if(usuarioExistente != null && usuarioExistente.getId() != null) {
			UsuarioAutenticado usuarioCriado = new UsuarioAutenticado();
			usuarioCriado.setId(usuarioExistente.getId());
			return usuarioCriado;
		}
		
		return usuarioDao.registrarSocialLogin(entity);
		
	}

	@Override
	public boolean atualizarFoto(Long id, String url) {
		return usuarioDao.atualizarFoto(id, url);
	}

	@Override
	public Long retornaQtdePasseiosEfetuados(Long id) {
		return usuarioDao.retornaQtdePasseiosEfetuados(id);
	}

}
