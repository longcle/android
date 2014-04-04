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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.tgudapp.model.ListPlace;
import android.tgudapp.model.Place;
import android.util.Log;

public class GetNearbyPlace implements Runnable {

	private final Handler handlerNearby;
	private final String lattitude;
	private final String longtitude;
	private final String distantLimit;
	static InputStream is = null;
	static String json = "";
	public GetNearbyPlace(Handler handlerNearby, String lattitude,
			String longtitude, String distantLimit) {
		super();
		this.handlerNearby = handlerNearby;
		this.lattitude = lattitude;
		this.longtitude = longtitude;
		this.distantLimit = distantLimit;
	}
	@Override
	public void run() {
		
		String URL = "http://thegioiuudai.com.vn/apps/server.php/merchant/nearby?currentLat=" + lattitude + "&currentLong=" + longtitude + "&distantLimit=" + distantLimit;
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
            JSONArray jArray = new JSONArray(json);
            List<Place> places = new ArrayList<Place>();
            for (int i= 0; i< jArray.length(); i++){
            	JSONObject jObj = jArray.getJSONObject(i);
            	String id = jObj.getString("id");
				String name = jObj.getString("name");
				String address = jObj.getString("address");
				String phoneNumber = jObj.getString("phoneNumber");
				String promotionPercentage ;
				if(null != jObj.getString("promotionPercentage")
					    || jObj.getString("promotionPercentage").trim().length() != 0){
					promotionPercentage = jObj.getString("promotionPercentage");
				}else{
					promotionPercentage = "0";
				}
				
				String image = jObj.getString("image");
				String expiryDate = jObj.getString("expiryDate");
				String category = jObj.getString("category");
				String district = jObj.getString("district");
				String features = jObj.getString("features");
				String lat = jObj.getString("lat");
				String lng = jObj.getString("lng");
				String conditions = jObj.getString("conditions");
				String description = jObj.getString("description");
				places.add(new Place(id, name, address, phoneNumber, promotionPercentage, image, expiryDate, category, district,features,lat,lng,conditions,description));
            }
            ListPlace listPlace = new ListPlace(places);
            Log.d("ListPlace size", Integer.toString(listPlace.getListPlace().size()));
            Bundle data = new Bundle();
            data.putSerializable(ListPlace.LISTPLACE, listPlace);
            Message msg = Message.obtain();
            msg.setData(data);
            handlerNearby.sendMessage(msg);
	      
	        
        } catch (JSONException e) {
        	e.printStackTrace();
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
	}
	

}
