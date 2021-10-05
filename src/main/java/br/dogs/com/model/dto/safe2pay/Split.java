package br.dogs.com.model.dto.safe2pay;

public class Split {

	private int CodeTaxType;
	private int CodeReceiverType;
	private String Identify;
	private String Name;
	private boolean IsPayTax;
	private double Amount;

	public int getCodeTaxType() {
		return CodeTaxType;
	}

	public void setCodeTaxType(int codeTaxType) {
		CodeTaxType = codeTaxType;
	}

	public int getCodeReceiverType() {
		return CodeReceiverType;
	}

	public void setCodeReceiverType(int codeReceiverType) {
		CodeReceiverType = codeReceiverType;
	}

	public String getIdentify() {
		return Identify;
	}

	public void setIdentify(String identify) {
		Identify = identify;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public boolean isIsPayTax() {
		return IsPayTax;
	}

	public void setIsPayTax(boolean isPayTax) {
		IsPayTax = isPayTax;
	}

	public double getAmount() {
		return Amount;
	}

	public void setAmount(double amount) {
		Amount = amount;
	}

}
