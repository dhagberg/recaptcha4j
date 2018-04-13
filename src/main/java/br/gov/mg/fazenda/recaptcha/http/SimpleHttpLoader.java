package br.gov.mg.fazenda.recaptcha.http;

/*
 * Copyright 2007 Soren Davidsen, Tanesha Networks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import br.gov.mg.fazenda.recaptcha.ReCaptchaException;


/**
 *
 * @author leonardo.luz
 * @version 0.1
 * @since 01/02/2015
 *
 */

public class SimpleHttpLoader implements HttpLoader {

	private Proxy proxy;

	public void setProxy( Proxy proxy ){
		this.proxy = proxy ;
	}


	public String httpGet(String urlS) {

		InputStream in = null;
		URLConnection connection = null;

		try {

			URL url = new URL(urlS);

			if( proxy != null ){
				connection = url.openConnection( proxy );
			}else{
				connection = url.openConnection();
			}

			// set a "reasonable" timeout
			connection.setReadTimeout(10000);
			connection.setConnectTimeout(10000);

			in = connection.getInputStream();

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];

			while (true) {

				int rc = in.read(buf);

				if (rc <= 0){
					break;
				}else {
					bout.write(buf, 0, rc);
				}
			}

			return bout.toString();

		} catch (IOException e) {
			throw new ReCaptchaException("Cannot load URL: " + e.getMessage(), e);
		} finally {

			try {
				if (in != null){
					in.close();
				}
			} catch (Exception e) {
				// swallow.
			}

		}
	}

	public String httpPost(String urlS, String postdata) {

		InputStream in = null;
		URLConnection connection = null;


		try {

			URL url = new URL(urlS);

			if( proxy != null ){
				connection = url.openConnection(proxy);
			}else{
				connection = url.openConnection();
			}

			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("User-Agent","Mozilla/5.0");
			connection.setRequestProperty("Accept-Charset","UTF-8");
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");

			//setJdk15Timeouts(connection);

			OutputStream out = connection.getOutputStream();
			out.write(postdata.getBytes());
			out.flush();

			in = connection.getInputStream();

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];

			while (true) {

				int rc = in.read(buf);

				if (rc <= 0){
					break;
				}else {
					bout.write(buf, 0, rc);
				}
			}

			out.close();
			in.close();

			return bout.toString();

		} catch (IOException e) {
			throw new ReCaptchaException("Cannot load URL: " + e.getMessage(), e);
		} finally {

			try {

				if (in != null){
					in.close();
				}
			}
			catch (Exception e) {
				// swallow.
			}

		}
	}
}
