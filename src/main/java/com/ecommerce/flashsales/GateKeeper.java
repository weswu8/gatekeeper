package com.ecommerce.flashsales;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/***
 * 
 * @author wuwesley
 * The inventory management service for the whole system.
 */
@RestController
@RequestMapping("/")
public class  GateKeeper {
	/*** indicate current version of this micro service ***/
	public final String cVersion = "1.0";
	
	@Autowired
	Environment environment;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${safeprotector.url}")
    private String safeprotectorBaseUrl;
    @Value("${policycontroller.url}")
    private String policycontrollerBaseUrl;
    @Value("${inventorymanager.url}")
    private String inventorymanagerBaseUrl;
    @Value("${shoppingcart.url}")
    private String shoppingcartBaseUrl;
	private FlashSalesAccessLogger fsAccessLogger = new FlashSalesAccessLogger();
	/*** rate limiter setting ***/
    @Value("${ratelimiter.consumeCount}")
	public double consumeCount;
    public int totalQuantity = 0;

    /***
     * customize the HTTP connection configuration.
     * @return
     */
    @Autowired
    public ClientHttpRequestFactory getClientHttpRequestFactory() {
        /*** set the long time out period ***/
    	int timeout = 60000;
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }
    /***
     * define the response class
     * @param msgKey
     * @param result
     * @return
     */
    public ClientResponse setClientResponse(String msgKey, Boolean result, Boolean isThrotted){
		ClientResponse clientResponse = new ClientResponse();
		clientResponse.setReponseMsg(environment.getProperty(msgKey));
		clientResponse.setRepResult(result);
		clientResponse.setIsThrottled(isThrotted);
		clientResponse.setVersion(cVersion);
		return clientResponse;
    }
    /***
     * do the safe protector validation.
     * @param restTemplate
     * @param clientRequest
     * @return {false,true},0-index: Exception, 1-index:validation result, 2-index:is throttled
     */
    public ArrayList<Boolean> doSafeValidation(RestTemplate restTemplate, ClientRequest clientRequest){
    	ArrayList<Boolean> rList = new ArrayList<Boolean>();
    	rList.add(0, false); /***Exception***/
    	rList.add(1, false); /***validation result***/
    	rList.add(2, false); /***isThrottled***/
    	SafeValidationR safeValidationR = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("sid", clientRequest.getSessionID());
			params.put("clientip", clientRequest.getClientIP());
			params.put("userid", clientRequest.getUserID());
			safeValidationR = restTemplate.getForObject(safeprotectorBaseUrl+"validate/sid/{sid}/userid/{userid}/clientip/{clientip}",SafeValidationR.class,params);
		} catch (ResourceNotFoundException nEx) {
        	rList.set(0, true);
        	return rList;
		} catch (UnexpectedHttpException uEx){
			rList.set(0, true);
			return rList;
		} catch (ResourceAccessException rEx){
			rList.set(0, true);
			return rList;
		}
		if (safeValidationR.getIsThrottled() == true){
			rList.set(1, false);
			rList.set(2, true);
        }else{
        	if (safeValidationR.getIsAllowed() == true){
        		rList.set(1, true);
    			rList.set(2, false);
        	}
        }
		return rList;
    }
    /***
     * do the business policy validation
     * @param restTemplate
     * @param clientRequest
     * @return {false,true},0-index: Exception, 1-index:validation result, 2-index:is throttled
     */
    public ArrayList<Boolean> doPolicyValidation(RestTemplate restTemplate, ClientRequest clientRequest){
    	ArrayList<Boolean> rList = new ArrayList<Boolean>();
    	rList.add(0, false); /***Exception***/
    	rList.add(1, false); /***validation result***/
    	rList.add(2, false); /***isThrottled***/
    	PolicyValidationR policyValidationR = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("sid", clientRequest.getSessionID());
			params.put("userid", clientRequest.getUserID());
			params.put("userlv", String.valueOf(clientRequest.getUserLevel()));
			params.put("sku", clientRequest.getGoodsSKU());
			params.put("quantity", String.valueOf(clientRequest.getGoodsQuantity()));
			policyValidationR = restTemplate.getForObject(policycontrollerBaseUrl+"/validate/sid/{sid}/userid/{userid}/userlv/{userlv}/sku/{sku}/quantity/{quantity}",PolicyValidationR.class,params);
		} catch (ResourceNotFoundException nEx) {
        	rList.set(0, true);
        	return rList;
		} catch (UnexpectedHttpException uEx){
			rList.set(0, true);
			return rList;
		} catch (ResourceAccessException rEx){
			rList.set(0, true);
			return rList;
		}
		if (policyValidationR.getIsThrottled() == true){
			rList.set(1, false);
			rList.set(2, true);
        }else{
        	if (policyValidationR.getIsAllowed() == true){
        		rList.set(1, true);
    			rList.set(2, false);
        	}
        }
		return rList;
    }
    /***
     * do the inventory validation
     * @param restTemplate
     * @param clientRequest
     * @return {false,true},0-index: Exception, 1-index:validation result, 2-index:is throttled
     */
    public ArrayList<Boolean> doInventoryValidation(RestTemplate restTemplate, ClientRequest clientRequest){
    	ArrayList<Boolean> rList = new ArrayList<Boolean>();
    	rList.add(0, false); /***Exception***/
    	rList.add(1, false); /***validation result***/
    	rList.add(2, false); /***isThrottled***/
    	InventoryValidationR inventoryValidationR = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("sid", clientRequest.getSessionID());
			params.put("sku", clientRequest.getGoodsSKU());
			params.put("quantity", String.valueOf(clientRequest.getGoodsQuantity()));
			inventoryValidationR = restTemplate.getForObject(inventorymanagerBaseUrl+"/validate/sid/{sid}/sku/{sku}/quantity/{quantity}",InventoryValidationR.class,params);
		} catch (ResourceNotFoundException nEx) {
        	rList.set(0, true);
        	return rList;
		} catch (UnexpectedHttpException uEx){
			rList.set(0, true);
			return rList;
		} catch (ResourceAccessException rEx){
			rList.set(0, true);
			return rList;
		}
		/*** the request is throttled ***/
		if (inventoryValidationR.getIsThrottled() == true){
			rList.set(1, false);
			rList.set(2, true);
        }else{ /*** the request is allowed ***/
        	if (inventoryValidationR.getIsAllowed() == true){
        		totalQuantity = inventoryValidationR.getTotalQuantity();
        		rList.set(1, true);
    			rList.set(2, false);
        	}
        }
		return rList;
    }
    /***
     * 
     * @param restTemplate
     * @param clientRequest
     * @return ArrayList, 0: Exception, 1: validation result
     */
    @SuppressWarnings("unchecked")
	public ArrayList<Boolean> doAddGoodToCart(RestTemplate restTemplate, ClientRequest clientRequest){
    	AddGoodsR addGoodsR;
    	ArrayList<Boolean> rList = new ArrayList<Boolean>();
    	rList.add(0, false); /***Exception***/
    	rList.add(1, false); /***validation result***/
    	rList.add(2, false); /***isThrottled***/
    	// create request body
        JSONObject mJSON = new JSONObject();
        mJSON.put("sessionID", clientRequest.getSessionID());
        mJSON.put("userID", clientRequest.getUserID());
        mJSON.put("goodsSKU", clientRequest.getGoodsSKU());
        mJSON.put("goodsQuantity", clientRequest.getGoodsQuantity());
        mJSON.put("totalQuantity", totalQuantity);

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(mJSON.toString(), headers);
        // send request and parse result
        try {
        	addGoodsR = restTemplate.postForObject(shoppingcartBaseUrl+"/add", entity, AddGoodsR.class);            
        } catch (ResourceNotFoundException nEx) {
        	rList.set(0, true);
        	return rList;
		} catch (UnexpectedHttpException uEx){
			rList.set(0, true);
			return rList;
		} catch (ResourceAccessException rEx){
			rList.set(0, true);
			return rList;
		}
        /*** add goods to cart successfully ***/
        if (addGoodsR.getIsThrottled() == true){
			rList.set(1, false);
			rList.set(2, true);
        }else{
        	if (addGoodsR.getIsAllowed() == true){
        		rList.set(1, true);
    			rList.set(2, false);
        	}
        }
		return rList;
    }
    /***
	 * add the selected goods to the shopping cart.
	 * Request url : http://localhost:8080/buy
	 * Request sample : {"sessionID":"SID00000001","clientIP":"202.100.100.1","userID":"FS00000002","userLevel":5,"goodsSKU":"QT3456","goodsQuantity":1}
	 * Response sample : {"reponseMsg":"xxxxxx","repResult":true}
     * @throws JsonProcessingException 
     * @throws ParseException 
     * @throws NoSuchAlgorithmException 
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/buy", headers = "Accept=application/json")
    public ClientResponse doBuyTheGoods(HttpServletRequest httpRequest, HttpServletResponse httpResponse, @RequestBody ClientRequest clientRequest) throws JsonProcessingException {
    	ArrayList<Boolean> resultList = new ArrayList<Boolean>();
    	ClientResponse clientResponse = new ClientResponse();
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
        restTemplate.setErrorHandler(new ClientErrorHandler());
        /*** log preparation ***/
        long sidStartTime = System.currentTimeMillis();
        ObjectMapper mapper = new ObjectMapper();

        /*** initialize the global tracing ID across the multiple micro services ***/
        String uniqueID = UUID.randomUUID().toString();
        clientRequest.setSessionID(uniqueID);
        
        /*** log the client requesting ***/
        fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.CLIENTREQUESTING.msgBody(), mapper.writeValueAsString(clientRequest), 0, clientResponse);
		
        /*** step 1. do throttling ***/
        long stepStartTime = System.currentTimeMillis();
        long stepEndTime = 0;
		if (GateKeeperApplication.rateLimiter.consume(consumeCount) == false){
			clientResponse.setIsThrottled(true);
			stepEndTime = System.currentTimeMillis();
			fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.THROTTING.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
			return clientResponse;
		}
		clientResponse = setClientResponse("shoppingcart.nothrotting.msg", true, false);
		stepEndTime = System.currentTimeMillis();
		fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.THROTTING.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
        
		/*** step 2. validate the bad ip and bad account ***/
        stepStartTime = System.currentTimeMillis();
        resultList = doSafeValidation(restTemplate, clientRequest);
        if (resultList.get(0) == true){
        	stepEndTime = System.currentTimeMillis();
            fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.SAFEPROTECTOR.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
        	clientResponse = setClientResponse("safeprotector.exception.msg", false, false);
        	return clientResponse;
        } else if (resultList.get(2) == true) {
        	stepEndTime = System.currentTimeMillis();
            fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.SAFEPROTECTOR.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
        	clientResponse = setClientResponse("shoppingcart.dothrotting.msg", false, true);
        	return clientResponse;
        } else if (resultList.get(1) == false){
        	stepEndTime = System.currentTimeMillis();
            fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.SAFEPROTECTOR.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
        	clientResponse = setClientResponse("safeprotector.failed.msg", false, false);
        	return clientResponse;
        } 
		clientResponse = setClientResponse("safeprotector.successed.msg", true, false);
        stepEndTime = System.currentTimeMillis();
        fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.SAFEPROTECTOR.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);

        /*** step 3. check the business policy ***/
        stepStartTime = System.currentTimeMillis();
        resultList = doPolicyValidation(restTemplate, clientRequest);
        if (resultList.get(0) == true){
        	stepEndTime = System.currentTimeMillis();
            fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.POLICYCONTROLLER.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
            clientResponse = setClientResponse("policycontroller.exception.msg", false, false);
        	return clientResponse;
        } else if (resultList.get(2) == true) {
        	stepEndTime = System.currentTimeMillis();
            fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.POLICYCONTROLLER.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
        	clientResponse = setClientResponse("shoppingcart.dothrotting.msg", false, true);
        	return clientResponse;
        } else if (resultList.get(1) == false){
        	stepEndTime = System.currentTimeMillis();
            fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.POLICYCONTROLLER.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
        	clientResponse = setClientResponse("policycontroller.failed.msg", false, false);
        	return clientResponse;
        }
		clientResponse = setClientResponse("policycontroller.successed.msg", true, false);
        stepEndTime = System.currentTimeMillis();
        fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.POLICYCONTROLLER.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
        
        /*** step 4. check the inventory info ***/
        stepStartTime = System.currentTimeMillis();
        resultList = doInventoryValidation(restTemplate, clientRequest);
        if (resultList.get(0) == true){
        	stepEndTime = System.currentTimeMillis();
            fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.INVENTORYMANAGER.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
        	clientResponse = setClientResponse("inventorymanager.exception.msg", false, false);
        	return clientResponse;
        } else if (resultList.get(2) == true) {
        	stepEndTime = System.currentTimeMillis();
            fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.INVENTORYMANAGER.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
        	clientResponse = setClientResponse("shoppingcart.dothrotting.msg", false, true);
        	return clientResponse;
        } else if (resultList.get(1) == false){
        	stepEndTime = System.currentTimeMillis();
            fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.INVENTORYMANAGER.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
            clientResponse = setClientResponse("inventorymanager.failed.msg", false, false);
        	return clientResponse;
        }
		clientResponse = setClientResponse("inventorymanager.successed.msg", true, false);
        stepEndTime = System.currentTimeMillis();
        fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.INVENTORYMANAGER.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
          
        /*** step 5. add the goods to the cart ***/
        stepStartTime = System.currentTimeMillis();
        resultList = doAddGoodToCart(restTemplate, clientRequest);
        if (resultList.get(0) == true){
        	stepEndTime = System.currentTimeMillis();
            fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.SHOPPINGCART.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
        	clientResponse = setClientResponse("shoppingcart.exception.msg", false, false);
        	return clientResponse;
        } else if (resultList.get(2) == true) {
        	stepEndTime = System.currentTimeMillis();
            fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.SHOPPINGCART.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
        	clientResponse = setClientResponse("shoppingcart.dothrotting.msg", false, true);
        	return clientResponse;
        } else if (resultList.get(1) == false){
        	stepEndTime = System.currentTimeMillis();
            fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.SHOPPINGCART.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
        	clientResponse = setClientResponse("shoppingcart.failed.msg", false, false);
        	return clientResponse;
        }
        clientResponse = setClientResponse("shoppingcart.successed.msg", true, false);
        stepEndTime = System.currentTimeMillis();
        fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.SHOPPINGCART.msgBody(), mapper.writeValueAsString(clientRequest), stepEndTime - stepStartTime, clientResponse);
               
    	/*** log the all step info ***/
        long sidEndTime = System.currentTimeMillis();
        fsAccessLogger.doAccessLog(httpRequest, httpResponse, clientRequest.getSessionID(), CurrentStep.ALLSTEPS.msgBody(), mapper.writeValueAsString(clientRequest), sidEndTime - sidStartTime, clientResponse);
        
        /*** reminde:update the inventory info when payment is finished ***/
        return clientResponse;
	}
}
