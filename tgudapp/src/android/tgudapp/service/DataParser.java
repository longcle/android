package android.tgudapp.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.tgudapp.model.Banner;
import android.tgudapp.model.ListPlace;
import android.tgudapp.model.Place;

public class DataParser {
	
	public List<Banner> parserListBannerResponse (JSONArray response){
		try {
			List<Banner> listBanner = new ArrayList<Banner>();
			for(int i=0; i<response.length(); i++){
				JSONObject jObj = response.getJSONObject(i);
				String bannerId = jObj.getString("bannerId");
				String bannerName = jObj.getString("name");
				
				String bannerImageUrl = jObj.getString("image");
				String bannerImage = bannerImageUrl.replaceAll(" ", "%20");
				String bannerUrl = jObj.getString("url");
				String bannerOrder = jObj.getString("order");
				listBanner.add(new Banner(bannerId, bannerName, bannerImage, bannerUrl, bannerOrder));
			}
			return listBanner;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public List<Place> parserListPlaceResponse(JSONArray response) {
		try {
            
            List<Place> places = new ArrayList<Place>();
            for (int i= 0; i< response.length(); i++){
            	JSONObject jObj = response.getJSONObject(i);
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
//            ListPlace listPlace = new ListPlace(places);
            return places;
	      
	        
        }catch(JSONException e){
        	e.printStackTrace();
        	return null;
        }
	}
	
//	public ListPlace parserListNearbyPlaceResponse(JSONArray response){
//		try {
//			
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

}
