package br.dogs.com.model.dto;

public class UsuarioAutenticado {
	
	private Long id;
	private String token = "Faça o login para obter o token.";
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
