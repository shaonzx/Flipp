package com.hiit.flipp;

public class Countries {
	
	private String countryName;
	private String countryId;
	
	public Countries(String aCountryName, String aCountryId)
	{
		countryName = aCountryName;
		countryId = aCountryId;		
	}
	
	public String CountryName()
	{
		return countryName;
	}
	
	public String CountryId()
	{
		return countryId;
	}

}
