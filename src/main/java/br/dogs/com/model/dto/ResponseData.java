package br.dogs.com.model.dto;

import java.util.HashMap;

public class ResponseData {

	private boolean temErro = false;
	private String mensagem = "";
	private Object conteudo = new HashMap<>();

	public boolean isTemErro() {
		return temErro;
	}

	public void setTemErro(boolean temErro) {
		this.temErro = temErro;
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

	@Override
	public String toString() {
		return "ResponseData [temErro=" + temErro + ", mensagem=" + mensagem + ", conteudo=" + conteudo + "]";
	}

}
