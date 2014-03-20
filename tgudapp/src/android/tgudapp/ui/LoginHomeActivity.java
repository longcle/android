package android.tgudapp.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.google.gson.Gson;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.InputType;
import android.tgudapp.model.CardInfomation;
import android.tgudapp.service.DeviceRegistrar;
import android.tgudapp.service.GetLogin;
import android.tgudapp.service.GetPlaceByCity;
import android.tgudapp.utils.ComplexPreferences;
import android.tgudapp.utils.TGUDApplication;
import android.tgudapp.utils.Util;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginHomeActivity extends Activity implements OnClickListener {
	private TGUDApplication tgudApplication;
	private final String TAG = "LoginHomeActivity";
	private EditText txtNumberCardEmail, txtDateOfBirth;
	private Button btnLogin, btnCallHotline;
	private TextView tvBreakLogin;
	SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_home);
        tgudApplication = (TGUDApplication)this.getApplication();
        if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
        //Get DeviceID
        String deviceId = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceId == null) {
        	deviceId = android.os.Build.SERIAL;
        }
        Log.d("deviceId",deviceId);
        //Send device ID to server
        registerDevice(deviceId);
        // set font type for this activity
        final Typeface mFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "SFUHelveticaLight.ttf"); 
		final ViewGroup mContainer = (ViewGroup)findViewById(android.R.id.content).getRootView();
		Util.setAppFont(mContainer, mFont);
		//setup view
        txtNumberCardEmail = (EditText)findViewById(R.id.txtNumberCardEmail);
        txtDateOfBirth = (EditText)findViewById(R.id.txtDateOfBirth);
        txtDateOfBirth.setInputType(InputType.TYPE_NULL);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnCallHotline = (Button)findViewById(R.id.btnCallHotline);
        tvBreakLogin = (TextView)findViewById(R.id.tvBreakLogin);
        //get shared preferend card info login
        ComplexPreferences pref = tgudApplication.getComplexPreference();
        if(pref != null){
        	CardInfomation cardInfo = pref.getObject(CardInfomation.CARDINFORMATION, CardInfomation.class);
        	if(cardInfo != null && cardInfo.getResponseCode().equalsIgnoreCase("0")){
        		String numberCardOrEmailValue = cardInfo.getCardNumberOrEmail();
        		Log.d("LoginHomeActivity", numberCardOrEmailValue);
            	String dateOfBirthValue = cardInfo.getDateOfBirth();
            	Log.d("LoginHomeActivity", dateOfBirthValue);
            	txtNumberCardEmail.setText(numberCardOrEmailValue);
            	txtDateOfBirth.setText(dateOfBirthValue);
            	Intent iMainActivity = new Intent(LoginHomeActivity.this, MainActivity.class);
    			startActivity(iMainActivity);
        	}
        }
        // set onclicked view
        txtDateOfBirth.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnCallHotline.setOnClickListener(this);
        tvBreakLogin.setOnClickListener(this);
        
    }
    
    Handler handlerResponseCardInfo = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			CardInfomation cardInfo;
			cardInfo = (CardInfomation)msg.getData().get(CardInfomation.CARDINFORMATION);
			if(cardInfo == null){
//				new Process
			}else{
				//login success
				if(cardInfo.getResponseCode().equalsIgnoreCase("0")){
					Toast.makeText(LoginHomeActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
					// save info login into share preference
					ComplexPreferences pref = tgudApplication.getComplexPreference();
					if(pref != null){
						pref.putObject(CardInfomation.CARDINFORMATION, cardInfo);
						pref.commit();
					}else{
						Log.e(TAG, "Preference is null");
					}
					Intent iMainActivity = new Intent(LoginHomeActivity.this, MainActivity.class);
					startActivity(iMainActivity);
				}else{
					// login fail
					if(cardInfo.getResponseCode().equalsIgnoreCase("1")){
						Toast.makeText(LoginHomeActivity.this, R.string.login_wrong_info, Toast.LENGTH_SHORT).show();
					}else if (cardInfo.getResponseCode().equalsIgnoreCase("2")) {
						Toast.makeText(LoginHomeActivity.this, R.string.info_not_availeble, Toast.LENGTH_SHORT).show();
					}else if (cardInfo.getResponseCode().equalsIgnoreCase("3")) {
						Toast.makeText(LoginHomeActivity.this, R.string.card_not_access, Toast.LENGTH_SHORT).show();
					}
				}
			}
			
		}
    	
    };
    Calendar mCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dateListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mCalendar.set(Calendar.YEAR, year);
			mCalendar.set(Calendar.MONTH, monthOfYear);
			mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateTxtDateOfBirth();
		}
	};
	
	private void updateTxtDateOfBirth(){
		String mFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(mFormat, Locale.US);
		txtDateOfBirth.setText(sdf.format(mCalendar.getTime()));
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_home, menu);
        return true;
    }


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.txtDateOfBirth:
			new DatePickerDialog(
					LoginHomeActivity.this, 
					dateListener, 
					mCalendar.get(Calendar.YEAR), 
					mCalendar.get(Calendar.MONTH), 
					mCalendar.get(Calendar.DAY_OF_MONTH)).show();
			break;
		
		case R.id.btnLogin:
			String cardNumberOrEmailValue = txtNumberCardEmail.getText().toString();
			String dateOfBirthValue = txtDateOfBirth.getText().toString();
//			String cardNumberOrEmailValue = "00001463";
//			String dateOfBirthValue = "28/05/1995";
			ConnectivityManager connectMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connectMgr.getActiveNetworkInfo();
			if(netInfo != null && netInfo.isConnected()){
				new GetLogin(handlerResponseCardInfo, cardNumberOrEmailValue, dateOfBirthValue).run();
			}else{
				Toast.makeText(LoginHomeActivity.this, "Your device is not connect network", Toast.LENGTH_LONG).show();
			}
			
			break;
		case R.id.tvBreakLogin:
			Intent iMainActivity = new Intent(LoginHomeActivity.this, MainActivity.class);
			startActivity(iMainActivity);
			break;
		case R.id.btnCallHotline:
			AlertDialog.Builder builder = new Builder(LoginHomeActivity.this);
			builder.setTitle("Call");
			builder.setMessage(R.string.do_u_want_call);
			builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent iCall = new Intent(Intent.ACTION_CALL);
					iCall.setData(Uri.parse("tel:0995393933"));
					startActivity(iCall);
				}
			});
			builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			AlertDialog dialogCall = builder.create();
			dialogCall.show();
			break;
		default:
			break;
		}
	}
    
	private void registerDevice(String deviceId)
	{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		ConnectivityManager connectMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connectMgr.getActiveNetworkInfo();
		if(netInfo != null && netInfo.isConnected()){
			new DeviceRegistrar(tgudApplication, deviceId).run();
		}else{
			Toast.makeText(LoginHomeActivity.this, "Your device is not connect network", Toast.LENGTH_LONG).show();
		}
	}
}
