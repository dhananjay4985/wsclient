package com.kpn.enfinity.Kpnfiberapp.internal.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.apache.axis2.AxisFault;

import com.intershop.beehive.core.capi.log.Logger;
import com.kpn.services.services.getinstalledbase_v1.GetInstalledBaseService;
import com.kpn.services.services.getinstalledbase_v1.WsPortType;

public class TechnicalIBServiceHandler
{
	public WsPortType initTechnicalIBStub(String sEndpointUrl, int iTimeOut,String userName,String password) throws AxisFault, Exception{
		
		System.out.println("Inside IBHandler");
		WsPortType technicalIBPort = null;
		/*System.out.println("Endpoint :"+sEndpointUrl);
		System.out.println("timeout :"+iTimeOut);
		System.out.println("user :"+userName);
		System.out.println("password :"+password);*/
		try{
			URL baseUrl;
			baseUrl = com.kpn.services.services.getinstalledbase_v1.GetInstalledBaseService.class.getResource("/META-INF/wsdls/GetInstalledBase-v1.1.wsdl");
			//System.out.println("Base URL :"+baseUrl);
			GetInstalledBaseService service = new GetInstalledBaseService(baseUrl, new QName("http://services.kpn.com/Services/GetInstalledBase-v1","GetInstalledBaseService"));
			//System.out.println("WSDL Location :"+service.getWSDLDocumentLocation());
			System.out.println("Before");
			technicalIBPort = service.getWsPort();
			System.out.println("After");
			//time-out
			((BindingProvider)technicalIBPort).getRequestContext().put(org.apache.axis2.transport.http.HTTPConstants.SO_TIMEOUT, iTimeOut);
			((BindingProvider)technicalIBPort).getRequestContext().put(org.apache.axis2.transport.http.HTTPConstants.CONNECTION_TIMEOUT, iTimeOut);
			//endpoint
			((BindingProvider)technicalIBPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,sEndpointUrl);
			//header
			@SuppressWarnings("rawtypes")
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.add(new TechnicalIBSecurityHeaderSOAPHandler());
			//handlerChain.add(new TechnicalIBSecurityHeaderSOAPHandler(userName,password));
			((BindingProvider) technicalIBPort).getBinding().setHandlerChain(handlerChain);
		}
		catch (Exception e) {
			Logger.error(this,"error from TechnicalInstalledBase",e);
		}
		return technicalIBPort;
	}

}
