package com.littlecloud.alert.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.jboss.logging.Logger;

import com.littlecloud.services.AlertMgr;

public class AlertClient {
	private static final Logger log = Logger.getLogger(AlertClient.class);
	public int sendCommand(String addr, int port, String command){
		Socket sock = null;
		BufferedWriter bw = null;
		OutputStreamWriter osw = null;
		try {
			sock = new Socket(addr, port);
			osw = new OutputStreamWriter(sock.getOutputStream());
			bw = new BufferedWriter(osw);
			bw.append(command);
			bw.flush();
		} catch (Exception e){
			if (command.equals("start -a")){
				log.info("AlertClient.sendCommand() - Connection will retry in a minutes!");
			}
			else {
				log.error("AlertClient.sendCommand()", e);
			}
			return 0;
		} finally {
			try {
				if (bw != null){
					bw.close();
				}
				if (osw != null){
					osw.close();
				}
				if (sock != null){
					sock.close();
				}
			} catch (IOException e) {
				log.error("AlertClient.sendCommand() - Connection close failure!", e);
			}
		}

		return 1;
	}
}
