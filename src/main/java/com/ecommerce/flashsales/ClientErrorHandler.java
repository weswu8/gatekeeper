package com.ecommerce.flashsales;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class ClientErrorHandler implements ResponseErrorHandler
{
   
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void handleError(ClientHttpResponse response) throws IOException 
	{
       if (response.getStatusCode() == HttpStatus.NOT_FOUND)
       {
			logger.error("The request resource is not found!");
			throw new ResourceNotFoundException();
       }

       // handle other possibilities, then use the catch all... 
       	logger.error("This is error:"+response.getStatusCode());
		throw new UnexpectedHttpException();

	}

   @Override
   public boolean hasError(ClientHttpResponse response) throws IOException 
   {
       if ( (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR)
         || (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) )
       {
           return true;
       }
       return false;
   }

}