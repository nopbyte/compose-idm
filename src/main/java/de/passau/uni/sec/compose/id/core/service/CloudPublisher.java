
package de.passau.uni.sec.compose.id.core.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.csb.client.CSBException;
import com.ibm.csb.client.CSBFactory;
import com.ibm.csb.client.Message;
import com.ibm.csb.client.PubSubEvent;
import com.ibm.csb.client.PubSubEventListener;
import com.ibm.csb.client.Session;
import com.ibm.csb.client.Topic;
import com.ibm.csb.client.TopicPublisher;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;

@Service
public class  CloudPublisher {
	
	public static String IDMUPDATES = "IDMUPDATES";
	private String topicName;
	private CSBFactory factory;
	private TopicPublisher pub;
	private ObjectMapper objectMapper ;
	private boolean connected = false;
	

	@Autowired
	private AnyEntityById entityById;
	
	private static Logger LOG = LoggerFactory.getLogger(CloudPublisher.class);
	
	public void init(String topicn)
	{
		this.topicName = topicn;
		objectMapper = new ObjectMapper();

		for(int i = 0; i<10 && !connected; i++)
		{
			try
			{
					factory = CSBFactory.getInstance();
					Session session = factory.createSession();
					connected = true;
					Topic topic = session.createTopic(topicName, null);
					pub = session.createTopicPublisher(topic, new PubSubEventListener() {
					@Override
					public void onEvent(PubSubEvent event) {
					}
				}, null);
			 } catch (CSBException e)
			{
				LOG.error("pub sub unable to intialize ");
				e.printStackTrace();
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e1)
				{
					
				}
			}
		}
		
	}
	
	public boolean updateEntity(String id, Collection<IPrincipal> collection) throws IdManagementException
	{
		//Map<String,Object> res = entityById.getAnyEntity(id, collection);
		sendMessage(id);
		return true;
	}
	private boolean sendMessage(String message)
	{
		StringWriter sr = new StringWriter();		
		try
		{
			objectMapper.writeValue(sr, message);
			Message msg = factory.createMessage();
			msg.setBuffer(sr.toString().replaceAll("\"", "").getBytes());
			pub.publish(msg);
			return true;
		} catch (CSBException e)
		{
			LOG.error("pub sub unable to send a message. Topic: "+topicName+" value "+sr.toString());
		} catch (JsonGenerationException e1)
		{
			LOG.error("Unable to convert to string Map object representation of entity"+e1.getLocalizedMessage());
		} catch (JsonMappingException e1)
		{
			LOG.error("Unable to convert to string Map object representation of entity"+e1.getLocalizedMessage());		
		} catch (IOException e1)
		{
				LOG.error("Unable to convert to string Map object representation of entity"+e1.getLocalizedMessage());	
		}

		return false;
	}
	private boolean sendMessage(Map<String,Object> message)
	{
		if(!connected)
			LOG.error("it seems pub sub is not connected for topic "+this.topicName+" ?");
		StringWriter sr = new StringWriter();		
		try
		{
			objectMapper.writeValue(sr, message);
			Message msg = factory.createMessage();
			msg.setBuffer(sr.toString().getBytes());
			pub.publish(msg);
			return true;
		} catch (CSBException e)
		{
			LOG.error("pub sub unable to send a message. Topic: "+topicName+" value "+sr.toString());
		} catch (JsonGenerationException e1)
		{
			LOG.error("Unable to convert to string Map object representation of entity"+e1.getLocalizedMessage());
		} catch (JsonMappingException e1)
		{
			LOG.error("Unable to convert to string Map object representation of entity"+e1.getLocalizedMessage());		} catch (IOException e1)
		{
				LOG.error("Unable to convert to string Map object representation of entity"+e1.getLocalizedMessage());	}

		return false;
	}
	

}
