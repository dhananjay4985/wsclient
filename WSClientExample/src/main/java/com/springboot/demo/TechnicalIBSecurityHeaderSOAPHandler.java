package com.kpn.enfinity.Kpnfiberapp.internal.utils;

import java.io.ByteArrayOutputStream;
import java.util.Set;
import javax.xml.soap.SOAPHeader;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPHeaderElement;

import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;

import com.intershop.beehive.core.capi.log.Logger;


public class TechnicalIBSecurityHeaderSOAPHandler implements SOAPHandler<SOAPMessageContext> {


	private static final String SOAP_ELEMENT_USERNAMETOKEN = "UsernameToken";
	private static final String SOAP_ELEMENT_USERNAME = "Username";
	private static final String SOAP_ELEMENT_PASSWORD = "Password";
	private static final String SOAP_ELEMENT_MESSAGEHEADER = "Security";
	private static final String NAMESPACE_SECURITY = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	private static final String PREFIX_SECURITY = "oas";


	public TechnicalIBSecurityHeaderSOAPHandler() {
	}

	/*private String userName = null;
	private String password = null;

	public TechnicalIBSecurityHeaderSOAPHandler(String userName,String password) {
		this.userName = userName;
		this.password = password;
	}
*/
	public boolean handleMessage(SOAPMessageContext soapMessageContext) {

		// if (Logger.isDebugEnabled())
		//   Logger.debug(this,"Start -> handleMessage()");
	//	System.out.println("rohit");
		Boolean outboundProperty = (Boolean) soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outboundProperty.booleanValue()) {

			try {
				SOAPMessage message = soapMessageContext.getMessage();
				SOAPEnvelope soapEnvelope = message.getSOAPPart().getEnvelope();

				SOAPHeader header = soapEnvelope.getHeader();
				if (header == null) {
					header = soapEnvelope.addHeader();
				}
				Options op = new Options();
				op.setProperty(HTTPConstants.CHUNKED,false);
				//soapEnvelope
				Name headerContextName = soapEnvelope.createName(SOAP_ELEMENT_MESSAGEHEADER, PREFIX_SECURITY, NAMESPACE_SECURITY);
				SOAPHeaderElement soapElementSecurityHeader = header.addHeaderElement(headerContextName);

				SOAPElement soapElementFrom = soapElementSecurityHeader.addChildElement(SOAP_ELEMENT_USERNAMETOKEN, PREFIX_SECURITY);
				SOAPElement soapElementUsername = soapElementFrom.addChildElement(SOAP_ELEMENT_USERNAME, PREFIX_SECURITY);
				soapElementUsername.addTextNode("wlp");

				SOAPElement soapElementPassword = soapElementFrom.addChildElement(SOAP_ELEMENT_PASSWORD, PREFIX_SECURITY);
				soapElementPassword.addTextNode("t3stwlp");

				//Printing the request in the log file
				StringBuilder builder = new StringBuilder();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				message.writeTo(System.out);
				message.writeTo(baos);
				builder.append(baos.toString());

				//if (Logger.isDebugEnabled()){
				//  System.out.println("::" +builder.toString());
				//Logger.debug(this,"KAM Request"+builder.toString());
				//Logger.debug(this,"Exit -> handleMessage()");
				// }
			} catch (Exception e) {
				System.out.println("Excepiton in Security header");
				// Logger.error(this,"Error on wsSecurityHandler  ->"+e.getMessage());
			}
		}
		return true;
	}

	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
}