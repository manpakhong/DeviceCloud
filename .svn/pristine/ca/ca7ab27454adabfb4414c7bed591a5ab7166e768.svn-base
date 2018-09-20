package com.littlecloud.ac;

import org.jboss.logging.Logger;

public class MsgRequeue implements Runnable {

	private static final Logger log = Logger.getLogger(MsgRequeue.class);
	private static final int SLEEP_INTERVAL_UNHEALTHY = 30000; // 30 sec
	private static final int SLEEP_INTERVAL_EMPTY = 30000; // 30 sec
	private static final int REPROCESS_INTERVAL = 100; // 100 ms
	private static final int REPROCESS_MARGIN = 100; // 100 below MAX
	private static final WtpMsgHandlerPool wtppool = WtpMsgHandlerPool.getInstance();
	
	public MsgRequeue()
	{
	}

	@Override
	public void run()
	{
		while (!Thread.currentThread().isInterrupted())
		{
			if (WtpMsgHandler.UNHEALTHY_SKIP_MESSAGE)
			{
				try {
					Thread.sleep(SLEEP_INTERVAL_UNHEALTHY);
				} catch (InterruptedException e) {
					log.error("InterruptedException ", e);
				}
			} else {
				while (!WtpMsgHandler.UNHEALTHY_SKIP_MESSAGE && WtpMsgHandler.MAX_CONCURRENT_MESSAGE - WtpMsgHandler.getCounter() > REPROCESS_MARGIN ) {
					String json = WtpMsgHandler.jsonQueue.poll();
					if(json == null)	{// no more json in queue
						try {
							Thread.sleep(SLEEP_INTERVAL_EMPTY);
						} catch (InterruptedException e) {
							log.error("InterruptedException ", e);
						}
						break;
					}
					wtppool.execute(new WtpMsgHandler(json));	// re-process the json
					try {
						Thread.sleep(REPROCESS_INTERVAL);
					} catch (InterruptedException e) {
						log.error("InterruptedException ", e);
					}
				}
			}
		}
	}
}