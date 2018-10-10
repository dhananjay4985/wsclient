package com.kpn.enfinity.Kpnfiberapp.pipelet;


import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;

import com.intershop.beehive.core.capi.log.Logger;
import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.beehive.core.capi.pipeline.PipelineInitializationException;

import com.kpn.enfinity.Kpnfiberapp.internal.utils.TechnicalIBServiceHandler;
import com.kpn.enfinity.ackpnwebservice.internal.config.KISConfigFactory;
import com.kpn.enfinity.ackpnwebservice.internal.config.KISConfig;
import com.kpn.enfinity.ackpnwebservice.internal.service.KPNAxisTemplateFactory;
import com.kpn.enfinity.kpn_fiber_core.internal.logger.KPNLogger;

import com.kpn.services.messages.getinstalledbaserequest_v1.ReqCfsInstStat;
import com.kpn.services.messages.getinstalledbaserequest_v1.GetInstalledBaseRequestV1;
import com.kpn.services.messages.getinstalledbaserequest_v1.GetInstalledBaseRequestV1.Cust;
import com.kpn.services.messages.getinstalledbaserequest_v1.GetInstalledBaseRequestV1.Prod;
import com.kpn.services.messages.getinstalledbaserequest_v1.GetInstalledBaseRequestV1.ReqParams;
import com.kpn.services.messages.getinstalledbaseresponse_v1.GetInstalledBaseResponseV1;

import com.kpn.services.services.getinstalledbase_v1.WsPortType;

import org.apache.axis2.AxisFault;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

public class TechnicalInstalledBaseService extends Pipelet{

	/**
	 * Constant used to access pipelet configuration with key 'KISConfigName'
	 */
	public static final String CN_KISCONFIG_NAME = "KISConfigName";
	/**
	 * Member attribute that holds the pipelet configuration value
	 * 'KISConfigName'
	 */
	private String cfg_kISConfigName = "";
	/**
	 * Constant used to access the pipeline dictionary with key 'Response'
	 */	
	public static final String DN_BUNDLEID = "BundleID";

	/**
	 * Constant used to access the pipeline dictionary with key 'GetTechnicalIBRequest'
	 */
	public static final String DN_GET_TECHNICALIBRESPONSE = "GetTechnicalIBResponse";
	
	 /**
     * Constant used to access the pipeline dictionary with key 'ServiceTimeOut'
     */
    public static final String DN_SERVICE_TIME_OUT = "ServiceTimeOut";
	
	/**
	 * Constant used to access the pipeline dictionary with key 'FaultCode'
	 */
	public static final String DN_FAULT_CODE = "FaultCode";


	public int execute(PipelineDictionary dict) throws PipeletExecutionException{
		System.out.println("Inside TechnicalInstalledBaseService");
		// lookup 'BundleId' in pipeline dictionary
		String  bundleID = (String) dict.get(DN_BUNDLEID);
		
		 // lookup 'ServiceTimeOut' in pipeline dictionary
        Integer serviceTimeOut = (Integer)dict.get(DN_SERVICE_TIME_OUT);
        System.out.println("Time Out from backoffice:"+serviceTimeOut);
        int timeOutDuration = 0;
		if(null != serviceTimeOut)
		{
		    timeOutDuration = serviceTimeOut.intValue();
		}
        
		GetInstalledBaseRequestV1 getInstalledBaseRequestV1 =  new  GetInstalledBaseRequestV1();
		Prod prod = new Prod();
		Cust cust = new Cust();
		ReqParams reqParams = new ReqParams();
		getInstalledBaseRequestV1.setOrigReqerId("KPN_CM");

		reqParams.setReqProdInstStat(ReqCfsInstStat.ACTIVE);
		getInstalledBaseRequestV1.setReqParams(reqParams);
		//prod.setProdInstId(bundleID);
		cust.setCustId("Jini00010");
		// prod.setProdInstId("Jini00010");
		//prod.setProdType("BUNDLE");
		getInstalledBaseRequestV1.setCust(cust);
		// getInstalledBaseRequestV1.setProd(prod);
		// System.out.println("Object data :"+getInstalledBaseRequestV1.toString());

		GetInstalledBaseResponseV1 getTechnicalResponse = null;
		try{
			KISConfig object = KISConfigFactory.getInstance().getConfiguration(cfg_kISConfigName);
			if(null==object){
				System.out.println("Error is kisconfig");
			}
			String endPointURL = object.getEndPointURL();
			String userName = object.getUsername();
			String password = object.getPassword();
			int webServiceTimeOut = object.getTimeOutDuration();
						
			getTechnicalResponse = getTechnicalIBResponse(getInstalledBaseRequestV1,endPointURL,webServiceTimeOut ,userName,password);
			if(null != getTechnicalResponse){
				
			}else{
				//Logger.error(this, "Error in wbservice [ TechnicalInstalledBase ] ");
				dict.put(DN_FAULT_CODE, "error");
				return PIPELET_ERROR;
			}
		}
		catch (SOAPException e) {
			Logger.error(this, "Error in wbservice [ TechnicalInstalledBase ] ", e);
			AxisFault axis = (AxisFault)e.getCause();
			if(null != axis.getFaultCode()){
				dict.put(DN_FAULT_CODE, axis.getFaultCode().toString()); 
			}   
			return PIPELET_ERROR;
		}

		catch (Exception e) {
			//Logger.error(this, "Error in wbservice [ TechnicalInstalledBase ] ", e);
			dict.put(DN_FAULT_CODE, "error");
			return PIPELET_ERROR;
		}
		dict.put(DN_GET_TECHNICALIBRESPONSE, getTechnicalResponse);
		return PIPELET_NEXT;
	}

	public GetInstalledBaseResponseV1 getTechnicalIBResponse(GetInstalledBaseRequestV1 requestType, String endPointUrl, int iTimeout,String userName,String password) throws AxisFault, Exception{
		TechnicalIBServiceHandler  technicalIBServiceHandler = new TechnicalIBServiceHandler();
		GetInstalledBaseResponseV1 responseType = null;
		try{
			WsPortType technicalIBPort = technicalIBServiceHandler.initTechnicalIBStub(endPointUrl, iTimeout,userName,password);
			responseType = technicalIBPort.getInstalledBase(requestType);             
			if( responseType == null ){
				throw new Exception( "TechnicalInstalledBase - responseType is not received." );
			}
		}catch(Exception e){
			System.out.println("Exception "+e);
			//Logger.error(this,"Exception occured in TechnicalInstalledBase ->",e);
		}
		return responseType;
	}
	public void init() throws PipelineInitializationException {
		// store 'KISConfigName' config value in field variable
		cfg_kISConfigName = (String) getConfiguration().get(CN_KISCONFIG_NAME);
		if (null == cfg_kISConfigName) {
			throw new PipelineInitializationException("Mandatory attribute 'KISConfigName' not found in pipelet configuration.");
		}
	}
	
}