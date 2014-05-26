package de.passau.uni.sec.compose.id.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import de.passau.uni.sec.compose.id.rest.messages.ErrorResponseMessage;

@ControllerAdvice
public class GlobalControllerExceptionHandler {
		
	private static Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);
	
    @ExceptionHandler(value = Exception.class)
    public @ResponseBody ResponseEntity<Object> defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it - like the OrderNotFoundException example
        // at the start of this post.
        // AnnotationUtils is a Spring Framework utility class.
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }
       
        HttpHeaders headers = new HttpHeaders();
        
        ErrorResponseMessage res = new ErrorResponseMessage();
        res.setErrorMessage(e.toString());
        //split different error messages accordingly.
        return new ResponseEntity<Object>(res, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
