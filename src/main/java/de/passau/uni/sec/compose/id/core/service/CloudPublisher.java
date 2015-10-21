
package de.passau.uni.sec.compose.id.core.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.csb.client.CSBException;
import com.ibm.csb.client.CSBFactory;
import com.ibm.csb.client.Message;
import com.ibm.csb.client.MessageListener;
import com.ibm.csb.client.PubSubEvent;
import com.ibm.csb.client.PubSubEventListener;
import com.ibm.csb.client.Session;
import com.ibm.csb.client.Topic;
import com.ibm.csb.client.TopicPublisher;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;

@Service
@PropertySource("classpath:csb.properties")
public class  CloudPublisher {
	
	
	private String idmtopic;
	private CSBFactory factory;
	private TopicPublisher idmPublisher;
	private ObjectMapper objectMapper ;
	private boolean idmConnected = false;
	private int tries=0;
	private String start;

	@Autowired
	private AnyEntityById entityById;
	
	@Autowired
	private Environment env;
	
	private static Logger LOG = LoggerFactory.getLogger(CloudPublisher.class);
	
	@PostConstruct
	private void init()
	{

		objectMapper = new ObjectMapper();
		this.idmtopic = env.getProperty("csb.idm.topic");
		start = env.getProperty("csb.start");
		String triesString = env.getProperty("csb.tries");
		tries = Integer.parseInt(triesString);
		if(idmtopic != null && start != null && start.trim().toUpperCase().equals("TRUE"))
		{
		
				for(int i = 0; i<tries && !idmConnected; i++)
				{
					try
					{
							factory = CSBFactory.getInstance();
							Session session = factory.createSession();
							if(session!=null){
								LOG.error("Session from CSB is null.... exiting the CSB attempt");
								return;
							}
							idmConnected = true;
							Topic topic = session.createTopic(idmtopic, null);
							idmPublisher = session.createTopicPublisher(topic, new PubSubEventListener() {
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
	}
	
	public boolean updateEntity(String id) throws IdManagementException
	{
		//Map<String,Object> res = entityById.getAnyEntity(id, collection);
		try{
			if(idmConnected)
				sendMessage(id);
			return true;
		}catch(Throwable e)
		{
			LOG.error("I thought CSB was connected but it wasn't... :(");
		}
		return false;
	}
	private boolean sendMessage(String message)
	{
		StringWriter sr = new StringWriter();		
		try
		{
			objectMapper.writeValue(sr, message);
			Message msg = factory.createMessage();
			msg.setBuffer(sr.toString().replaceAll("\"", "").getBytes());
			idmPublisher.publish(msg);
			return true;
		} catch (CSBException e)
		{
			LOG.error("pub sub unable to send a message. Topic: "+idmtopic+" value "+sr.toString());
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
		if(!idmConnected)
			LOG.error("it seems pub sub is not connected for topic "+this.idmtopic+" ?");
		StringWriter sr = new StringWriter();		
		try
		{
			objectMapper.writeValue(sr, message);
			Message msg = factory.createMessage();
			msg.setBuffer(sr.toString().getBytes());
			idmPublisher.publish(msg);
			return true;
		} catch (CSBException e)
		{
			LOG.error("pub sub unable to send a message. Topic: "+idmtopic+" value "+sr.toString());
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
