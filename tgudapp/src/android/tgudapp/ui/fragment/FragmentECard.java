package android.tgudapp.ui.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tgud.R;
import android.tgudapp.utils.ComplexPreferences;
import android.tgudapp.utils.TGUDApplication;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentECard extends Fragment {
	private final String TAG = "FragmentECard";
	private int promotionPercent = 0;
	
	public void setPromotionPercent(int promotionPercent) {
		this.promotionPercent = promotionPercent;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ecard, container, false);
		TextView txtCardNumber = (TextView)view.findViewById(R.id.txtCardNumber);
		TGUDApplication tgudApplication = (TGUDApplication) getActivity().getApplication();
		ComplexPreferences pref = tgudApplication.getComplexPreference();
		String cardNumber = pref.getObject("E_CARD_NUMBER", String.class);
		txtCardNumber.setText("Số thẻ: " + cardNumber);
		TextView txtPromotionPercent = (TextView)view.findViewById(R.id.txtPromotionPercent);
		txtPromotionPercent.setText("Giảm giá " + promotionPercent + "%");
		return view;
	}
	
	@Override
	public void onPause() {
//		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onPause();
	}
}
