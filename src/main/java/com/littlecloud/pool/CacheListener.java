package com.littlecloud.pool;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryInvalidated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryLoaded;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.annotation.DataRehashed;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryInvalidatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryLoadedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.infinispan.notifications.cachelistener.event.DataRehashedEvent;
import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.pool.object.ConfigGetObject;

@Listener(sync=false)
public class CacheListener
{
	private static Logger logger = Logger.getLogger(CacheListener.class);
	private static final int MAX_RAND_SLEEP = 5000;
	private static final String ConfigGetObjectSig = ConfigGetObject.class+"sn_pk";
	
	@CacheEntryCreated
	public void created(CacheEntryCreatedEvent<String, String> event) {
		if( event.isPre() == false )
		{
			// events.add(event);
			if(logger.isDebugEnabled())
				logger.debugf("CacheEntryCreated[%s] EntryEvent=%s (%s,?)", this, event.getType().name(), event.getKey());
			if (event.getKey().startsWith("ACCommandObjectsn_pk"))
			{
				ACService.fetchCommand(event.getKey());
				if(logger.isDebugEnabled())
					logger.info("created: " + Cluster.getUniqueLocalId() + " fetch command " + event.getKey());
			}
		}
	}
	
	@CacheEntryModified
	public void modified(CacheEntryModifiedEvent<String, String> event) {
		if( event.isPre() == false )
		{
			// events.add(event);
			if(logger.isDebugEnabled())
				logger.debugf("CacheEntryModified[%s] EntryEvent=%s (%s)", this, event.getType().name(), event.getKey());
			if (event.getKey().startsWith("ACCommandObjectsn_pk"))
			{
				if (ACService.fetchCommand(event.getKey()))
					if(logger.isDebugEnabled())
						logger.info("modified: " + Cluster.getUniqueLocalId() + " fetch command " + event.getKey());
			}
		}
	}

	@CacheEntryRemoved
	public void removed(CacheEntryRemovedEvent<String, String> event) {
		if( event.isPre() == false )
		{
			// events.add(event);
			if(logger.isDebugEnabled())
				logger.debugf("CacheEntryRemoved[%s] EntryEvent=%s (%s,%s)", this, event.getType().name(), event.getKey(), event.getValue());

//			if (event.getKey().contains(ConfigGetObjectSig))
//			{
////				if (ACService.fetchCommand(event.getKey()))
////					logger.debug(Cluster.getUniqueLocalId() + " fetch command " + event.getKey());
//				if(logger.isDebugEnabled())
//					logger.debugf("removed - ConfigGetObjectSig is found!");
//			}
		}
	}
	
	@CacheEntryLoaded
	public void loaded(CacheEntryLoadedEvent<String, String> event) {
		if( event.isPre() == false )
		{
			// events.add(event);
			if(logger.isDebugEnabled())
				logger.debugf("CacheEntryLoaded[%s] EntryEvent=%s (%s,%s)", this, event.getType().name(), event.getKey(), event.getValue());
		}
	}
	
	@CacheEntryInvalidated
	public void invalidated(CacheEntryInvalidatedEvent<String, String> event) {
		if( event.isPre() == false )
		{
			// events.add(event);
			if(logger.isDebugEnabled())
				logger.debugf("CacheEntryInvalidated[%s] EntryEvent=%s (%s,%s)", this, event.getType().name(), event.getKey(), event.getValue());

//			if (event.getKey().contains(ConfigGetObjectSig))
//			{
////				if (ACService.fetchCommand(event.getKey()))
////					logger.debug(Cluster.getUniqueLocalId() + " fetch command " + event.getKey());
//				if(logger.isDebugEnabled())
//					logger.debugf("invalidated - ConfigGetObjectSig is found!");
//			}
		}
	}
	
	@DataRehashed
	public void rehash(DataRehashedEvent<String, String> event) {		
		
		if( event.isPre() == false )
		{
			// events.add(event);
			if(logger.isDebugEnabled())
				logger.debugf("DataRehashedEvent[%s] event=%s start=%s end=%s", this, event.getType().name(), event.getMembersAtStart(), event.getMembersAtEnd());			
		}
	}
}