package br.dogs.com.model.dto.safe2pay;

public class ResponseDetail {

	private String SingleSaleHash;
	private String SingleSaleUrl;

	public String getSingleSaleHash() {
		return SingleSaleHash;
	}

	public void setSingleSaleHash(String singleSaleHash) {
		SingleSaleHash = singleSaleHash;
	}

	public String getSingleSaleUrl() {
		return SingleSaleUrl;
	}

	public void setSingleSaleUrl(String singleSaleUrl) {
		SingleSaleUrl = singleSaleUrl;
	}

}
