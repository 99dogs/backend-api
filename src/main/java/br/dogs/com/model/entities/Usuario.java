package br.dogs.com.model.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Usuario extends BaseEntity implements UserDetails, GrantedAuthority {

	private static final long serialVersionUID = 1L;

	private String nome;
	private String email;
	@JsonIgnore
	private String senha;
	private String telefone;
	private String rua;
	private String bairro;
	@JsonIgnore
	private int cidadeId;
	@JsonIgnore
	private int estadoId;
	@JsonIgnore
	private int paisId;
	private String numero;
	private String cep;
	private double avaliacao;
	private int qtdeTicketDisponivel;
	private String tipo;
	private Cidade cidade;
	private Estado estado;
	@JsonIgnore
	private List<Passeio> passeios;
	@JsonIgnore
	private String tokenPushNotification;

	public String getTokenPushNotification() {
		return tokenPushNotification;
	}

	public void setTokenPushNotification(String tokenPushNotification) {
		this.tokenPushNotification = tokenPushNotification;
	}

	public String getFotoUrl() {
		return fotoUrl;
	}

	public void setFotoUrl(String fotoUrl) {
		this.fotoUrl = fotoUrl;
	}

	private String fotoUrl;

	public List<Passeio> getPasseios() {
		return passeios;
	}

	public void setPasseios(List<Passeio> passeios) {
		this.passeios = passeios;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getRua() {
		return rua;
	}

	public void setRua(String rua) {
		this.rua = rua;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public int getCidadeId() {
		return cidadeId;
	}

	public void setCidadeId(int cidadeId) {
		this.cidadeId = cidadeId;
	}

	public int getEstadoId() {
		return estadoId;
	}

	public void setEstadoId(int estadoId) {
		this.estadoId = estadoId;
	}

	public int getPaisId() {
		return paisId;
	}

	public void setPaisId(int paisId) {
		this.paisId = paisId;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public double getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(double avaliacao) {
		this.avaliacao = avaliacao;
	}

	public int getQtdeTicketDisponivel() {
		return qtdeTicketDisponivel;
	}

	public void setQtdeTicketDisponivel(int qtdeTicketDisponivel) {
		this.qtdeTicketDisponivel = qtdeTicketDisponivel;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@JsonIgnore
	@Override
	public String getAuthority() {
		if (this.getTipo() != null) {
			return this.getTipo().toUpperCase();
		}

		return this.getTipo();
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> grantedAuthoriyList = new ArrayList<GrantedAuthority>();
		grantedAuthoriyList.add(new SimpleGrantedAuthority("ROLE_" + this.getTipo()));
		return grantedAuthoriyList;
	}

	@JsonIgnore
	@Override
	public String getPassword() {
		return this.getSenha();
	}

	@JsonIgnore
	@Override
	public String getUsername() {
		return this.getEmail();
	}

	@Override
	public String toString() {
		return "Usuario [nome=" + nome + ", email=" + email + ", senha=" + senha + ", telefone=" + telefone + ", rua="
				+ rua + ", bairro=" + bairro + ", cidadeId=" + cidadeId + ", estadoId=" + estadoId + ", paisId="
				+ paisId + ", numero=" + numero + ", avaliacao=" + avaliacao + ", tipo=" + tipo + ", cidade=" + cidade
				+ ", estado=" + estado + ", passeios=" + passeios + ", getPasseios()=" + getPasseios() + ", getNome()="
				+ getNome() + ", getEmail()=" + getEmail() + ", getSenha()=" + getSenha() + ", getTelefone()="
				+ getTelefone() + ", getRua()=" + getRua() + ", getBairro()=" + getBairro() + ", getCidadeId()="
				+ getCidadeId() + ", getEstadoId()=" + getEstadoId() + ", getPaisId()=" + getPaisId() + ", getNumero()="
				+ getNumero() + ", getAvaliacao()=" + getAvaliacao() + ", getTipo()=" + getTipo() + ", getCidade()="
				+ getCidade() + ", getEstado()=" + getEstado() + ", isAccountNonExpired()=" + isAccountNonExpired()
				+ ", isAccountNonLocked()=" + isAccountNonLocked() + ", isCredentialsNonExpired()="
				+ isCredentialsNonExpired() + ", isEnabled()=" + isEnabled() + ", getAuthority()=" + getAuthority()
				+ ", getAuthorities()=" + getAuthorities() + ", getPassword()=" + getPassword() + ", getUsername()="
				+ getUsername() + ", getId()=" + getId() + ", getCriado()=" + getCriado() + ", getModificado()="
				+ getModificado() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(avaliacao);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((bairro == null) ? 0 : bairro.hashCode());
		result = prime * result + ((cidade == null) ? 0 : cidade.hashCode());
		result = prime * result + cidadeId;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
		result = prime * result + estadoId;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		result = prime * result + paisId;
		result = prime * result + ((passeios == null) ? 0 : passeios.hashCode());
		result = prime * result + ((rua == null) ? 0 : rua.hashCode());
		result = prime * result + ((senha == null) ? 0 : senha.hashCode());
		result = prime * result + ((telefone == null) ? 0 : telefone.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (Double.doubleToLongBits(avaliacao) != Double.doubleToLongBits(other.avaliacao))
			return false;
		if (bairro == null) {
			if (other.bairro != null)
				return false;
		} else if (!bairro.equals(other.bairro))
			return false;
		if (cidade == null) {
			if (other.cidade != null)
				return false;
		} else if (!cidade.equals(other.cidade))
			return false;
		if (cidadeId != other.cidadeId)
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (estadoId != other.estadoId)
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		if (paisId != other.paisId)
			return false;
		if (passeios == null) {
			if (other.passeios != null)
				return false;
		} else if (!passeios.equals(other.passeios))
			return false;
		if (rua == null) {
			if (other.rua != null)
				return false;
		} else if (!rua.equals(other.rua))
			return false;
		if (senha == null) {
			if (other.senha != null)
				return false;
		} else if (!senha.equals(other.senha))
			return false;
		if (telefone == null) {
			if (other.telefone != null)
				return false;
		} else if (!telefone.equals(other.telefone))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		return true;
	}

}
