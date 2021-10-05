package br.dogs.com.model.dto;

import br.dogs.com.model.entities.BaseEntity;

public class PasseioLatLong extends BaseEntity {

	private String latitude;
	private String longitude;
	private Long passeioId;

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public Long getPasseioId() {
		return passeioId;
	}

	public void setPasseioId(Long passeioId) {
		this.passeioId = passeioId;
	}

}
