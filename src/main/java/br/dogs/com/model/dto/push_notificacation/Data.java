package br.dogs.com.model.dto.push_notificacation;

import java.util.HashMap;

public class Data {

	private String click_action;
	private String id;
	private HashMap<String, String> payload;

	public String getClick_action() {
		return click_action;
	}

	public void setClick_action(String click_action) {
		this.click_action = click_action;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public HashMap<String, String> getPayload() {
		return payload;
	}

	public void setPayload(HashMap<String, String> payload) {
		this.payload = payload;
	}

}
