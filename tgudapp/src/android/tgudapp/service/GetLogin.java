package android.tgudapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.tgudapp.model.CardInfomation;
import android.util.Log;

public class GetLogin implements Runnable {

	
	private Handler handlerReplyCardInfo;
	private String cardNumberOrEmail;
	private String dateOfBirth;
	private InputStream is = null;
	private String json = "";
	
	
	public GetLogin(Handler handlerReplyCardInfo, String cardNumberOrEmail,
			String dateOfBirth) {
		super();
		this.handlerReplyCardInfo = handlerReplyCardInfo;
		this.cardNumberOrEmail = cardNumberOrEmail;
		this.dateOfBirth = dateOfBirth;
	}


	@Override
	public void run() {
		
		String URL = "http://thegioiuudai.com.vn/apps/server.php/member/auth/login?cardNumberOrEmail=" + cardNumberOrEmail + "&dateOfBirth=" + dateOfBirth;
		try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            // post
//            HttpPost httpPost = new HttpPost(URL);
            // get
            HttpUriRequest httpGet = new HttpGet(URL);
            HttpResponse httpResponse = httpClient.execute(httpGet);
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
            
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        Log.d("Json string =>>>", json);
        // try parse the string to a JSON object
        try {
        	CardInfomation cardInfo = null;
            JSONObject jObj = new JSONObject(json);
            String responseCode = jObj.getString("responseCode");
            String spentAmount = jObj.getString("spentAmount");
            String savedAmount = jObj.getString("savedAmount");
            String redeemedPoints = jObj.getString("redeemedPoints");
            String remainingPoints = jObj.getString("remainingPoints");
            String memberId = jObj.getString("member_id");
            cardInfo = new CardInfomation(cardNumberOrEmail, dateOfBirth, responseCode, spentAmount, savedAmount, redeemedPoints, remainingPoints, memberId);
            Bundle bundle = new Bundle();
            bundle.putSerializable(CardInfomation.CARDINFORMATION, cardInfo);
            Message msg = Message.obtain();
            msg.setData(bundle);
            handlerReplyCardInfo.sendMessage(msg);
	        
        } catch (JSONException e) {
        	e.printStackTrace();
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
		
	}

}
