package br.dogs.com.model.entities;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

public class BaseEntity {
	
	private Long id;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00")
	private Timestamp criado;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00")
	private Timestamp modificado;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Timestamp getCriado() {
		return criado;
	}
	public void setCriado(Timestamp criado) {
		this.criado = criado;
	}
	public Timestamp getModificado() {
		return modificado;
	}
	public void setModificado(Timestamp modificado) {
		this.modificado = modificado;
	}
	
}
