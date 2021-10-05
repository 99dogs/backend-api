package br.dogs.com.model.dto;

import java.util.ArrayList;

public class ResponseFatura {

	private boolean temErro;
	private String id;
	private String url;
	private String mensagem = "";
	private Object conteudo = new ArrayList<>();

	public boolean isTemErro() {
		return temErro;
	}

	public void setTemErro(boolean temErro) {
		this.temErro = temErro;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Object getConteudo() {
		return conteudo;
	}

	public void setConteudo(Object conteudo) {
		this.conteudo = conteudo;
	}

}
