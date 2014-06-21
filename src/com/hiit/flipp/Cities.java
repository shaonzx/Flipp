package com.hiit.flipp;

public class Cities {
	
	private String cityName;
	private String cityId;
	
	public Cities(String aCityName, String aCityId)
	{
		cityName = aCityName;
		cityId = aCityId;		
	}
	
	public String CityName()
	{
		return cityName;
	}
	
	public String CityId()
	{
		return cityId;
	}

}
