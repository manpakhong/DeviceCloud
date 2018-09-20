package com.littlecloud.control.webservices.util;

import com.littlecloud.control.entity.branch.Products;
import com.littlecloud.pool.object.utils.ProductUtils;

public class ProductWsUtils 
{
	public static boolean isWifiSupport(Integer productId)
	{
		boolean result = false;
		Products prod = ProductUtils.getProducts(productId);
		if( prod != null )
		{
			if( prod.getRadio1_24G() || prod.getRadio1_5G() || prod.getRadio2_24G() || prod.getRadio2_5G() )
				result = true;
		}
		return result;
	}
}
