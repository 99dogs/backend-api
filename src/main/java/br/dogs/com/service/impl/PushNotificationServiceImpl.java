package br.dogs.com.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.dogs.com.model.dto.push_notificacation.Data;
import br.dogs.com.model.dto.push_notificacation.Message;
import br.dogs.com.model.dto.push_notificacation.Notification;
import br.dogs.com.model.entities.Passeio;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.PasseioService;
import br.dogs.com.service.PushNotificationService;
import br.dogs.com.service.UsuarioService;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {
	
	Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);
	
	@Autowired
	private PasseioService passeioService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Value("${fcm_endpoint}")
	private String endpoint;
	
	@Value("${token_server_firebase}")
	private String tokenServerFirebase;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	
	private boolean enviar(Message message) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", "key=" + this.tokenServerFirebase);		
		
		try {
			
			HttpEntity<Message> requestEntity = new HttpEntity<>(message, headers);
			
			ResponseEntity<String> requestResponse = restTemplate.exchange(
				endpoint, 
				HttpMethod.POST, 
				requestEntity,
				String.class
			);
			
			if(requestResponse.getStatusCodeValue() == 200) {
				return true;
			}
			
		} catch (Exception e) {
			
			logger.error(e.getMessage());
			
		}
		
		return false;
		
	}
	
	@Override
	public boolean passeioAceito(Long id) {
		
		Passeio passeio = passeioService.buscarPorId(id);
		Usuario usuario = usuarioService.buscarPorId(passeio.getTutorId());
		
		Message message = new Message();
		Notification notification = new Notification();
		Data data = new Data();
		HashMap<String, String> payload = new HashMap<>();
		
		String body = "Seu passeio para o dia " + passeio.getDatahora().format(formatter) + " foi aceito.";
		
		notification.setTitle("Nova atualização sobre seu passeio #" + id);
		notification.setBody(body);
		
		payload.put("sub", passeio.getTutorId().toString());
		
		data.setClick_action("FLUTTER_NOTIFICATION_CLICK");
		data.setId(String.valueOf(System.currentTimeMillis()));
		data.setPayload(payload);
		
		message.setNotification(notification);
		message.setData(data);
		message.setPriority("high");
		message.setTo(usuario.getTokenPushNotification());
		
		return enviar(message);
		
	}

	@Override
	public boolean passeioRecusado(Long id) {
		
		Passeio passeio = passeioService.buscarPorId(id);
		Usuario usuario = usuarioService.buscarPorId(passeio.getTutorId());
		
		Message message = new Message();
		Notification notification = new Notification();
		Data data = new Data();
		HashMap<String, String> payload = new HashMap<>();
		
		String body = "Seu passeio para o dia " + passeio.getDatahora().format(formatter) + " foi recusado.";
		
		notification.setTitle("Nova atualização sobre seu passeio #" + id);
		notification.setBody(body);
		
		payload.put("sub", passeio.getTutorId().toString());
		
		data.setClick_action("FLUTTER_NOTIFICATION_CLICK");
		data.setId(String.valueOf(System.currentTimeMillis()));
		data.setPayload(payload);
		
		message.setNotification(notification);
		message.setData(data);
		message.setPriority("high");
		message.setTo(usuario.getTokenPushNotification());
		
		return enviar(message);
		
	}

	@Override
	public boolean passeioSolicitado(Long id) {
		
		Passeio passeio = passeioService.buscarPorId(id);
		Usuario usuario = usuarioService.buscarPorId(passeio.getDogwalkerId());
		
		Message message = new Message();
		Notification notification = new Notification();
		Data data = new Data();
		HashMap<String, String> payload = new HashMap<>();
		
		String body = "Um novo agendamento de passeio para o dia " + passeio.getDatahora().format(formatter);
		
		notification.setTitle("Novo passeio");
		notification.setBody(body);
		
		payload.put("sub", passeio.getDogwalkerId().toString());
		
		data.setClick_action("FLUTTER_NOTIFICATION_CLICK");
		data.setId(String.valueOf(System.currentTimeMillis()));
		data.setPayload(payload);
		
		message.setNotification(notification);
		message.setData(data);
		message.setPriority("high");
		message.setTo(usuario.getTokenPushNotification());
		
		return enviar(message);
		
	}

	@Override
	public boolean saldoCreditado(Long id) {
		
		Passeio passeio = passeioService.buscarPorId(id);
		Usuario usuario = usuarioService.buscarPorId(passeio.getDogwalkerId());
		
		Message message = new Message();
		Notification notification = new Notification();
		Data data = new Data();
		HashMap<String, String> payload = new HashMap<>();
		
		String body = "O saldo referente ao passeio #" + id + " foi creditado em sua conta";
		
		notification.setTitle("Novo saldo disponível");
		notification.setBody(body);
		
		payload.put("sub", passeio.getDogwalkerId().toString());
		
		data.setClick_action("FLUTTER_NOTIFICATION_CLICK");
		data.setId(String.valueOf(System.currentTimeMillis()));
		data.setPayload(payload);
		
		message.setNotification(notification);
		message.setData(data);
		message.setPriority("high");
		message.setTo(usuario.getTokenPushNotification());
			
		if(usuario.getTokenPushNotification() == null || usuario.getTokenPushNotification().isBlank()) {
			return false;
		}
		
		return enviar(message);
		
	}

}
