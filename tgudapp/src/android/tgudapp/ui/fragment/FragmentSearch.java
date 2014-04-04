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
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.tgudapp.adapter.ListViewPlaceAdapter;
import android.tgudapp.model.Place;
import android.tgudapp.service.DataParser;
import com.tgud.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class FragmentSearch extends Fragment implements OnClickListener{
	private EditText txtSearch;
	private EditText txtCitySearch, txtDistrictSearch;
	private ListView lvPlaceSearch;
	private String[] cityName, cityValue;
	private String[] districtName, districtValue;
	private String cityValueSelected;
	private String districtValueSelected;
	private RequestQueue mVolleyRequest;
	private JsonArrayRequest jArrRequest;
	private List<Place> listPlace;
	private List<Place> listSort;
	private ProgressDialog mProgress;
	private ListViewPlaceAdapter mListPlaceAdapter;
	private final String TAG_REQUEST = "MY_TAG";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setUpActionBar();
		
		View v = inflater.inflate(R.layout.fragment_search, container, false);
		txtSearch = (EditText)v.findViewById(R.id.txtSearch);
		lvPlaceSearch = (ListView)v.findViewById(R.id.lvPlaceSearch);
//		listPlace = new ArrayList<Place>();
//		mListPlaceAdapter = new ListViewPlaceAdapter(getActivity(), listPlace);
//		lvPlaceSearch.setAdapter(mListPlaceAdapter);
		txtCitySearch = (EditText)v.findViewById(R.id.txtCitySearch);
		txtCitySearch.setOnClickListener(this);
		txtDistrictSearch = (EditText)v.findViewById(R.id.txtDistrictSearch);
		txtDistrictSearch.setOnClickListener(this);
		mVolleyRequest = Volley.newRequestQueue(getActivity());
		
		txtSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!txtSearch.getText().toString().equalsIgnoreCase("")){
					listSort = new ArrayList<Place>();
					listSort.clear();
					if (listPlace == null) {
						listPlace = new ArrayList<Place>();
					}
					String textSearch = txtSearch.getText().toString();
					for(int i=0; i<listPlace.size(); i++){
						if(listPlace.get(i).getNamePlace().toUpperCase().toString().contains(textSearch.toUpperCase())){
							listSort.add(listPlace.get(i));
						}
					}
					mListPlaceAdapter = new ListViewPlaceAdapter(getActivity(), listSort);
					lvPlaceSearch.setAdapter(mListPlaceAdapter);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(txtSearch.getText().toString().equalsIgnoreCase("")){
					mListPlaceAdapter = new ListViewPlaceAdapter(getActivity(), listPlace);
					lvPlaceSearch.setAdapter(mListPlaceAdapter);
				}
			}
		});
		lvPlaceSearch.setOnItemClickListener(new OnItemClickListener() {

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
		return v;
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
	private void setUpActionBar(){
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().getActionBar().setTitle(R.string.search);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtCitySearch:
			
			View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_custom_view, null);
			
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setTitle(R.string.choose_city);
			builder.setView(dialogView);
			final AlertDialog cityDialog = builder.create();
			cityDialog.show();
			ListView lvCityDialog = (ListView)dialogView.findViewById(R.id.lvCityDialog);
			cityName = getActivity().getResources().getStringArray(R.array.cityName);
			cityValue = getActivity().getResources().getStringArray(R.array.cityValues);
			ArrayAdapter<String> arrCityAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_single_choice, cityName);
			lvCityDialog.setAdapter(arrCityAdapter);
			lvCityDialog.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					txtDistrictSearch.setText(null);
					String citySelected = (String) parent.getItemAtPosition(position);
					cityValueSelected = cityValue[position];
					Log.d("FragmentSearch", "========>" + cityValueSelected);
					txtCitySearch.setText(citySelected);
					cityDialog.dismiss();
					makeSearchHttpRequest();
					showProgress();
				}
			});
			break;
		case R.id.txtDistrictSearch:
			if(txtCitySearch.getText().toString().length() == 0){
				Toast.makeText(getActivity(), R.string.please_choose_city, Toast.LENGTH_SHORT).show();
			}else{
				View dialogDistrictView = getActivity().getLayoutInflater().inflate(R.layout.dialog_district_view, null);
				
				AlertDialog.Builder builderdistrict = new Builder(getActivity());
				builderdistrict.setTitle(R.string.choose_district);
				builderdistrict.setView(dialogDistrictView);
				final AlertDialog districtDialog = builderdistrict.create();
				districtDialog.show();
				ListView lvDistrictDialog = (ListView)dialogDistrictView.findViewById(R.id.lvDistrictDialog);
				if(cityValueSelected.equalsIgnoreCase("30")){
					districtName = getResources().getStringArray(R.array.district_name_hcm);
					districtValue = getResources().getStringArray(R.array.district_id_hcm);
				}else if (cityValueSelected.equalsIgnoreCase("65")) {
					districtName = getResources().getStringArray(R.array.district_name_nhatrang);
					districtValue = getResources().getStringArray(R.array.district_id_nhatrang);
				}else if (cityValueSelected.equalsIgnoreCase("72")) {
					districtName = getResources().getStringArray(R.array.district_name_hanoi);
					districtValue = getResources().getStringArray(R.array.district_id_hanoi);
				}else if (cityValueSelected.equalsIgnoreCase("74")) {
					districtName = getResources().getStringArray(R.array.district_name_binhthuan);
					districtValue = getResources().getStringArray(R.array.district_id_binhthuan);
				}else if (cityValueSelected.equalsIgnoreCase("73")) {
					districtName = getResources().getStringArray(R.array.district_name_danang);
					districtValue = getResources().getStringArray(R.array.district_id_danang);
				}
				ArrayAdapter<String> arrDistrictAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_single_choice, districtName);
				lvDistrictDialog.setAdapter(arrDistrictAdapter);
				lvDistrictDialog.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						String districtSelected = (String) parent.getItemAtPosition(position);
						districtValueSelected = districtValue[position];
						Log.d("FragmentSearch", "========>" + districtValueSelected);
						txtDistrictSearch.setText(districtSelected);
						districtDialog.dismiss();
						makeSearchDistrictHttpRequest();
						showProgress();
					}
				});
			}
			break;
		default:
			break;
		}
	}
	private void makeSearchHttpRequest() {
		String URL = "http://thegioiuudai.com.vn/apps/server.php/merchant/list";
		Uri.Builder builder = Uri.parse(URL).buildUpon();
		builder.appendQueryParameter("city", cityValueSelected);
//		if(txtDistrictSearch.getText().toString() != null){
//			builder.appendQueryParameter("district", districtValueSelected);
//		}
		jArrRequest = new JsonArrayRequest(
				builder.toString(), 
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						try {
							DataParser parser = new DataParser();
							listPlace = parser.parserListPlaceResponse(response);
							mListPlaceAdapter = new ListViewPlaceAdapter(getActivity(), listPlace);
							lvPlaceSearch.setAdapter(mListPlaceAdapter);
						} catch (Exception e) {
							e.printStackTrace();
						}
						stopProgress();
					}
				}, 
				new Response.ErrorListener() {

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
	private void makeSearchDistrictHttpRequest() {
		String URL = "http://thegioiuudai.com.vn/apps/server.php/merchant/list";
		Uri.Builder builder = Uri.parse(URL).buildUpon();
		builder.appendQueryParameter("city", cityValueSelected);
		builder.appendQueryParameter("district", districtValueSelected);
		jArrRequest = new JsonArrayRequest(
				builder.toString(), 
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						try {
							DataParser parser = new DataParser();
							listPlace = parser.parserListPlaceResponse(response);
							mListPlaceAdapter = new ListViewPlaceAdapter(getActivity(), listPlace);
							lvPlaceSearch.setAdapter(mListPlaceAdapter);
						} catch (Exception e) {
							e.printStackTrace();
						}
						stopProgress();
					}
				}, 
				new Response.ErrorListener() {

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
}
