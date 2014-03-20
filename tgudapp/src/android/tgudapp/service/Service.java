package android.tgudapp.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.tgudapp.listener.IServiceListener;
import android.tgudapp.utils.API;
import android.util.Log;

public class Service implements Runnable {
	
	private static final String TAG = Service.class.getSimpleName();
	private HttpURLConnection _connection;
	private ServiceAction _action;
	private ArrayList<IServiceListener> _listener;
	private boolean _connecting;
	private Thread _thread;
	private String _actionURI;
	private Map<String, Object> _params;
	private boolean _includeHost;
	private boolean _isGet;
	private Service _service;
	private boolean _isBitmap;
	private HttpClient httpClient;
	
	public void login(String cardNumberOrEmail, String dateOfBirth){
		_action = ServiceAction.ActionLogin;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cardNumberOrEmail", cardNumberOrEmail);
		params.put("dateOfBirth", dateOfBirth);
		request("/member/auth/login", params, true, true);
	}
	
	public void getPlaceByCityFollowCategory(String cityId, String categoryId){
		_action = ServiceAction.ActionGetPlaceByCityFollowCategory;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("district", cityId);
		params.put("industry", categoryId);
		request("/merchant/list", params, true, true);
	}
	
	final Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			for(IServiceListener listener : _listener){
				listener.onCompleted(_service, (ServiceResponse)msg.obj);
			}
		}
		
	};
	
	public Service(IServiceListener...listeners){
		_action = ServiceAction.ActionNone;
		_listener = new ArrayList<IServiceListener>();
		if(_listener != null){
			for(IServiceListener listener : listeners){
				_listener.add(listener);
			}
		}
		_connecting = false;
		_includeHost = true;
		_service = this;
		_isBitmap = false;
	}
	
	public void addListener(IServiceListener listener){
		_listener.add(listener);
	}
	
	public boolean isConnecting(){
		return _connecting;
	}
	
	public void stop(){
		//clean up
		if(httpClient != null){
			httpClient.getConnectionManager().shutdown();
		}
		_action = ServiceAction.ActionNone;
		_connecting = false;
	}
	
	private boolean request(String uri, Map<String, Object> params, boolean includeHost){
		if(_connecting)
		{
			return false;
		}
		_connecting = true;
		_actionURI = uri;
		_params = params;
		_includeHost = includeHost;
		_thread = new Thread(this);
		_thread.start();
		return true;
	}

	private boolean request(String uri, Map<String, Object> params, boolean includeHost, boolean isGet){
		_isGet = isGet;
		request(uri, params, includeHost);
		return true;
	}
	
	private void processError(ResultCode errorCode){
		Message msg = handler.obtainMessage(0, new ServiceResponse(_action, null, errorCode));
		handler.sendMessage(msg);
	}
	
	private void dispatchResult(String result){
		if (_listener == null || _action == ServiceAction.ActionNone || !_connecting)
		{
			return;
		}
		ServiceAction act = _action;
		Object resObj = null;
		ServiceResponse response = null;
		DataParser parser = new DataParser();
		switch (act) {
		case ActionNone:
			
			break;
		case ActionLogin:
//			resObj = parser.parserLogin(result);
			break;
		case ActionGetPlaceByCityFollowCategory:
//			resObj = parser.parserPlaceByCityFollowCategory(result);
			break;
		default:
			resObj = result;
			break;
		}
		
		if(resObj == null){
			response = new ServiceResponse(act, null, ResultCode.Failed);
		}else{
			response = new ServiceResponse(act, resObj);
		}
		// stop thread
		stop();
		//send message (response) to UI
		Message msg = handler.obtainMessage(0, response);
		handler.sendMessage(msg);
	}
	
	private void dispatchResult(Bitmap result){
		if (_listener == null || _action == ServiceAction.ActionNone || !_connecting)
		{
			return;
		}
		ServiceAction act = _action;
		ServiceResponse response = null;
		if(result == null){
			response = new ServiceResponse(act, null, ResultCode.Failed);
		}else{
			response = new ServiceResponse(act, result);
		}
		// stop thread
		stop();
		// send message (response) to UI
		Message msg = handler.obtainMessage(0, response);
		handler.sendMessage(msg);
	}
	@Override
	public void run() {
		Log.d(_action.toString(), "=========== run ===========\n" + _actionURI);
		httpClient = new DefaultHttpClient();
		HttpParams httpParameters = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
		HttpConnectionParams.setSoTimeout(httpParameters, 600000);
		HttpConnectionParams.setTcpNoDelay(httpParameters, true);
		try {
			String urlString = null;
			if(_action == ServiceAction.ActionPostOrder){
				
			}else{
				urlString = _includeHost ? API.hostURL + _actionURI : _actionURI;
			}
			HttpRequestBase request = null;
			if(_isGet){
				request = new HttpGet();
				if(_params != null){
					attachUriWithQuery(request, Uri.parse(urlString), _params);
				}
			}else{
				request = new HttpPost();
				if(_params != null){
					MultipartEntity reqEntity = paramsToList2(_params);
					((HttpPost)request).setEntity(reqEntity);
				}
			}
			HttpResponse response = httpClient.execute(request);
			InputStream in = null;
			if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK){
				Header[] header = response.getHeaders("Content-Encoding");
				if (header != null && header.length != 0) {
					for (Header h : header) {
						if (h.getName().trim().equalsIgnoreCase("gzip"))
							in = new GZIPInputStream(response.getEntity()
									.getContent());
					}
				}

				if (in == null)
					in = new BufferedInputStream(response.getEntity()
							.getContent());

				if (_isBitmap) {
					Bitmap bm = BitmapFactory.decodeStream(in);
					dispatchResult(bm);
				} else {
					String temp = convertStreamToString(in);// text.toString();
					Log.d(_action.toString(), "==" + temp + "");
					in.close();
					dispatchResult(temp);
				}

			} else if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND)
				processError(ResultCode.Failed);
			else if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_SERVER_ERROR)
				processError(ResultCode.ServerError);
			else
				processError(ResultCode.NetworkError);
		} catch (Exception e) {
			e.printStackTrace();
			processError(ResultCode.NetworkError);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		
	}
	private void attachUriWithQuery(HttpRequestBase request, Uri uri,
			Map<String, Object> params) {
		try {
			if (params == null) {
				// No params were given or they have already been
				// attached to the Uri.
				request.setURI(new URI(uri.toString()));
			} else {
				Uri.Builder uriBuilder = uri.buildUpon();

				// Loop through our params and append them to the Uri.
				for (BasicNameValuePair param : paramsToList(params)) {
					uriBuilder.appendQueryParameter(param.getName(),
							param.getValue());
				}

				uri = uriBuilder.build();
				request.setURI(new URI(uri.toString()));
			}
		} catch (URISyntaxException e) {
			Log.e(TAG, "URI syntax was incorrect: " + uri.toString());
		}
	}

	private static List<BasicNameValuePair> paramsToList(
			Map<String, Object> params) {
		ArrayList<BasicNameValuePair> formList = new ArrayList<BasicNameValuePair>(
				params.size());

		for (String key : params.keySet()) {
			Object value = params.get(key);

			if (value != null)
				formList.add(new BasicNameValuePair(key, value.toString()));
		}

		return formList;
	}

	public static MultipartEntity paramsToList2(Map<String, Object> params) {
		MultipartEntity reqEntity = new MultipartEntity();
		for (String key : params.keySet()) {
			try {

				Object value = params.get(key);
				if (key.toUpperCase().equals("PHOTO")) {
					reqEntity.addPart(key, (ContentBody) value);
				} else {
					Charset chars = Charset.forName("UTF-8");
					reqEntity.addPart(key, new StringBody(value.toString(),
							chars));
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		return reqEntity;
	}
	private String convertStreamToString(InputStream is) {
		// TODO Auto-generated method stub

		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */

		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch (IOException e) {
				return "";
			} finally {
				try {
					is.close();
				} catch (IOException e) {

				}
			}
			return writer.toString();
		} else {
			return "";
		}

	}

	public int getConnectionTimeout() {
		return _connection.getConnectTimeout();
	}
}
