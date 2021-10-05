package br.dogs.com.model.entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Passeio extends BaseEntity {
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime datahora;
	
	private String status;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Timestamp datahorafinalizacao;
	
	private Long dogwalkerId;
	private Long tutorId;
	private List<Long> cachorrosIds;
	private List<Cachorro> cachorros;
	private Usuario dogwalker;
	private Usuario tutor;

	public LocalDateTime getDatahora() {
		return datahora;
	}

	public void setDatahora(LocalDateTime datahora) {
		this.datahora = datahora;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getDatahorafinalizacao() {
		return datahorafinalizacao;
	}

	public void setDatahorafinalizacao(Timestamp datahorafinalizacao) {
		this.datahorafinalizacao = datahorafinalizacao;
	}

	public Long getDogwalkerId() {
		return dogwalkerId;
	}

	public void setDogwalkerId(Long dogwalkerId) {
		this.dogwalkerId = dogwalkerId;
	}

	public Long getTutorId() {
		return tutorId;
	}

	public void setTutorId(Long tutorId) {
		this.tutorId = tutorId;
	}

	public List<Long> getCachorrosIds() {
		return cachorrosIds;
	}

	public void setCachorrosIds(List<Long> cachorrosIds) {
		this.cachorrosIds = cachorrosIds;
	}

	public List<Cachorro> getCachorros() {
		return cachorros;
	}

	public void setCachorros(List<Cachorro> cachorros) {
		this.cachorros = cachorros;
	}

	public Usuario getDogwalker() {
		return dogwalker;
	}

	public void setDogwalker(Usuario dogwalker) {
		this.dogwalker = dogwalker;
	}

	public Usuario getTutor() {
		return tutor;
	}

	public void setTutor(Usuario tutor) {
		this.tutor = tutor;
	}

}
