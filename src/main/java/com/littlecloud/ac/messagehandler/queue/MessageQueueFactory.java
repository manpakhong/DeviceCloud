package com.littlecloud.ac.messagehandler.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.jboss.logging.Logger;

import com.littlecloud.ac.messagehandler.RedirectWtpMessageHandler;
import com.littlecloud.ac.messagehandler.queue.executor.CaptivePortalMessageHandleExecutorController;
import com.littlecloud.ac.messagehandler.queue.messages.Message;
import com.littlecloud.pool.utils.PropertyService;

public class MessageQueueFactory {
	private static final Logger log = Logger.getLogger(MessageQueueFactory.class);
	private static BlockingQueue<Message> q;
	private static PropertyService<MessageQueueFactory> ps = new PropertyService<MessageQueueFactory>(MessageQueueFactory.class);
	private static final Integer blockQueueSize = ps.getInteger("BLOCK_QUEUE_SIZE");
	
	public static BlockingQueue<Message> getInstance() {
		try {
			if (q == null) {
				q = new ArrayBlockingQueue<Message>(blockQueueSize);
			}
			return q;
		} catch (Exception e) {
			log.error("MSGHANDLER20140617 - MessageQueueFactory.getInstance() - ", e );
			return null;
		}
	}
}
