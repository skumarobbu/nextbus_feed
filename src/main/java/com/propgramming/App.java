package com.propgramming;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Program to send push bullet note of estimated arrival time of buses to  to xtbus channel!
 *
 */
public class App 
{
	private static final String	API_KEY	= "Bearer 0rYSj5cHMKLyvF18MCyJYN4zrE8vT1YV";

	public static void main( String[] args )
	{
		String url = "http://webservices.nextbus.com/service/publicXMLFeed?command="
					+ "predictions&a=emery&r=Hollis&d=hollis_inbound&s=ho64_i&title=NextBus";
		/*
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod(url);
		
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
						new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			System.out.println(new String(responseBody)); 
		}catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		}catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		}finally {
		// Release the connection.
			method.releaseConnection();
		}
		*/
		
		//<< XML Parsing >>
		DocumentBuilderFactory factory =DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(url);
			NodeList bodyList = document.getDocumentElement().getChildNodes();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < bodyList.getLength(); i++) {
				Node bodyNode = bodyList.item(i);
				if (bodyNode instanceof Element) {
					System.out.println("Node Attributes: "+bodyNode.getAttributes().getNamedItem("stopTitle").getNodeValue());
					//sb.append("Stop Title -> ").append(bodyNode.getAttributes().getNamedItem("stopTitle").getNodeValue()).append(" | ");
					NodeList predictionsList = bodyNode.getChildNodes();
					for (int j = 0; j < predictionsList.getLength(); j++) {
						Node predNode = predictionsList.item(j);
						if (predNode instanceof Element) {
							NodeList directionsList = predNode.getChildNodes();
							System.out.println("Node Attributes: "+predNode.getAttributes().getNamedItem("title").getNodeValue());
							//sb.append("Direction -> ").append(predNode.getAttributes().getNamedItem("title").getNodeValue()).append(" | ");
							sb.append("Arrives in :");
							for (int k = 0; k < directionsList.getLength(); k++) {
								Node routeNode = directionsList.item(k);
								if (routeNode instanceof Element) {
									System.out.println("Node Attributes: "+routeNode.getAttributes().getNamedItem("minutes").getNodeValue());
									sb.append(routeNode.getAttributes().getNamedItem("minutes").getNodeValue()).append(" ..");
								}
							}
							
						}
					}
					
				}
			}
			
			//<< Send Push Note to channel xtbus
			String url2 = "https://api.pushbullet.com/v2/pushes";
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpost = new HttpPost(url2);
			httpost.setHeader("Authorization", API_KEY);
			httpost.setHeader("Content-Type", "application/json");
			if(sb.toString().isEmpty()){
				sb.append("No buses running");
			}
			String json = "{\"type\": \"note\", \"body\": \"xtBus Tracker\", \"channel_tag\": \"xtbus\", \"title\": \""+sb.toString()+"\"}";
			System.out.println("Json value : "+json);
			StringEntity entity = new StringEntity(json);
			httpost.setEntity(entity);

			System.out.println("POST Response Status:: "
							+ httpclient.execute(httpost).getStatusLine().getStatusCode());
			
			//System.out.println( "Result: " + result );
			
		}catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} 
}
