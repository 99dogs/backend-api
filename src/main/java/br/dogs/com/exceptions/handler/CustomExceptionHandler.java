package br.dogs.com.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import br.dogs.com.exceptions.InvalidJwtAuthenticationException;
import br.dogs.com.model.dto.ResponseData;

@RestControllerAdvice
public class CustomExceptionHandler {
		
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ResponseData> handleAllExceptions(Exception ex, WebRequest request) {

		ResponseData response = new ResponseData();
		
		response.setTemErro(true);
		response.setMensagem(ex.getMessage());
		
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler(InvalidJwtAuthenticationException.class)
	public final ResponseEntity<ResponseData> InvalidJwtAuthenticationException(Exception ex, WebRequest request) {

		ResponseData response = new ResponseData();
		
		response.setTemErro(true);
		response.setMensagem(ex.getMessage());
		
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseData> invalidFormatException(HttpMessageNotReadableException ex) {
		
		ResponseData response = new ResponseData();
		
		response.setTemErro(true);
		response.setMensagem(ex.getMessage());
		
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		
	}

	
}
