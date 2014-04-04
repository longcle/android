package android.tgudapp.ui.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.tgudapp.adapter.ListViewPlaceAdapter;
import android.tgudapp.model.Place;
import android.tgudapp.service.DataParser;
import com.tgud.R;

import android.tgudapp.utils.GMapV2Direction;
import android.tgudapp.utils.HttpClient;
import android.tgudapp.utils.TGUDApplication;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentMapPlaceDetail extends Fragment implements OnClickListener, OnInfoWindowClickListener {
	private GoogleMap mMap;
	private Place placeItem;
	private double latPlace;
	private double lngPlace;
	private LatLng latlngPlace;
	private RequestQueue mVolleyRequest;
	private JsonArrayRequest jArrRequest;
	private List<Place> listPlaceNearbyPlace;
	private ListViewPlaceAdapter mListPlaceAdapter;
	private ProgressDialog mProgress;
	private ListView lvPlaceNearbyPlace;
	private TextView tvNearbyPlace;
	private final String TAG_REQUEST = "MY_TAG";
	private Button btnFindNearby, btnGetDirection;
	private LinearLayout llNearbyPlace;
	private FragmentMapPlaceDetail myself = this;
	private Map<Marker, Place> markerMap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		placeItem = (Place) getArguments().getSerializable(FragmentMain.PLACEITEM);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setUpActionBar();
		View v = inflater.inflate(R.layout.fragment_map_place_detail, container, false);
		btnFindNearby = (Button)v.findViewById(R.id.btnFindNearby);
		btnFindNearby.setOnClickListener(this);
		btnGetDirection = (Button)v.findViewById(R.id.btnGetDirection);
		btnGetDirection.setOnClickListener(this);
		llNearbyPlace = (LinearLayout)v.findViewById(R.id.llNearbyPlace);
		lvPlaceNearbyPlace = (ListView)v.findViewById(R.id.lvPlaceNearbyFromPlace);
		tvNearbyPlace = (TextView)v.findViewById(R.id.tvNearbyPlace);
		lvPlaceNearbyPlace.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Place item;
				item = (Place) arg0.getItemAtPosition(arg2);
				Bundle bundle = new Bundle();
				bundle.putSerializable(FragmentMain.PLACEITEM, item);
				FragmentPlaceDetail fragPlaceDetail = new FragmentPlaceDetail();
				fragPlaceDetail.setArguments(bundle);
				FragmentManager fragManager = getActivity().getSupportFragmentManager();
				FragmentTransaction fragTran = fragManager.beginTransaction();
				fragTran.replace(R.id.frameContent, fragPlaceDetail)
						.addToBackStack(null)
						.commit();
			}
		});
		markerMap = new HashMap<Marker, Place>();
		setUpMapIfNeeded();
		return v;
	}
	private void setUpActionBar(){
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().getActionBar().setTitle(placeItem.getNamePlace());
	}
	@Override
	public void onResume() {
		setUpMapIfNeeded(); 
		super.onResume();
	}
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
        	FragmentManager fragManager = getActivity().getSupportFragmentManager();
            mMap = ((SupportMapFragment) fragManager.findFragmentById(R.id.mapPlaceDetail))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
	}
    private void setUpMap() {
    	if (placeItem.getLat() != "null" && placeItem.getLng() != "null") {
    		latPlace = Double.parseDouble(placeItem.getLat());
    		lngPlace = Double.parseDouble(placeItem.getLng());
    	} else {
    		try {
    			HttpGet httpGet = new HttpGet(
    	            "http://maps.google.com/maps/api/geocode/json?address="
    	                    + URLEncoder.encode(placeItem.getAddressPlace(), "utf-8") + "&ka&sensor=false");
    	    	org.apache.http.client.HttpClient client = new DefaultHttpClient();
    	    	HttpResponse response;
    	    	StringBuilder stringBuilder = new StringBuilder();

    	        response = client.execute(httpGet);
    	        HttpEntity entity = response.getEntity();
    	        InputStream stream = entity.getContent();
    	        int b;
    	        while ((b = stream.read()) != -1) {
    	            stringBuilder.append((char) b);
    	        }
    	        
    	        JSONObject jsonObject = new JSONObject();
        	    jsonObject = new JSONObject(stringBuilder.toString());

        	    lngPlace = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
        	            .getJSONObject("geometry").getJSONObject("location")
        	            .getDouble("lng");

        	    latPlace = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
        	            .getJSONObject("geometry").getJSONObject("location")
        	            .getDouble("lat");
        	    
    	    } catch (ClientProtocolException e) {
    	    } catch (IOException e) {
    	    } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	latlngPlace = new LatLng(latPlace, lngPlace);
    	
    	int locationIconId = R.drawable.location48;
		if (placeItem.getCategory().trim().equalsIgnoreCase("1")) {
			locationIconId = R.drawable.location_food_48;
		} else if (placeItem.getCategory().trim().equalsIgnoreCase("2")) {
			locationIconId = R.drawable.location_travel_48;
		} else if (placeItem.getCategory().trim().equalsIgnoreCase("3")) {
			locationIconId = R.drawable.location_shopping_48;
		} else if (placeItem.getCategory().trim().equalsIgnoreCase("4")) {
			locationIconId = R.drawable.location_health_48;
		} else if (placeItem.getCategory().trim().equalsIgnoreCase("5")) {
			locationIconId = R.drawable.location_entertainment_48;
		} else if (placeItem.getCategory().trim().equalsIgnoreCase("6")) {
			locationIconId = R.drawable.location_cafe_48;
		}
		mMap.addMarker(new MarkerOptions().position(latlngPlace)
											.icon(BitmapDescriptorFactory.fromResource(locationIconId))
											.title(placeItem.getNamePlace())
											.snippet(placeItem.getAddressPlace()));
		
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngPlace, 17);
		mMap.animateCamera(cameraUpdate);
		mMap.setMyLocationEnabled(true);
	}

    private void makeHttpRequestNearbyPlace() {
		String URL = "http://thegioiuudai.com.vn/apps/server.php/merchant/nearby";
		Uri.Builder builder = Uri.parse(URL).buildUpon();
		builder.appendQueryParameter("currentLat", Double.toString(latPlace));
		builder.appendQueryParameter("currentLong", Double.toString(lngPlace));
		builder.appendQueryParameter("distantLimit", "2");
		jArrRequest = new JsonArrayRequest(
				builder.toString(), 
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						try {
							DataParser parser = new DataParser();
							listPlaceNearbyPlace = parser.parserListPlaceResponse(response);
							for(int i = 0; i<listPlaceNearbyPlace.size(); i++){
								Place item = listPlaceNearbyPlace.get(i);
								double latItem = Double.parseDouble(item.getLat());
								double lngItem = Double.parseDouble(item.getLng());
								LatLng latlngItem = new LatLng(latItem, lngItem);
								int locationIconId = R.drawable.location48;
								if (item.getCategory().trim().equalsIgnoreCase("1")) {
									locationIconId = R.drawable.location_food_48;
								} else if (item.getCategory().trim().equalsIgnoreCase("2")) {
									locationIconId = R.drawable.location_travel_48;
								} else if (item.getCategory().trim().equalsIgnoreCase("3")) {
									locationIconId = R.drawable.location_shopping_48;
								} else if (item.getCategory().trim().equalsIgnoreCase("4")) {
									locationIconId = R.drawable.location_health_48;
								} else if (item.getCategory().trim().equalsIgnoreCase("5")) {
									locationIconId = R.drawable.location_entertainment_48;
								} else if (item.getCategory().trim().equalsIgnoreCase("6")) {
									locationIconId = R.drawable.location_cafe_48;
								}
								mMap.setOnInfoWindowClickListener(myself);
								Marker aMarker = mMap.addMarker(new MarkerOptions().position(latlngItem)
																	.icon(BitmapDescriptorFactory.fromResource(locationIconId))
																	.title(item.getNamePlace())
																	.snippet(item.getAddressPlace()));
								markerMap.put(aMarker, item);
							}
							mListPlaceAdapter = new ListViewPlaceAdapter(getActivity(), listPlaceNearbyPlace);
							lvPlaceNearbyPlace.setAdapter(mListPlaceAdapter);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						stopProgress();
					}
				}, 
				new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if( error instanceof NetworkError) {
						} else if( error instanceof ServerError) {
						} else if( error instanceof AuthFailureError) {
						} else if( error instanceof ParseError) {
						} else if( error instanceof NoConnectionError) {
						} else if( error instanceof TimeoutError) {
						}

						stopProgress();
						showToast(error.getMessage());
					}
				});
		jArrRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jArrRequest.setTag(TAG_REQUEST);
		mVolleyRequest.add(jArrRequest);
	}
    private void showProgress() {
		mProgress = ProgressDialog.show(getActivity(), "", "Loading...");
	}
	
	private void stopProgress() {
		mProgress.cancel();
	}
	
	private void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.mapPlaceDetail);
		if(f != null){
			getActivity().getSupportFragmentManager().beginTransaction().remove(f).commit();
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnFindNearby:
			if(llNearbyPlace.getVisibility() == View.GONE){
				llNearbyPlace.setVisibility(View.VISIBLE);
				mVolleyRequest = Volley.newRequestQueue(getActivity());
				tvNearbyPlace.setText(getResources().getString(R.string.promotions_places_nearby_this_place) + " " + placeItem.getNamePlace());
				makeHttpRequestNearbyPlace();
				showProgress();
			}else{
				llNearbyPlace.setVisibility(View.GONE);
			}
			break;

		case R.id.btnGetDirection:
			LatLng fromPosition = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
			LatLng toPosition = latlngPlace;
			
			GMapV2Direction md = new GMapV2Direction();

			Document doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);
			ArrayList<LatLng> directionPoint = md.getDirection(doc);
			PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);

			for(int i = 0 ; i < directionPoint.size() ; i++) {          
			rectLine.add(directionPoint.get(i));
			}

			mMap.addPolyline(rectLine);
			break;
			
		default:
			break;
		}
	}
	
	@Override
	public void onInfoWindowClick(Marker arg0) {
		Place item = markerMap.get(arg0);
		Bundle bundle = new Bundle();
		bundle.putSerializable("placeitem", item);
		FragmentPlaceDetail fragPlaceDetail = new FragmentPlaceDetail();
		fragPlaceDetail.setArguments(bundle);
		FragmentManager fragManager1 = getFragmentManager();
		FragmentTransaction fragTransaction1 = fragManager1.beginTransaction();
		fragTransaction1.replace(R.id.frameContent, fragPlaceDetail)
						.addToBackStack(null)
						.commit();
	}

}
	