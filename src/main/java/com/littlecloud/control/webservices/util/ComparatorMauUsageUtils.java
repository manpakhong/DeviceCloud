package com.littlecloud.control.webservices.util;

import java.util.Comparator;

import com.littlecloud.control.json.model.Json_Manufacturer_Usage;

public class ComparatorMauUsageUtils implements Comparator<Object>
{
	private String sort = "usage";
	
	public ComparatorMauUsageUtils(String sort)
	{
		this.sort = sort;
	}
	
	@Override
	public int compare(Object o1, Object o2) 
	{
		// TODO Auto-generated method stub
		int flag = 0;
		
		Json_Manufacturer_Usage mu1 = (Json_Manufacturer_Usage)o1;
		Json_Manufacturer_Usage mu2 = (Json_Manufacturer_Usage)o2;
		
		if( sort.equals("usage") )
		{	
			flag = mu2.getUsage().compareTo(mu1.getUsage());
		}
		else if( sort.equals("client") )
		{
			flag = mu2.getClients_count().compareTo(mu1.getClients_count());
		}
		
		return flag;
	}

}
