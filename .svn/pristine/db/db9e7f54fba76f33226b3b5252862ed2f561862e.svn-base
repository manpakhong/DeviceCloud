package com.littlecloud.control.webservices.util;

import java.util.Comparator;

import com.littlecloud.control.json.model.Json_SSID_Usage;

public class ComparatorSsidUsageUtils implements Comparator<Object>
{
	private String sort = "usage";
	
	public ComparatorSsidUsageUtils( String sort )
	{
		this.sort = sort;
	}

	@Override
	public int compare(Object o1, Object o2) 
	{
		// TODO Auto-generated method stub
		int flag = 0;
		
		Json_SSID_Usage ss1 = (Json_SSID_Usage)o1;
		Json_SSID_Usage ss2 = (Json_SSID_Usage)o2;
		
		if( sort.equals("usage") )
		{	
			flag = ss2.getUsage().compareTo(ss1.getUsage());
		}
		else if( sort.equals("client") )
		{
			flag = ss2.getClients_count().compareTo(ss1.getClients_count());
		}
		
		return flag;
	}
	
	
	
}
