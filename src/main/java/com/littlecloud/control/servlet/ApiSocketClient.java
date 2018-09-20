package com.littlecloud.control.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ApiSocketClient 
{
	class SocketThread extends Thread
	{
		public void run()
		{

	        
//	        if (args.length != 2) {
//	            System.err.println(
//	                "Usage: java EchoClient <host name> <port number>");
//	            System.exit(1);
//	        }
	// 
//	        String hostName = args[0];
//	        int portNumber = Integer.parseInt(args[1]);
			Socket echoSocket = null;
	        try
	        {
	        	echoSocket = new Socket("127.0.0.1", 8888);
	            PrintWriter out =
	                new PrintWriter(echoSocket.getOutputStream(), true);
	            BufferedReader in =
	                new BufferedReader(
	                    new InputStreamReader(echoSocket.getInputStream()));
	     
	            String json = "{\"timestamp\":1395191909,\"data\":{\"variant\":\"GENERIC\",\"fw_ver\":\"6.1.1 build 1524\",\"lan_list\":[{\"vlan_id\":0,\"vlan_ip\":\"192.168.50.1\",\"netmask\":\"255.255.255.0\"}],\"product_name\":\"Pepwave MAX BR1\",\"model\":\"PWMAXBR1\",\"full_pcode\":\"MAX-BR1\",\"product_code\":\"MAX-BR1\",\"lan_mac\":\"00:1A:DD:C5:47:00\",\"conf_checksum\":\"4922FC7B6492497FA04A532E7968DC52\",\"version\":1},\"sid\":\"\",\"iana_id\":23695,\"fifo_test_id\":94,\"status\":200,\"type\":\"PIPE_INFO_TYPE_DEV_ONLINE\",\"sn\":\"2830-E420-67B8\"}";
	            
//	            while (userInput != null) {
	            for( int i = 0 ; i < 10 ; i++ )
	            {
	            	out.println(json);
	               // System.out.println("echo: " + userInput);
	            }
	            in.close();
	            out.close();
	        } catch (UnknownHostException e) 
	        {
//	            System.err.println("Don't know about host " + hostName);
	            System.exit(1);
	        } catch (IOException e) {
//	            System.err.println("Couldn't get I/O for the connection to " +
//	                hostName);
	            System.exit(1);
	        } 
	        finally
	        {
	        	try {
					echoSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    
		}
	}
	public static void main(String[] args) throws IOException 
	{ 
		ApiSocketClient apiSocketClient = new ApiSocketClient();
		for(int i=0;i<1000;i++)
		{
			SocketThread socket = apiSocketClient.new SocketThread();
			socket.start();
		}
	}
}
