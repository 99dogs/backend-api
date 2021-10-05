package br.dogs.com.model.entities;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Cachorro extends BaseEntity {
	
	private String nome;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date dataNascimento;
	
	private String comportamento;
	private Long racaId;
	private Long porteId;
	private Long usuarioId;
	private Raca raca;
	private Porte porte;
	@JsonIgnore
	private Usuario usuario;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getComportamento() {
		return comportamento;
	}

	public void setComportamento(String comportamento) {
		this.comportamento = comportamento;
	}

	public Long getRacaId() {
		return racaId;
	}

	public void setRacaId(Long racaId) {
		this.racaId = racaId;
	}

	public Long getPorteId() {
		return porteId;
	}

	public void setPorteId(Long porteId) {
		this.porteId = porteId;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Raca getRaca() {
		return raca;
	}

	public void setRaca(Raca raca) {
		this.raca = raca;
	}

	public Porte getPorte() {
		return porte;
	}

	public void setPorte(Porte porte) {
		this.porte = porte;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
