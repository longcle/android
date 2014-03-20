package android.tgudapp.ui.fragment;

import java.util.List;

import org.json.JSONArray;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract.Constants;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.tgudapp.adapter.ListViewPlaceAdapter;
import android.tgudapp.maps.GPSTracker;
import android.tgudapp.model.ListPlace;
import android.tgudapp.model.Place;
import android.tgudapp.service.DataParser;
import android.tgudapp.service.GetNearbyPlace;
import android.tgudapp.ui.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class FragmentNearBy extends Fragment {

	private GoogleMap mMap;
	private GPSTracker gps;
	private LatLng currentLocation ;
	private double lat;
	private double lng;
	private List<Place> listPlaceNearby;
	private ListView lvPlaceNearby;
	private ListViewPlaceAdapter lvPlaceNearbyAdapter;
	private ProgressDialog mProgress;
	private RequestQueue mRequestQueue;
	private JsonArrayRequest jArrRequest;
	private final String TAG_REQUEST = "MY_TAG";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setUpActionBar();
		View v = inflater.inflate(R.layout.fragment_near_by, container, false);
		lvPlaceNearby = (ListView)v.findViewById(R.id.lvPlaceNearBy);
		lvPlaceNearby.setOnItemClickListener(new OnItemClickListener() {

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
		setUpMapIfNeeded();
		return v;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		SupportMapFragment fm = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
		if(fm != null){
			getActivity().getSupportFragmentManager().beginTransaction().remove(fm).commit();
		}
	}

	private void setUpActionBar(){
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().getActionBar().setTitle(R.string.near_by);
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
	public void onResume() {
		setUpMapIfNeeded();
		super.onResume();
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
        	FragmentManager fragManager = getActivity().getSupportFragmentManager();
            mMap = ((SupportMapFragment) fragManager.findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
	}
	
	private void setUpMap() {
////		mMap.setMyLocationEnabled(true);
		gps = new GPSTracker(getActivity());
		if(gps.canGetLocation()){
			// received lattitude, longtitude current position
			lat = gps.getLatitude();
			lng = gps.getLongitude();
			currentLocation = new LatLng(lat, lng);
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 15);
			mMap.animateCamera(cameraUpdate);
			Log.d("Nearby CurrentLocation", "Your Location is - \nLat: " + Double.toString(lat) + "\nLong: " + Double.toString(lat)); 
			// get nearby place at current position
			showProgress();
			mRequestQueue = Volley.newRequestQueue(getActivity());
			makeHttpRequest();
		}else{
			gps.showSettingsAlert();
		}
		
		mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
	}
	
	private void makeHttpRequest() {
		mMap.setMyLocationEnabled(true);
//		Location location = mMap.getMyLocation();
//
//	    if (location != null) {
//	        LatLng myLocation = new LatLng(location.getLatitude(),
//	                location.getLongitude());
//	        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15));
//	        lat = location.getLatitude();
//	        lng = location.getLongitude();
//	        
////	        double radiusInMeters = 1000.0;
////	        int strokeColor = 0xffff0000; //red outline
////	        int shadeColor = 0x44ff0000; //opaque red fill
////
////	        CircleOptions circleOptions = new CircleOptions().center(myLocation).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
////	        Circle mCircle = mMap.addCircle(circleOptions);
////
////	        MarkerOptions markerOptions = new MarkerOptions().position(myLocation);
////	        Marker mMarker = mMap.addMarker(markerOptions);
//	    }
		String URL = "http://thegioiuudai.com.vn/apps/server.php/merchant/nearby";
		Uri.Builder builder = Uri.parse(URL).buildUpon();
		builder.appendQueryParameter("currentLat", Double.toString(lat));
		builder.appendQueryParameter("currentLong", Double.toString(lng));
		builder.appendQueryParameter("distantLimit", "3");
		jArrRequest = new JsonArrayRequest(
				builder.toString(), 
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						try {
							DataParser parser = new DataParser();
							listPlaceNearby = parser.parserListPlaceResponse(response);
							for(int i = 0; i<listPlaceNearby.size(); i++){
								Place item = listPlaceNearby.get(i);
								double latItem = Double.parseDouble(item.getLat());
								double lngItem = Double.parseDouble(item.getLng());
								LatLng latlngItem = new LatLng(latItem, lngItem);
								mMap.addMarker(new MarkerOptions().position(latlngItem)
																	.icon(BitmapDescriptorFactory.fromResource(R.drawable.location48))
																	.title(item.getNamePlace()));
							}
							lvPlaceNearbyAdapter = new ListViewPlaceAdapter(getActivity(), listPlaceNearby);
							lvPlaceNearby.setAdapter(lvPlaceNearbyAdapter);
							
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
		mRequestQueue.add(jArrRequest);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	
}
