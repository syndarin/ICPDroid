package com.syndarin.icpdroid;


public enum GSMProvider {
	MTS(R.drawable.mts, new String[]{"050","066","095","099"}),
	LIFE(R.drawable.life, new String[]{"063","093"}),
	KIYVSTAR(R.drawable.kiyvstar, new String[]{"067","097"}),
	STD(R.drawable.phone, new String[0]);
	
	private int logo;
	private String[] codes;
	
	private GSMProvider(int logo, String[] codes) {
		this.logo = logo;
		this.codes = codes;
	}
	
	public int getLogo() {
		return logo;
	}

	public String[] getCodes() {
		return codes;
	}

	public static GSMProvider getProviderByCode(String code){
		for(GSMProvider prov : values())
			if(isCodeBelongToProvider(prov, code))
				return prov;
		
		return STD;
	}
	
	private static boolean isCodeBelongToProvider(GSMProvider provider, String code){
		for(String supportedCode : provider.getCodes())
			if(supportedCode.equalsIgnoreCase(code))
				return true;
		
		return false;
	}
}
