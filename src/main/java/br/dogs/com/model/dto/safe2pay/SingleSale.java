package br.dogs.com.model.dto.safe2pay;

import java.util.ArrayList;

public class SingleSale {

	private Customer Customer;
	private ArrayList<Product> Products;
	private ArrayList<PaymentMethod> PaymentMethods;
	private String DueDate;
	private String Instruction;
	private ArrayList<String> Messages;
	private String Reference;
	private double PenaltyAmount;
	private double InterestAmount;
	private ArrayList<String> Emails;
	private ArrayList<Split> Splits;
	private String CallbackUrl;

	public Customer getCustomer() {
		return Customer;
	}

	public void setCustomer(Customer customer) {
		Customer = customer;
	}

	public ArrayList<Product> getProducts() {
		return Products;
	}

	public void setProducts(ArrayList<Product> products) {
		Products = products;
	}

	public ArrayList<PaymentMethod> getPaymentMethods() {
		return PaymentMethods;
	}

	public void setPaymentMethods(ArrayList<PaymentMethod> paymentMethods) {
		PaymentMethods = paymentMethods;
	}

	public String getDueDate() {
		return DueDate;
	}

	public void setDueDate(String dueDate) {
		DueDate = dueDate;
	}

	public String getInstruction() {
		return Instruction;
	}

	public void setInstruction(String instruction) {
		Instruction = instruction;
	}

	public ArrayList<String> getMessages() {
		return Messages;
	}

	public void setMessages(ArrayList<String> messages) {
		Messages = messages;
	}

	public String getReference() {
		return Reference;
	}

	public void setReference(String reference) {
		Reference = reference;
	}

	public double getPenaltyAmount() {
		return PenaltyAmount;
	}

	public void setPenaltyAmount(double penaltyAmount) {
		PenaltyAmount = penaltyAmount;
	}

	public double getInterestAmount() {
		return InterestAmount;
	}

	public void setInterestAmount(double interestAmount) {
		InterestAmount = interestAmount;
	}

	public ArrayList<String> getEmails() {
		return Emails;
	}

	public void setEmails(ArrayList<String> emails) {
		Emails = emails;
	}

	public ArrayList<Split> getSplits() {
		return Splits;
	}

	public void setSplits(ArrayList<Split> splits) {
		Splits = splits;
	}

	public String getCallbackUrl() {
		return CallbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		CallbackUrl = callbackUrl;
	}

	@Override
	public String toString() {
		return "SingleSale [Customer=" + Customer + ", Products=" + Products + ", PaymentMethods=" + PaymentMethods
				+ ", DueDate=" + DueDate + ", Instruction=" + Instruction + ", Messages=" + Messages + ", Reference="
				+ Reference + ", PenaltyAmount=" + PenaltyAmount + ", InterestAmount=" + InterestAmount + ", Emails="
				+ Emails + ", Splits=" + Splits + ", CallbackUrl=" + CallbackUrl + ", getCustomer()=" + getCustomer()
				+ ", getProducts()=" + getProducts() + ", getPaymentMethods()=" + getPaymentMethods()
				+ ", getDueDate()=" + getDueDate() + ", getInstruction()=" + getInstruction() + ", getMessages()="
				+ getMessages() + ", getReference()=" + getReference() + ", getPenaltyAmount()=" + getPenaltyAmount()
				+ ", getInterestAmount()=" + getInterestAmount() + ", getEmails()=" + getEmails() + ", getSplits()="
				+ getSplits() + ", getCallbackUrl()=" + getCallbackUrl() + "]";
	}

}
