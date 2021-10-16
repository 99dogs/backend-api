package br.dogs.com.service;

public interface PushNotificationService {
	
	public boolean passeioAceito(Long id);
	
	public boolean passeioRecusado(Long id);
	
	public boolean passeioSolicitado(Long id);
	
	public boolean saldoCreditado(Long id);
	
}
