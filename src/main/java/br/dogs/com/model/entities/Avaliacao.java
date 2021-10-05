package br.dogs.com.model.entities;

public class Avaliacao extends BaseEntity {

	double nota;
	String descricao;
	Long passeioId;

	public double getNota() {
		return nota;
	}

	public void setNota(double nota) {
		this.nota = nota;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getPasseioId() {
		return passeioId;
	}

	public void setPasseioId(Long passeioId) {
		this.passeioId = passeioId;
	}

}
