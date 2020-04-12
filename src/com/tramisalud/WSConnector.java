package com.tramisalud;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class WSConnector {

  private static WSConnector wsConnector = null;
  private String username;
  private String password;
  private String url;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public static WSConnector getInstance() {
    if (wsConnector == null) {
      wsConnector = new WSConnector();
    }
    return wsConnector;
  }

  @SuppressWarnings("deprecation")
public String login() throws NoSuchAlgorithmException, ClientProtocolException, IOException {
	  

	MessageDigest md5 = MessageDigest.getInstance("MD5");
	String passwordHash = new BigInteger(1, md5.digest(this.password.getBytes())).toString(16);
	
	Map<String, String> userCredentials = new LinkedHashMap<String, String>();
	userCredentials.put("user_name", this.username);
	userCredentials.put("password", passwordHash);
	
	Map<String, Object> request = new LinkedHashMap<String, Object>();
	request.put("user_auth", userCredentials);
	request.put("application_name", "RestClient");
	
	MultipartEntity multipartEntity = new MultipartEntity();
	multipartEntity.addPart("method", new StringBody("login"));
	multipartEntity.addPart("input_type", new StringBody("JSON"));
	multipartEntity.addPart("response_type", new StringBody("JSON"));
	multipartEntity.addPart("rest_data", new StringBody(JSONObject.toJSONString(request)));
	
	
	
	HttpHost target = new HttpHost("209.126.122.86", 80, "http");
	HttpHost proxy = new HttpHost("192.168.1.73", 8080, "http");
	
	RequestConfig config = RequestConfig.custom()
            .setProxy(proxy)
            .build();
	HttpPost httpPost = new HttpPost(this.getUrl());
	httpPost.setEntity(multipartEntity);
	httpPost.setConfig(config);
	
	HttpClient httpClient = HttpClientBuilder.create().build();
	//httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, arg1);
	HttpResponse execute = httpClient.execute(target, httpPost);
	//HttpResponse execute = httpClient.execute(httpPost);
	/*
	 * DefaultHttpClient defaultHttpCliente = new DefaultHttpClient(); HttpResponse execute =
	 * defaultHttpCliente.execute(httpPost);
	 */
	HttpEntity entity = execute.getEntity();
	// TODO: Cuando hay un error no se esta reportando
	JSONObject parse = (JSONObject) JSONValue.parse(new InputStreamReader(entity.getContent()));
	return (String) parse.get("id");
  }

  public String setEntry(String session, String moduleName, Map<String, Object> nameValueList)
      throws ClientProtocolException, IOException {

    Map<String, Object> request = new LinkedHashMap<String, Object>();
    request.put("session", session);
    request.put("module_name", moduleName);
    request.put("name_value_list", nameValueList);
    
    /*
    MultipartEntity multipartEntity = new MultipartEntity();
    multipartEntity.addPart("method", new StringBody("set_entry"));
    multipartEntity.addPart("input_type", new StringBody("JSON"));
    multipartEntity.addPart("response_type", new StringBody("JSON"));
    multipartEntity.addPart("rest_data", new StringBody(JSONObject.toJSONString(request)));

    HttpPost httpPost = new HttpPost(this.getUrl());
    httpPost.setEntity(multipartEntity);
    */
    
    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    nvps.add(new BasicNameValuePair("method", new String("set_entry")));
    nvps.add(new BasicNameValuePair("input_type", new String("JSON")));
    nvps.add(new BasicNameValuePair("response_type", new String("JSON")));
    nvps.add(new BasicNameValuePair("rest_data", JSONObject.toJSONString(request)));
    
    HttpHost target = new HttpHost("209.126.122.86", 80, "http");
	HttpHost proxy = new HttpHost("192.168.1.73", 8080, "http");
	
	RequestConfig config = RequestConfig.custom()
            .setProxy(proxy)
            .build();

    HttpPost httpPost = new HttpPost(this.getUrl());
    httpPost.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));
    httpPost.setConfig(config);
    
    HttpClient httpClient = HttpClientBuilder.create().build();
	HttpResponse execute = httpClient.execute(target, httpPost);
    //HttpResponse execute = httpClient.execute(httpPost);
    /*
     * DefaultHttpClient defaultHttpCliente = new DefaultHttpClient(); HttpResponse execute =
     * defaultHttpCliente.execute(httpPost);
     */
    HttpEntity entity = execute.getEntity();
    JSONObject parse = (JSONObject) JSONValue.parse(new InputStreamReader(entity.getContent()));
    if (parse != null) {
      return ((String) parse.get("id"));
    } else {
      return null;
    }
  }

  public String getEntry(String session, String module, String id, List<String> selectedFields) {
    return null;
  }

  public String getEntryId(String session, String moduleName, String query, String orderBy,
      List<String> selectedFields) throws ClientProtocolException, IOException {
    Map<String, Object> request = new LinkedHashMap<String, Object>();
    request.put("session", session);
    request.put("module_name", moduleName);
    request.put("query", query);
    request.put("order_by", "");
    request.put("offset", "");
    request.put("select_fields", selectedFields);
    request.put("link_name_to_fields_array", new ArrayList<>());

    //MultipartEntity multipartEntity = new MultipartEntity();
    //multipartEntity.addPart("method", new StringBody("get_entry_list"));
    //multipartEntity.addPart("input_type", new StringBody("JSON"));
    //multipartEntity.addPart("response_type", new StringBody("JSON"));
    //multipartEntity.addPart("rest_data", new StringBody(JSONObject.toJSONString(request)));

    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    nvps.add(new BasicNameValuePair("method", "get_entry_list"));
    nvps.add(new BasicNameValuePair("input_type", "JSON"));
    nvps.add(new BasicNameValuePair("response_type", "JSON"));
    nvps.add(new BasicNameValuePair("rest_data", JSONObject.toJSONString(request)));
    
    HttpHost target = new HttpHost("209.126.122.86", 80, "http");
	HttpHost proxy = new HttpHost("192.168.1.73", 8080, "http");
	
	RequestConfig config = RequestConfig.custom()
            .setProxy(proxy)
            .build();

    HttpPost httpPost = new HttpPost(this.getUrl());
    httpPost.setEntity(new UrlEncodedFormEntity(nvps,"ISO-8859-1"));
    httpPost.setConfig(config);
   
    //httpPost.setEntity(new UrlEncodedFormEntity(nvps));
    
    //HttpPost httpPost = new HttpPost(this.getUrl());
    //httpPost.setEntity(multipartEntity);

    HttpClient httpClient = HttpClientBuilder.create().build();
    //HttpResponse execute = httpClient.execute(httpPost);
    HttpResponse execute = httpClient.execute(target, httpPost);
    /*
     * DefaultHttpClient defaultHttpCliente = new DefaultHttpClient(); HttpResponse execute =
     * defaultHttpCliente.execute(httpPost);
     */
    HttpEntity entity = execute.getEntity();
    JSONObject parse = (JSONObject) JSONValue.parse(new InputStreamReader(entity.getContent()));
    if (parse != null) {
      JSONArray jsonArray = (JSONArray) parse.get("entry_list");
      if (jsonArray.size() > 0) {
        JSONObject json = (JSONObject) jsonArray.get(0);
        return (String) json.get("id");
      } else {
        return null;
      }
    } else {
      return null;
    }

  }

  public void setRelation(String session, String moduleName, String parentId, String linkFieldName,
      String childId) throws ClientProtocolException, IOException {
    Map<String, Object> request = new LinkedHashMap<String, Object>();

    List<String> ids = new ArrayList<String>();
    ids.add(childId);

    request.put("session", session);
    request.put("module_name", moduleName);
    request.put("module_id", parentId);
    request.put("link_field_name", linkFieldName);
    request.put("related_ids", ids);
    request.put("name_value_list", "name");
    request.put("delete", 0);
  
    /*
    MultipartEntity multipartEntity = new MultipartEntity();
    multipartEntity.addPart("method", new StringBody("set_relationship"));
    multipartEntity.addPart("input_type", new StringBody("JSON"));
    multipartEntity.addPart("response_type", new StringBody("JSON"));
    multipartEntity.addPart("rest_data", new StringBody(JSONObject.toJSONString(request)));
     */
    
    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    nvps.add(new BasicNameValuePair("method", "set_relationship"));
    nvps.add(new BasicNameValuePair("input_type", "JSON"));
    nvps.add(new BasicNameValuePair("response_type", "JSON"));
    nvps.add(new BasicNameValuePair("rest_data", JSONObject.toJSONString(request)));
    
    HttpHost target = new HttpHost("209.126.122.86", 80, "http");
	HttpHost proxy = new HttpHost("192.168.1.73", 8080, "http");
	
	RequestConfig config = RequestConfig.custom()
            .setProxy(proxy)
            .build();

    HttpPost httpPost = new HttpPost(this.getUrl());
    httpPost.setEntity(new UrlEncodedFormEntity(nvps,"ISO-8859-1"));
    httpPost.setConfig(config);
    //httpPost.setEntity(multipartEntity);
    
    HttpClient httpClient = HttpClientBuilder.create().build();
    //HttpResponse execute = httpClient.execute(httpPost);
    HttpResponse execute = httpClient.execute(target, httpPost);
    
    //DefaultHttpClient defaultHttpCliente = new DefaultHttpClient();
    //HttpResponse execute = defaultHttpCliente.execute(httpPost);

    HttpEntity entity = execute.getEntity();
    JSONObject parse = (JSONObject) JSONValue.parse(new InputStreamReader(entity.getContent()));
    // return parse;
  }


  /*
  public boolean verifyConnection() throws Exception{
	  try {
		URL url = new URL(this.getUrl());
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		urlConn.connect();
		if( HttpURLConnection.HTTP_OK == urlConn.getResponseCode() ){
			return true;
		} else{
			return true;
		}
	} catch (Exception e) {
		e.printStackTrace();
		throw e;
	}
  }
*/
}
