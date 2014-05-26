package de.passau.uni.sec.compose.id.common.exception;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;




public class IdManagementException extends Exception
{
	
	public static enum Level {DEBUG, INFO, WARN, ERROR, FATAL;}
	/**
	 * 
	 */
	private static final long serialVersionUID = -1952931378846719578L;
	/*
	 * Message dedicated for logging
	 */
	private String loggingMessage;
	/**
	 * This attribute is true if the exception is created by the Identity Management itself. If this
	 * attribute is false it is very likely that it comes from Cloud Foundry's UAA API.
	 */
	private boolean internal;
	
	private Map<String,Object> externalError;
	
	/**
	 * Error code for HTTP, can be 0 if none
	 */
	private int HTTPErrorCode;
	
	/**
	 * 
	 * @param cause
	 * @return Stracktrace for the Trhowable object
	 */
	public static String getStackTrace(Throwable cause)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		cause.printStackTrace(pw);
		return sw.toString();
	}
	/**
	 * This constructor should be used to generate Internal Exceptions (i.e. created by Id Management classes)
	 * @param message Message to be displayed to the final user
	 * @param cause cause of the exception (if any; can be null)
	 * @param classLogger Logger coming from the original class generating the exception
	 * @param loggingMessage Message for the log files
	 * @param level level for the log message
	 */
	public IdManagementException(String message, Throwable cause, Logger classLogger, String loggingMessage, Level level, int HTTPErrorCode )
	{
		super(message, cause);
		this.internal = true;
		this.HTTPErrorCode = HTTPErrorCode;
		this.loggingMessage = loggingMessage;
		if (cause != null)
		{
			String trace = getStackTrace(cause);
			//TODO extract this as a configuration property in the future, to make it DEBUG or ERROR.
			this.log(classLogger,Level.ERROR, trace);
		}
		this.log(classLogger,level, loggingMessage);
	}
	
	public String getLoggingMessage() {
		return loggingMessage;
	}
	public void setLoggingMessage(String loggingMessage) {
		this.loggingMessage = loggingMessage;
	}
	/**
	 * * This constructor should be used to generate Exceptions which come from errors received from UAA API(i.e. external Exceptions)
	 * @param message Message to be displayed to the final user
	 * @param classLogger Logger coming from the original class generating the exception
	 * @param JSonException JSon representation of the error comming from the UAA
	 * @param level level for the log message in the internal logs
	 */
	public IdManagementException(String message, Logger classLogger, Map<String,Object> externalError, Level level, int HTTPErrorCode)
	{
		super(message);
		this.internal = false;
		this.externalError = externalError;
		this.HTTPErrorCode = HTTPErrorCode;
		this.log(classLogger,level, message);
	}
	/**
	 * 
	 * @param classLogger
	 * @param level
	 * @param trace
	 */
	private void log(Logger classLogger,Level level, String trace) 
	{
		switch (level){
		case INFO:
			classLogger.info(trace);
			break;
		case DEBUG:
			classLogger.debug(trace);
			break;
		 case WARN:
			 classLogger.warn(trace);
			 break;
		 case ERROR:
			 classLogger.error(trace);
			 break;
	     default:
	    	 classLogger.error(trace);
		 	
		}
	}
	/**
	 * Useful to return errors to the user
	 * @return error message for the user.
	 */
	public Map<String, Object> getErrorAsMap()
	{
		if(internal)
		{
		  Map<String, Object> map = new HashMap<String,Object>();
		  map.put("error", super.getMessage());
		  return map;
		}
		return externalError;
		
	}
	public int getHTTPErrorCode() {
		return HTTPErrorCode;
	}
	public void setHTTPErrorCode(int hTTPErrorCode) {
		HTTPErrorCode = hTTPErrorCode;
	}
	
	
}
