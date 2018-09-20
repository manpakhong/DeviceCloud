package com.littlecloud.ac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.jboss.logging.Logger;

import com.littlecloud.ac.root.WtpMsgHandlerRoot;
import com.littlecloud.ac.util.RootBranchRedirectManager;

public class FifoReader implements Runnable {

	private static final Logger log = Logger.getLogger(FifoReader.class);
	private static final String INPUT_ENCODING="UTF-8";
	private static final WtpMsgHandlerPool wtppool = WtpMsgHandlerPool.getInstance();
	private static final boolean isRootServerMode = RootBranchRedirectManager.isRootServerMode();
	
	private InputStream is = null;
	private InputStreamReader isr = null;
	private BufferedReader br = null;
	private String fifo = null;
	private int sleepPeriod = 1; // default is 1ms 
	
	public FifoReader(String path) throws FileNotFoundException
	{
		fifo = path;
	}

	public FifoReader(String path, int period) throws FileNotFoundException
	{
		fifo = path;
		sleepPeriod = period;
	}

	public void openFifo() throws FileNotFoundException {
	
		if (is == null)
		{
			// open input stream test.txt for reading purpose.
			is = new FileInputStream(fifo);

			// create new input stream reader
			try {
				isr = new InputStreamReader(is, INPUT_ENCODING);
			} catch (UnsupportedEncodingException e) {
				log.error("UnsupportedEncodingException", e);
			}

			// create new buffered reader
			br = new BufferedReader(isr);
		}
		
	}

	public void forwardMsg(String json)
	{

		/* Wtp msg handler pool */  
//		ExecutorService executor = Executors.newSingleThreadExecutor();
//		Future<?> future = executor.submit(new WtpMsgHandler(json));
//		log.infof("WtpMsgHandler counter ExecutorService Active : " + ((java.util.concurrent.ThreadPoolExecutor) executor).getActiveCount());
//		try {
//			future.get(15, TimeUnit.SECONDS);
//		} catch (InterruptedException | ExecutionException e) {
//			log.error(e.getMessage(), e);
//		} catch (TimeoutException e1){
//			log.error("WtpMsgHandler timeout for message (" + json + ")", e1);			
//		}
		
		//new Thread(new WtpMsgHandler(json)).start();
		
		if (isRootServerMode)
			wtppool.execute(new WtpMsgHandlerRoot(json));
		else
			wtppool.execute(new WtpMsgHandler(json));		
	}
	
	/*public void printStr(String json)
	{
		QueryInfo<Object> info = null;
		Gson gson = new Gson();
		try {			
			info = gson.fromJson(json, QueryInfo.class);
			log.info("info=" + info);
		} catch (Exception e) {
			log.errorf("Json from WTP format exception (json=%s)", json, e);
			throw e;
		}
		
		log.info("result=" + json);
	}*/

	@Override
	public void run()
	{
		//for (;;)
		log.debugf("FifoReader: %s ENTER", fifo);
		try {
			openFifo();
		} catch(Exception e) {
			log.fatal("FifoReader exception "+e, e);
			return;
		}
		log.warnf("FifoReader: %s OPENED", fifo);

		while (!Thread.currentThread().isInterrupted())
		{
			if (read()==0)
			{
				try {
					if(log.isDebugEnabled() && sleepPeriod>1)
						log.debug("Sleep for " + sleepPeriod);
					Thread.sleep(sleepPeriod);
				} catch (InterruptedException e) {
					log.error("InterruptedException ", e);
				}
			}
		}
	}

	public synchronized int read()
	{
		//log.info("read: enter");
		
		int readlen = 0;

		try {
			readlen = is.available();
//			log.info("readlen="+readlen);
			//if (readlen !=0 && br.ready())
			if (readlen !=0)
			{
				//log.info("reading");
				forwardMsg(br.readLine());
			}
//			else
//			{
				/* dont sleep while synchronize */ 
//				Thread.sleep(1);
//			}
		} catch (Exception e) {
			log.error("fifo exception ", e);

			try {
				// releases resources associated with the streams
				if (is != null)
					is.close();
				if (isr != null)
					isr.close();
				if (br != null)
					br.close();
				
				openFifo();
			} catch (IOException ioe)
			{
				log.error("fifo io error", ioe);
			}
			
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e1) {
//				log.error("fifo - Thread.sleep error", e1);				
//			}
		}

		//log.info("read: return");
		return readlen;
	}
}