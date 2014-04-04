package android.tgudapp.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.meetme.android.horizontallistview.HorizontalListView;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.tgudapp.adapter.HorizontalListPlaceAdapter;
import android.tgudapp.adapter.PagerBannerAdapter;
import android.tgudapp.model.Banner;
import android.tgudapp.model.Place;
import android.tgudapp.service.DataParser;
import android.tgudapp.service.DataRepository;
import android.tgudapp.toolbox.DiskBitmapCache;
import android.tgudapp.ui.MainActivity;
import com.tgud.R;
import android.tgudapp.ui.TakeOrderActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class FragmentMain extends Fragment implements OnClickListener {
	public static final String PLACEITEM = "placeitem";
	private ViewPager mPagerBanner;
	private PagerBannerAdapter mPagerBannerAdapter;
	private HorizontalListView hlvFood, hlvTravel, hlvEntertainment, hlvHealth, hlvCoffeeScream, hlvShopping;
//	private List<Place> listFood, listTravel, listEntertainment, listHealth, listCoffeeScream, listShopping;
//	private List<Place> listPlace;
	private RelativeLayout rlFood, rlTravel, rlEntertainment, rlHealth, rlCoffeeScream, rlShopping;
	private String cityValueSelected;
	private LinearLayout llPromotionPlace, llTakeOrder, llMarkCharge;
	private RequestQueue mRequestQueue;
	private JsonArrayRequest jsonArrRequest;
	private JsonArrayRequest jsonBannerRequest;
	private List<Banner> listBanner;
	private ProgressDialog mProgress;
	private ImageLoader mImageLoader;
	private HorizontalListPlaceAdapter mHorizontalListPlaceAdapter;
	private final String TAG_REQUEST = "MY_TAG";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setUpActionBar();
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		
		if(getArguments().getString(MainActivity.CITY_VALUE_SELECTED) == null){
			if(savedInstanceState != null){
				cityValueSelected = savedInstanceState.getString("abc");
			}
		}else{
			cityValueSelected = getArguments().getString(MainActivity.CITY_VALUE_SELECTED);
		}
		Log.d("FragmentMain", "==========>" + cityValueSelected);
		
		// Setup view footer
		llPromotionPlace = (LinearLayout)view.findViewById(R.id.llPromotionPlace);
		llPromotionPlace.setOnClickListener(this);
		llTakeOrder = (LinearLayout)view.findViewById(R.id.llTakeOrder);
		llTakeOrder.setOnClickListener(this);
		llMarkCharge = (LinearLayout)view.findViewById(R.id.llMarkCharge);
		llMarkCharge.setOnClickListener(this);
		// setup view category
		rlFood = (RelativeLayout)view.findViewById(R.id.rlFood);
		rlCoffeeScream = (RelativeLayout)view.findViewById(R.id.rlCoffeeScream);
		rlEntertainment = (RelativeLayout)view.findViewById(R.id.rlEntertainment);
		rlHealth = (RelativeLayout)view.findViewById(R.id.rlHealth);
		rlShopping = (RelativeLayout)view.findViewById(R.id.rlShopping);
		rlTravel = (RelativeLayout)view.findViewById(R.id.rlTravel);
		// Setup view Horizontal Listview
		hlvFood = (HorizontalListView)view.findViewById(R.id.hlvFood);
		hlvCoffeeScream = (HorizontalListView)view.findViewById(R.id.hlvCoffeScream);
		hlvEntertainment = (HorizontalListView)view.findViewById(R.id.hlvEntertainment);
		hlvHealth = (HorizontalListView)view.findViewById(R.id.hlvHealth);
		hlvShopping = (HorizontalListView)view.findViewById(R.id.hlvShopping);
		hlvTravel = (HorizontalListView)view.findViewById(R.id.hlvTravel);
		
		mPagerBanner = (ViewPager)view.findViewById(R.id.pagerBanner);
		mRequestQueue = Volley.newRequestQueue(getActivity());
		makeHttpBannerRequest();
		int MAX_CACHE_SIZE = 1000000;
		mImageLoader = new ImageLoader(mRequestQueue, new DiskBitmapCache(getActivity().getCacheDir(), MAX_CACHE_SIZE));
		makeHttpRequest();
		return view;
	}
	private void makeHttpBannerRequest(){
		String Url = "http://thegioiuudai.com.vn/apps/server.php/site/banners";
		jsonBannerRequest = new JsonArrayRequest(
				Url, 
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						DataParser parser = new DataParser();
						listBanner = parser.parserListBannerResponse(response);
						mPagerBannerAdapter = new PagerBannerAdapter(getChildFragmentManager(), listBanner);
						mPagerBanner.setAdapter(mPagerBannerAdapter);
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
						showToast(error.getMessage());
					}
				});
		jsonBannerRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonBannerRequest.setTag(TAG_REQUEST);
		mRequestQueue.add(jsonBannerRequest);
	}
	private void makeHttpRequest() {
		if (DataRepository.listPlace == null) {
			DataRepository.listPlace = new ArrayList<Place>();
			DataRepository.listFood = new ArrayList<Place>();
			DataRepository.listTravel = new ArrayList<Place>();
			DataRepository.listShopping = new ArrayList<Place>();
			DataRepository.listHealth = new ArrayList<Place>();
			DataRepository.listEntertainment = new ArrayList<Place>();
			DataRepository.listCoffeeScream = new ArrayList<Place>();
		}
		
		if (DataRepository.listPlace.size() <= 0) {
			showProgress();
			String URL = "http://thegioiuudai.com.vn/apps/server.php/merchant/list";
			Uri.Builder builder = Uri.parse(URL).buildUpon();
			builder.appendQueryParameter("city", cityValueSelected);
			jsonArrRequest = new JsonArrayRequest(
					builder.toString(), 
					new Response.Listener<JSONArray>() {

						@Override
						public void onResponse(JSONArray response) {
							try {
								DataParser parser = new DataParser();
								DataRepository.listPlace = parser.parserListPlaceResponse(response);
//								DataRepository.listFood = new ArrayList<Place>();
//								DataRepository.listTravel = new ArrayList<Place>();
//								DataRepository.listShopping = new ArrayList<Place>();
//								DataRepository.listHealth = new ArrayList<Place>();
//								DataRepository.listEntertainment = new ArrayList<Place>();
//								DataRepository.listCoffeeScream = new ArrayList<Place>();
								for(int i = 0; i< DataRepository.listPlace.size(); i++){
									Place item = DataRepository.listPlace.get(i);
									if(item.getCategory().equalsIgnoreCase("1")){
										DataRepository.listFood.add(item);
									}else if (item.getCategory().equalsIgnoreCase("2")) {
										DataRepository.listTravel.add(item);
									}else if (item.getCategory().equalsIgnoreCase("3")) {
										DataRepository.listShopping.add(item);
									}else if (item.getCategory().equalsIgnoreCase("4")) {
										DataRepository.listHealth.add(item);
									}else if (item.getCategory().equalsIgnoreCase("5")) {
										DataRepository.listEntertainment.add(item);
									}else if (item.getCategory().equalsIgnoreCase("6")) {
										DataRepository.listCoffeeScream.add(item);
									}
								}
								showPlaceList();
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
			jsonArrRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			jsonArrRequest.setTag(TAG_REQUEST);
			mRequestQueue.add(jsonArrRequest);
		} else {
			showPlaceList();
		}
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
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		// TODO Auto-generated method stub
//		super.onSaveInstanceState(outState);
//		outState.putString("abc", cityValueSelected);
//	}

	@Override
	public void onStart() {
		ConnectivityManager connectMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connectMgr.getActiveNetworkInfo();
		if(netInfo != null && netInfo.isConnected()){
//			new GetPlaceByCity(handlerResponse, cityValueSelected).run();
		}else{
//			Toast.makeText(getActivity(), "Your device is not connect network", Toast.LENGTH_LONG).show();
		}
		super.onStart();
	}

	

	private void setUpActionBar(){
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActivity().getActionBar().setDisplayShowTitleEnabled(false);
	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.llPromotionPlace:
			FragmentManager fragManager1 = getFragmentManager();
			FragmentTransaction fragTransaction1 = fragManager1.beginTransaction();
			FragmentNearBy fragNearBy = new FragmentNearBy();
			fragTransaction1.replace(R.id.frameContent, fragNearBy)
							.addToBackStack(null)
							.commit();
			break;
		case R.id.llTakeOrder:
			Intent iTakeOrder = new Intent(getActivity(), TakeOrderActivity.class);
			startActivity(iTakeOrder);
			break;
		case R.id.llMarkCharge:
			FragmentManager fragManager = getFragmentManager();
			FragmentTransaction fragTransaction = fragManager.beginTransaction();
			FragmentCheckCharge fragCheckCharge = new FragmentCheckCharge();
			fragTransaction	.replace(R.id.frameContent, fragCheckCharge)
							.addToBackStack(null)
							.commit();
		default:
			break;
		}
	}
	
	private void showPlaceList() {
		if(DataRepository.listFood.size() != 0){
			rlFood.setVisibility(View.VISIBLE);
			hlvFood.setAdapter(new HorizontalListPlaceAdapter(getActivity(), DataRepository.listFood,mImageLoader));
			hlvFood.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Place itemAdapter;
					itemAdapter = (Place)parent.getItemAtPosition(position);
					Bundle bundle = new Bundle();
					bundle.putSerializable(PLACEITEM, itemAdapter);
					FragmentPlaceDetail fragPlaceDetail = new FragmentPlaceDetail();
					fragPlaceDetail.setArguments(bundle);
					FragmentManager fragManager1 = getFragmentManager();
					FragmentTransaction fragTransaction1 = fragManager1.beginTransaction();
					fragTransaction1.replace(R.id.frameContent, fragPlaceDetail)
									.addToBackStack(null)
									.commit();
				}
				
			});
		}else{
			rlFood.setVisibility(View.GONE);
		}
		if(DataRepository.listTravel.size() != 0){
			rlTravel.setVisibility(View.VISIBLE);
			hlvTravel.setAdapter(new HorizontalListPlaceAdapter(getActivity(), DataRepository.listTravel,mImageLoader));
			hlvTravel.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Place itemAdapter;
					itemAdapter = (Place)parent.getItemAtPosition(position);
					Bundle bundle = new Bundle();
					bundle.putSerializable(PLACEITEM, itemAdapter);
					FragmentPlaceDetail fragPlaceDetail = new FragmentPlaceDetail();
					fragPlaceDetail.setArguments(bundle);
					FragmentManager fragManager6 = getFragmentManager();
					FragmentTransaction fragTransaction6 = fragManager6.beginTransaction();
					fragTransaction6.replace(R.id.frameContent, fragPlaceDetail)
									.addToBackStack(null)
									.commit();
				}
				
			});
		}else{
			rlTravel.setVisibility(View.GONE);
		}
		if(DataRepository.listShopping.size() != 0){
			rlShopping.setVisibility(View.VISIBLE);
			hlvShopping.setAdapter(new HorizontalListPlaceAdapter(getActivity(), DataRepository.listShopping,mImageLoader));
			hlvShopping.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Place itemAdapter;
					itemAdapter = (Place)parent.getItemAtPosition(position);
					Bundle bundle = new Bundle();
					bundle.putSerializable(PLACEITEM, itemAdapter);
					FragmentPlaceDetail fragPlaceDetail = new FragmentPlaceDetail();
					fragPlaceDetail.setArguments(bundle);
					FragmentManager fragManager5 = getFragmentManager();
					FragmentTransaction fragTransaction5 = fragManager5.beginTransaction();
					fragTransaction5.replace(R.id.frameContent, fragPlaceDetail)
									.addToBackStack(null)
									.commit();
				}
				
			});
		}else{
			rlShopping.setVisibility(View.GONE);
		}
		if(DataRepository.listHealth.size() != 0){
			rlHealth.setVisibility(View.VISIBLE);
			hlvHealth.setAdapter(new HorizontalListPlaceAdapter(getActivity(), DataRepository.listHealth,mImageLoader));
			hlvHealth.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Place itemAdapter;
					itemAdapter = (Place)parent.getItemAtPosition(position);
					Bundle bundle = new Bundle();
					bundle.putSerializable(PLACEITEM, itemAdapter);
					FragmentPlaceDetail fragPlaceDetail = new FragmentPlaceDetail();
					fragPlaceDetail.setArguments(bundle);
					FragmentManager fragManager4 = getFragmentManager();
					FragmentTransaction fragTransaction4 = fragManager4.beginTransaction();
					fragTransaction4.replace(R.id.frameContent, fragPlaceDetail)
									.addToBackStack(null)
									.commit();
				}
				
			});
		}else{
			rlHealth.setVisibility(View.GONE);
		}
		if(DataRepository.listEntertainment.size() != 0){
			rlEntertainment.setVisibility(View.VISIBLE);
			hlvEntertainment.setAdapter(new HorizontalListPlaceAdapter(getActivity(), DataRepository.listEntertainment,mImageLoader));
			hlvEntertainment.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Place itemAdapter;
					itemAdapter = (Place)parent.getItemAtPosition(position);
					Bundle bundle = new Bundle();
					bundle.putSerializable(PLACEITEM, itemAdapter);
					FragmentPlaceDetail fragPlaceDetail = new FragmentPlaceDetail();
					fragPlaceDetail.setArguments(bundle);
					FragmentManager fragManager3 = getFragmentManager();
					FragmentTransaction fragTransaction3 = fragManager3.beginTransaction();
					fragTransaction3.replace(R.id.frameContent, fragPlaceDetail)
									.addToBackStack(null)
									.commit();
				}
				
			});
		}else{
			rlEntertainment.setVisibility(View.GONE);
		}
		if(DataRepository.listCoffeeScream.size() != 0){
			rlCoffeeScream.setVisibility(View.VISIBLE);
			hlvCoffeeScream.setAdapter(new HorizontalListPlaceAdapter(getActivity(), DataRepository.listCoffeeScream,mImageLoader));
			hlvCoffeeScream.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Place itemAdapter;
					itemAdapter = (Place)parent.getItemAtPosition(position);
					Bundle bundle = new Bundle();
					bundle.putSerializable(PLACEITEM, itemAdapter);
					FragmentPlaceDetail fragPlaceDetail = new FragmentPlaceDetail();
					fragPlaceDetail.setArguments(bundle);
					FragmentManager fragManager2 = getFragmentManager();
					FragmentTransaction fragTransaction2 = fragManager2.beginTransaction();
					fragTransaction2.replace(R.id.frameContent, fragPlaceDetail)
									.addToBackStack(null)
									.commit();
				}
				
			});
		}else{
			rlCoffeeScream.setVisibility(View.GONE);
		}
	}

	
	
}
