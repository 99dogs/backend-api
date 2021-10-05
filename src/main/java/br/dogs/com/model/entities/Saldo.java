package br.dogs.com.model.entities;

public class Saldo extends BaseEntity {

	private double unitario;
	private boolean depositado;
	private Long depositoId;
	private Long passeioId;
	private Long usuarioId;

	public double getUnitario() {
		return unitario;
	}

	public void setUnitario(double unitario) {
		this.unitario = unitario;
	}

	public boolean isDepositado() {
		return depositado;
	}

	public void setDepositado(boolean depositado) {
		this.depositado = depositado;
	}

	public Long getDepositoId() {
		return depositoId;
	}

	public void setDepositoId(Long depositoId) {
		this.depositoId = depositoId;
	}

	public Long getPasseioId() {
		return passeioId;
	}

	public void setPasseioId(Long passeioId) {
		this.passeioId = passeioId;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

}
