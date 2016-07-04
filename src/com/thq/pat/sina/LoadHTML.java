package com.thq.pat.sina;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * @filename LoadHTML.java
 * @version  1.0
 * @note     (1) Download html pages according to url (search keywords),
 *           (2) Defined 3 methods to get html: normal, custom cookie policy, and proxyIP.
 * @author   DianaCody
 * @since    2014-09-27 15:23:28
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
public class LoadHTML {
	
	/** 一般方法 */
	public String[] getHTML(String url) throws ClientProtocolException, IOException {
		String[] html = new String[2];
		html[1] = "null";
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(2000) //设置socket超时
				.setConnectTimeout(2000) //设置connect超时
				.build();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.build();
		HttpGet httpGet = new HttpGet(url);
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			//System.out.println(response.getStatusLine().getStatusCode());
			html[0] = String.valueOf(response.getStatusLine().getStatusCode());
			html[1] = EntityUtils.toString(response.getEntity(), "utf-8");
			//System.out.println(html);
		} catch (IOException e) {
			System.out.println("Connection timeout...");
		}
		return html;
	}
	
	/** cookie方法的getHTMl(): 设置cookie策略,防止cookie rejected问题,拒绝写入cookie
	 *  --重载,3参数:url, hostName, port */
/*	public String getHTML(String url, String hostName, int port) throws URISyntaxException, ClientProtocolException, IOException {
		//自定义的cookie策略,解决cookie rejected问题(cookie拒绝写入)
		HttpHost proxy = new HttpHost(hostName, port);
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		CookieSpecProvider cookieSpecProvider = new CookieSpecProvider() {
			public CookieSpec create(HttpContext context) {
				return new BrowserCompatSpec() {
					@Override
					public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
						//Oh, I am easy;
					}
				};
			}
		};
		Registry<CookieSpecProvider> r = RegistryBuilder
				.<CookieSpecProvider> create()
				.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
				.register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory())
				.register("easy", cookieSpecProvider)
				.build();
		RequestConfig requestConfig = RequestConfig.custom()
				.setCookieSpec("easy")
				.setSocketTimeout(4000) //设置socket超时
				.setConnectTimeout(4000) //设置connect超时
				.build();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultCookieSpecRegistry(r)
				.setRoutePlanner(routePlanner)
				.build();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		String html = "null";
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			html = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (IOException e) {
			System.out.println("Connection timeout...");
		}
		return html;
	}*/
	
	/** proxy代理IP方法 */
	public String getHTMLbyProxy(String targetURL, String hostName, int port) throws ClientProtocolException, IOException {
		HttpHost proxy = new HttpHost(hostName, port);
		String html = "null";
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(2000) //设置socket超时
				.setConnectTimeout(2000) //设置connect超时
				.build();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setRoutePlanner(routePlanner)
				.setDefaultRequestConfig(requestConfig)
				.build();
		HttpGet httpGet = new HttpGet(targetURL); //"http://iframe.ip138.com/ic.asp"
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			//System.out.println(response.getStatusLine().getStatusCode());
			if(statusCode == HttpStatus.SC_OK) {
				html = EntityUtils.toString(response.getEntity(), "gb2312");
			}
			response.close();
			//System.out.println(html);
		} catch (IOException e) {
			System.out.println("Connection timeout...");
		}
		return html;
	}
	
	//add by hongqi
	public String downloadPage(String pageUrl) {
		try {
		    pageUrl = removeWwwFromUrl(pageUrl);
			URL newUrl = verifyUrl(pageUrl);
			HttpURLConnection hConnect = (HttpURLConnection) newUrl.openConnection();
//          hConnect.setDefaultRequestProperty("http.agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT");
			hConnect.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT");

			// 读取内容

			InputStreamReader mInputStreamReader = new InputStreamReader(
					hConnect.getInputStream());
			BufferedReader reader = new BufferedReader(mInputStreamReader);

			// Open connection to URL for reading.
			//		        BufferedReader reader =
			//		          new BufferedReader(new InputStreamReader(pageUrl.openStream()));

			// Read page into buffer.
			String line;
			StringBuffer pageBuffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				pageBuffer.append(line);
			}

			reader.close();
			mInputStreamReader.close();
			hConnect.disconnect();

			return pageBuffer.toString();
		} catch (Exception e) {
		    Log.e("THQ err", "" + e);
		    e.printStackTrace();
		}

		return null;
	}
	
	
    //检测URL格式
    private URL verifyUrl(String url) {
        // 只处理HTTP URLs.
        if (!url.toLowerCase().startsWith("http://"))
            return null;

        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(url);
        } catch (Exception e) {
            return null;
        }

        return verifiedUrl;
    }
    
    // 从URL中去掉"www"
    private String removeWwwFromUrl(String url) {
        int index = url.indexOf("://www.");
        if (index != -1) {
            return url.substring(0, index + 3) +
                    url.substring(index + 7);
        }

        return (url);
    }

}