package com.littlecloud.control.servlet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.FifoWriter;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.pool.object.ACCommandObject;
import com.littlecloud.pool.utils.PropertyService;

public class MessageTestServlet extends HttpServlet
{
	private static PropertyService<ACService> ps = new PropertyService<ACService>(ACService.class);
	private static ExecutorService executorAcWtpCmd = Executors.newFixedThreadPool(Integer.valueOf(ps.getString("USER_REQ_AC_TO_WTP_FIFO.THREAD_POOL_SIZE")));
	
	public void doGet(HttpServletRequest request,HttpServletResponse response)
	{
		String pass_word = request.getParameter("password");
		if(pass_word == "123")
		{
			String cacheObjectKey = "{\"data\":{\"iana_id\":\"23695\",\"sn\":\"2830-C91E-795A\",\"model\":\"PWMAXBR1\",\"variant\":\"GENERIC\"},\"sid\":\"20140127102028355262014012710202690375120\",\"iana_id\":23695,\"type\":\"PIPE_INFO_TYPE_REG_LIST\",\"status\":200,\"BYPASS_WARRANTY_CHECK\":\"false\",\"sn\":\"2830-E24C-0774\"}";
//			ACCommandObject aco = new ACCommandObject();
//			aco.setJson(cacheObjectKey);
			System.out.println("json:"+cacheObjectKey);
			//Future<String> result = executorAcWtpCmd.submit(new FifoWriter(ps.getString("USER_REQ_AC_TO_WTP_FIFO"), cacheObjectKey));
		}
	}
	
}
