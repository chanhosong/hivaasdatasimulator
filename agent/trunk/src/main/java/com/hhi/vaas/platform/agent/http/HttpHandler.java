/* Copyright (C) 2015~ Hyundai Heavy Industries. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Hyundai Heavy Industries
 * You shall not disclose such Confidential Information and shall use it only 
 * in accordance with the terms of the license agreement
 * you entered into with Hyundai Heavy Industries.
 *
 * Revision History
 * Author			Date				Description
 * ---------------	----------------	------------
 * Bongjin Kwon	2015. 5. 6.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.http;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * org.apache.http handler.
 * 
 * @author BongJin Kwon
 *
 */
public class HttpHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpHandler.class);

	/**
	 * 
	 */
	public HttpHandler() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * post request for enlist
	 * @param uri
	 * @param params
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public Content requestEnlistPost(String uri, List<NameValuePair> params) throws IOException, ClientProtocolException{
		return requestPost(uri, params);
	}

	/**
	 * post request for noti
	 * 
	 * @param uri
	 * @param params
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public Content requestNotiPost(String uri, List<NameValuePair> params) throws IOException, ClientProtocolException{
		return requestPost(uri, params);
	}
	
	/**
	 * post request for update result.
	 * 
	 * @param uri
	 * @param params
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public Content requestUpdateResultPost(String uri, List<NameValuePair> params) throws IOException, ClientProtocolException{
		return requestPost(uri, params);
	}
	
	/**
	 * post request that used common
	 * 
	 * @param uri
	 * @param params
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public Content requestPost(String uri, List<NameValuePair> params) throws IOException, ClientProtocolException{
		LOGGER.info("request : {}", uri);
		return Request.Post(uri).bodyForm(params).execute().returnContent();
	}
	
	public void download(String uri, String downFilePath) throws IOException, ClientProtocolException{
		LOGGER.info("Connecting... {}", uri);
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(uri);
        HttpResponse response = client.execute(get);

        InputStream input = null;
        OutputStream output = null;
        byte[] buffer = new byte[1024];

        try {
        	LOGGER.info("Downloading... {}", downFilePath);
        	
            input = response.getEntity().getContent();
            output = new FileOutputStream(downFilePath);
            for (int length; (length = input.read(buffer)) > 0;) {
                output.write(buffer, 0, length);
            }
            LOGGER.info("File successfully downloaded!");
        } finally {
            if (output != null) try { output.close(); } catch (IOException logOrIgnore) {}
            if (input != null) try { input.close(); } catch (IOException logOrIgnore) {}
        }
	}
	

}
//end of HttpHandler.java