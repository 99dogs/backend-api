package br.dogs.com.service.impl.payment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.dogs.com.model.dto.ResponseFatura;
import br.dogs.com.model.dto.TicketFatura;
import br.dogs.com.model.dto.safe2pay.Address;
import br.dogs.com.model.dto.safe2pay.Customer;
import br.dogs.com.model.dto.safe2pay.PaymentMethod;
import br.dogs.com.model.dto.safe2pay.Product;
import br.dogs.com.model.dto.safe2pay.ResponseSafe2Pay;
import br.dogs.com.model.dto.safe2pay.SingleSale;
import br.dogs.com.model.dto.safe2pay.Split;
import br.dogs.com.model.entities.Ticket;
import br.dogs.com.model.entities.Usuario;
import br.dogs.com.service.payment.Safe2PayPaymentService;

@Service
public class Safe2PayPaymentServiceImpl implements Safe2PayPaymentService {

	Logger logger = LoggerFactory.getLogger(Safe2PayPaymentServiceImpl.class);
	
	@Value("${safe2pay_api_token}")
	private String API_TOKEN;
	
	@Value("${safe2pay_callback_url}")
	private String CALLBACK_URL;
	
	@Override
	public ResponseFatura gerarFatura(TicketFatura entity, Ticket ticket, Usuario usuario) {

		ResponseFatura responseFatura = new ResponseFatura();
		
		String endpoint = "https://api.safe2pay.com.br/v2/SingleSale/Add";
		SingleSale payload = gerarPayload(entity, ticket, usuario);
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("X-API-KEY", this.API_TOKEN);		
		
		try {
			
			HttpEntity<SingleSale> requestEntity = new HttpEntity<>(payload, headers);
			
			ResponseEntity<ResponseSafe2Pay> requestResponse = restTemplate.exchange(
				endpoint, 
				HttpMethod.POST, 
				requestEntity,
				ResponseSafe2Pay.class
			);
						
			ResponseSafe2Pay responseSafe2Pay = requestResponse.getBody();
			
			if(responseSafe2Pay.HasError == true) {
				
				responseFatura.setTemErro(true);
				
				if(responseSafe2Pay.ResponseDetail != null) {
					responseFatura.setConteudo(responseSafe2Pay.ResponseDetail);
				}else if(responseSafe2Pay.Error != null || responseSafe2Pay.Error.isEmpty()){
					responseFatura.setMensagem(responseSafe2Pay.Error);
				}
				
			}else {
				
				responseFatura.setId(responseSafe2Pay.ResponseDetail.get("SingleSaleHash").toString());
				responseFatura.setUrl(responseSafe2Pay.ResponseDetail.get("SingleSaleUrl").toString());
				
			}
			
		} catch (Exception e) {
			
			logger.error(e.getMessage());
			
			responseFatura.setTemErro(true);
			responseFatura.setMensagem("Ocorreu algo inesperado.");
			
		}

		return responseFatura;

	}
	
	private SingleSale gerarPayload(TicketFatura ticketFatura, Ticket ticket, Usuario tutor) {
				
		SingleSale payload = new SingleSale();
		
		Customer customer = new Customer();
		Address address = new Address();
		Product product = new Product();
		PaymentMethod paymentMethod = new PaymentMethod();
		ArrayList<Product> products = new ArrayList<Product>();
		ArrayList<PaymentMethod> paymentMethods = new ArrayList<PaymentMethod>();
		ArrayList<String> messages = new ArrayList<String>();
		ArrayList<String> emails = new ArrayList<String>();
		ArrayList<Split> splits = new ArrayList<Split>();
		String dueDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
		
		//String email = tutor.getEmail();
		String email = "lfscovelo@gmail.com";
		
		address.setStreet(tutor.getRua());
		address.setNumber(tutor.getNumero());
		address.setDistrict(tutor.getEstado().getNome());
		address.setZipCode(tutor.getCep());
		address.setComplement("");
		address.setCityName(tutor.getCidade().getNome());
		address.setStateInitials(tutor.getEstado().getSigla());
		address.setCountryName("Brasil");
		
		customer.setIdentity(ticketFatura.getCpfPagador());
		customer.setName(tutor.getNome());
		customer.setEmail(email);
		customer.setPhone(tutor.getTelefone());
		customer.setAddress(address);
		
		product.setDescription("Aquisição de ticket #" + ticket.getId() + " - 99Dogs");
		product.setQuantity(ticket.getQuantidade());
		product.setUnitPrice(ticket.getUnitario());
		products.add(product);
		
		paymentMethod.setCodePaymentMethod(ticket.getFormaDePagamento().getTipo());
		paymentMethods.add(paymentMethod);
		
		emails.add(email);
		
		payload.setCustomer(customer);
		payload.setProducts(products);
		payload.setPaymentMethods(paymentMethods);
		payload.setDueDate(dueDate);
		payload.setInstruction("Instruções para pagamento");
		payload.setMessages(messages);
		payload.setReference(ticket.getId().toString());
		payload.setPenaltyAmount(0);
		payload.setInterestAmount(0);
		payload.setEmails(emails);
		payload.setSplits(splits);
		payload.setCallbackUrl(this.CALLBACK_URL);
		
		return payload;
		
	}

	@Override
	public boolean cancelarFatura(Ticket ticket) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("X-API-KEY", this.API_TOKEN);		
		
		try {
			
			if(ticket == null || ticket.getId() == null) {
				throw new Exception("Ticket não encontrado.");
			}
			
			String endpoint = "https://api.safe2pay.com.br/v2/SingleSale/Delete?singleSaleHash=";
			endpoint += ticket.getFaturaId();
			
			HttpEntity<String> requestEntity = new HttpEntity<>(headers);
			
			ResponseEntity<Void> requestResponse = restTemplate.exchange(
				endpoint, 
				HttpMethod.DELETE, 
				requestEntity,
				Void.class
			);
			
			if(requestResponse.getStatusCodeValue() == 200) {
				return true;
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return false;
	}
	
}
