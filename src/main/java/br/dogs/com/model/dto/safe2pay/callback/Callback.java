package br.dogs.com.model.dto.safe2pay.callback;

public class Callback {

	public int IdTransaction;
	public TransactionStatus TransactionStatus;
	public Origin Origin;

	public int getIdTransaction() {
		return IdTransaction;
	}

	public void setIdTransaction(int idTransaction) {
		IdTransaction = idTransaction;
	}

	public TransactionStatus getTransactionStatus() {
		return TransactionStatus;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		TransactionStatus = transactionStatus;
	}

	public Origin getOrigin() {
		return Origin;
	}

	public void setOrigin(Origin origin) {
		Origin = origin;
	}

}
