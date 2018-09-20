package com.littlecloud.ac;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;

public class FifoWriter implements Callable<String> {

	private static boolean PROD_MODE = DebugManager.isPROD_MODE();
	private static final Logger logger = Logger.getLogger(FifoWriter.class);
	private static final String OUTPUT_ENCODING="UTF-8";

	private final String fifo;
	private final String data;
	
	private static AtomicInteger counter = new AtomicInteger(0);
	
	public static void setCounter(boolean bAdd) {
		if (bAdd)
		{
			counter.incrementAndGet();
		}
		else
		{
			counter.decrementAndGet();
		}
	}

	public FifoWriter(String fifo, String data) {
		super();
		this.fifo = fifo;
		this.data = data;
	}

	public String call() {
		
		if (!PROD_MODE)
			return data;
		
		setCounter(true);
		logger.info("FifoWriter start counter = " + counter);
		
		final int MAX_RETRY = 3;
		int retryCount = 0;
		
		FileOutputStream fout = null;
		BufferedWriter out = null;

		/* get length of data size */
		boolean bJobDone = false;
		while (!bJobDone) {
			try {
				bJobDone = true;
				fout = new FileOutputStream(new File(fifo));
				out = new BufferedWriter(new OutputStreamWriter(fout, OUTPUT_ENCODING));

				out.write(data+"\r\n");
				out.flush();
				out.close();

				logger.info(">> cmd sent="+data);
			} catch (IOException ex) {
				retryCount++;
				logger.errorf("IO Exception at write (%d)!! %s",retryCount,ex.getMessage());
				
				if (retryCount<=MAX_RETRY)
				{
					bJobDone = false;					
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.warnf("InterruptedException %s",e.getMessage());
				}
							
			} finally {
				try {
					if (fout != null)
						fout.close();					
				} catch (IOException e) {
					logger.error("IO Exception at close!! " + e.getMessage());					
				}
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				logger.error("InterruptedException ",e1);
			}

		}

		setCounter(false);
		logger.infof("FifoWriter end counter = %d (retryCount=%d)", counter, retryCount);
		return data;
	}
}