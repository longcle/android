package android.tgudapp.ui;



import java.util.ArrayList;
import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.tgudapp.model.CardInfomation;
import android.tgudapp.slidingmenu.SlidingMenuAdapter;
import android.tgudapp.slidingmenu.SlidingMenuEntryItem;
import android.tgudapp.slidingmenu.SlidingMenuItem;
import android.tgudapp.slidingmenu.SlidingMenuSectionItem;
import android.tgudapp.ui.fragment.FragmentListPlace;
import android.tgudapp.ui.fragment.FragmentMain;
import android.tgudapp.ui.fragment.FragmentNearBy;
import android.tgudapp.ui.fragment.FragmentSearch;
import android.tgudapp.ui.fragment.FragmentECard;
import android.tgudapp.utils.ComplexPreferences;
import android.tgudapp.utils.TGUDApplication;
import android.tgudapp.utils.Util;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

public class MainActivity extends SlidingFragmentActivity {
	public final static String CITY_VALUE_SELECTED = "cityValueSelected";
	public final static String CATEGORY_VALUE_SELECTED = "categoryValueSelected";
	private String[] cityArray, cityValues;
	private String cityValueSelected ;
	private String categoryId;
	private FragmentManager fragManger = getSupportFragmentManager();
	private TGUDApplication tgudApplication;
	Bundle bundle;
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tgudApplication = (TGUDApplication)this.getApplication();
		setContentView(R.layout.activity_main);
		setBehindContentView(R.layout.menu_frame);
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		Typeface mFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "SFUHelveticaLight.ttf"); 
		ViewGroup mContainer = (ViewGroup)findViewById(android.R.id.content).getRootView();
		Util.setAppFont(mContainer, mFont);
		
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(true);
		
		// setup list navigation on actionbar
		cityArray = getResources().getStringArray(R.array.cityName);
		cityValues = getResources().getStringArray(R.array.cityValues);
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.cityName, android.R.layout.simple_spinner_dropdown_item);
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ActionBar.OnNavigationListener navListener = new ActionBar.OnNavigationListener() {
			
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				cityValueSelected = cityValues[itemPosition];
				
				FragmentMain fragMain = new FragmentMain();
				bundle = new Bundle();
				bundle.putString(CITY_VALUE_SELECTED, cityValueSelected);
				fragMain.setArguments(bundle);
				fragManger.beginTransaction()
							.replace(R.id.frameContent, fragMain)
							.addToBackStack(null)
							.commit();
				
				return true;
			}
		};
		getActionBar().setListNavigationCallbacks(mSpinnerAdapter, navListener);
		//set view for sliding menu
		setUpListSlidingMenu();
		// setup SlidingMenu		
		SlidingMenu sm = getSlidingMenu();
		sm.setMode(SlidingMenu.LEFT);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.LEFT);
		
	}

	private void setUpListSlidingMenu(){
		ListView lvSlidingMenu = (ListView)findViewById(R.id.lvSlidingMenu);
		List<SlidingMenuItem> smItem = new ArrayList<SlidingMenuItem>();
		smItem.add(new SlidingMenuSectionItem("Category"));
		smItem.add(new SlidingMenuEntryItem(R.drawable.home48, R.string.home));
		smItem.add(new SlidingMenuEntryItem(R.drawable.location48, R.string.near_by));
		smItem.add(new SlidingMenuEntryItem(R.drawable.shopping_48, R.string.shopping));
		smItem.add(new SlidingMenuEntryItem(R.drawable.food_48, R.string.food));
		smItem.add(new SlidingMenuEntryItem(R.drawable.entertainment_48, R.string.entertainment));
		smItem.add(new SlidingMenuEntryItem(R.drawable.travel_48, R.string.travel));
		smItem.add(new SlidingMenuEntryItem(R.drawable.coffee_48, R.string.coffee_scream));
		smItem.add(new SlidingMenuEntryItem(R.drawable.health_48, R.string.health));
		smItem.add(new SlidingMenuEntryItem(R.drawable.star48, R.string.check_charge));
		smItem.add(new SlidingMenuSectionItem("General"));
//		smItem.add(new SlidingMenuEntryItem(R.drawable.setting48, R.string.e_card));
		smItem.add(new SlidingMenuEntryItem(R.drawable.setting48, R.string.setting));
		smItem.add(new SlidingMenuEntryItem(R.drawable.info48, R.string.about));
		smItem.add(new SlidingMenuEntryItem(R.drawable.info48, R.string.logout));
		SlidingMenuAdapter smAdapter = new SlidingMenuAdapter(this, smItem);
		lvSlidingMenu.setAdapter(smAdapter);
		lvSlidingMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 1:
					FragmentMain fragMain = new FragmentMain();
					fragMain.setArguments(bundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, fragMain)
												.commit();
					toggle();
					break;
				case 2:
					getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, new FragmentNearBy())
												.commit();
					toggle();
					break;
				case 3:
					categoryId = "3";
					bundle.putString(CATEGORY_VALUE_SELECTED, categoryId);
					FragmentListPlace fragShopping = new FragmentListPlace();
					fragShopping.setArguments(bundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, fragShopping)
						.commit();
					toggle();
					break;
				case 4:
					categoryId = "1";
					bundle.putString(CATEGORY_VALUE_SELECTED, categoryId);
					FragmentListPlace fragFood = new FragmentListPlace();
					fragFood.setArguments(bundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, fragFood)
						.commit();
					toggle();
					break;
				case 5:
					categoryId = "5";
					bundle.putString(CATEGORY_VALUE_SELECTED, categoryId);
					FragmentListPlace fragEntertainment = new FragmentListPlace();
					fragEntertainment.setArguments(bundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, fragEntertainment)
						.commit();
					toggle();
					break;
				case 6:
					categoryId = "2";
					bundle.putString(CATEGORY_VALUE_SELECTED, categoryId);
					FragmentListPlace fragTravel = new FragmentListPlace();
					fragTravel.setArguments(bundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, fragTravel)
						.commit();
					toggle();
					break;
				case 7:
					categoryId = "6";
					bundle.putString(CATEGORY_VALUE_SELECTED, categoryId);
					FragmentListPlace fragCoffee = new FragmentListPlace();
					fragCoffee.setArguments(bundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, fragCoffee)
						.commit();
					toggle();
					break;
				case 8:
					categoryId = "4";
					bundle.putString(CATEGORY_VALUE_SELECTED, categoryId);
					FragmentListPlace fragHealth = new FragmentListPlace();
					fragHealth.setArguments(bundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, fragHealth)
						.commit();
					toggle();
					break;
				case 13:
					ComplexPreferences pref = tgudApplication.getComplexPreference();
					if(pref != null){
			        	pref.clear();
			            Intent iMainActivity = new Intent(MainActivity.this, LoginHomeActivity.class);
			    		startActivity(iMainActivity);
			        }
					break;
				default:
					break;
				}
			}
		});
	}
//	@Override
//	public void onContentChanged() {
//		Typeface mFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "SFUHelveticaLight.ttf"); 
//		ViewGroup mContainer = (ViewGroup)findViewById(android.R.id.content).getRootView();
//		Util.setAppFont(mContainer, mFont);
//		super.onContentChanged();
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			break;
		case R.id.action_search:
			FragmentSearch fragSearch = new FragmentSearch();
			FragmentTransaction fragTransaction2 = fragManger.beginTransaction();
			fragTransaction2.replace(R.id.frameContent, fragSearch)
							.addToBackStack(null)
							.commit();
			
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	
}
