package android.tgudapp.ui.fragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.tgudapp.model.CardInfomation;
import android.tgudapp.ui.R;
import android.tgudapp.utils.ComplexPreferences;
import android.tgudapp.utils.TGUDApplication;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentCheckCharge extends Fragment {
	private final String TAG = "FragmentCheckCharge";
	private TGUDApplication tgudApplication;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setUpActionBar();
		View view = inflater.inflate(R.layout.fragment_check_charge, container, false);
		TextView tvSpentAmount = (TextView)view.findViewById(R.id.tvSpentAmount);
		TextView tvSavedAmount = (TextView)view.findViewById(R.id.tvSavedAmount);
		TextView tvRedeemed = (TextView)view.findViewById(R.id.tvRedeemedPoint);
		TextView tvRemaining = (TextView)view.findViewById(R.id.tvRemainingPoint);
		tgudApplication = (TGUDApplication)getActivity().getApplication();
		ComplexPreferences pref = tgudApplication.getComplexPreference();
        if(pref != null){
        	CardInfomation cardInfo = pref.getObject(CardInfomation.CARDINFORMATION, CardInfomation.class);
        	if(cardInfo != null && cardInfo.getResponseCode().equalsIgnoreCase("0")){
        		String spentAmount = cardInfo.getSpentAmount();
        		String savedAmount = cardInfo.getSavedAmount();
        		Float spentAmountValue = Float.parseFloat(spentAmount);
        		Float savedAmountValue = Float.parseFloat(savedAmount);
        		tvSpentAmount.setText(getResources().getString(R.string.spent_amount) + " " + cardInfo.getSpentAmount() + " VND");
        		tvSavedAmount.setText(getResources().getString(R.string.saved_amount) + " " + cardInfo.getSavedAmount() + " VND");
        		
        		tvRedeemed.setText(getResources().getString(R.string.redeemed) + " " + cardInfo.getRedeemedPoints());
        		tvRemaining.setText(getResources().getString(R.string.remaining) + " " + cardInfo.getRemainingPoints());
        		
        	}
        }else{
        	Toast.makeText(getActivity(), "Please login with your account", Toast.LENGTH_SHORT).show();
        }
		
		
		return view;
	}
	private void setUpActionBar(){
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().getActionBar().setTitle(R.string.check_charge);
	}
	
}
