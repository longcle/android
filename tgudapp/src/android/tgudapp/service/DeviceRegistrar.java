package android.tgudapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.tgudapp.model.CardInfomation;
import android.tgudapp.utils.ComplexPreferences;
import android.tgudapp.utils.TGUDApplication;
import android.util.Log;

public class DeviceRegistrar implements Runnable {
	
	private String deviceId;
	private InputStream is = null;
	private String json = "";
	private TGUDApplication tgudApplication;
	
	public DeviceRegistrar(TGUDApplication tgudApplication, String deviceId) {
		super();
		this.tgudApplication = tgudApplication;
		this.deviceId = deviceId;
	}
	
	@Override
	public void run() {
		String url = "http://thegioiuudai.com.vn/apps/server.php/member/device";
		try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>(1);
            params.add(new BasicNameValuePair("deviceId", this.deviceId));
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            
            ComplexPreferences pref = tgudApplication.getComplexPreference();
			if(pref != null){
				pref.putObject("E_CARD_NUMBER", json);
				pref.commit();
			}else{
				Log.e("DeviceRegistrar", "Preference is null");
			}
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        Log.d("Json string =>>>", json);
	}

}
