package android.tgudapp.utils;

import android.app.Application;

public class TGUDApplication extends Application {
    private static final String TAG = "ObjectPreference";
    private ComplexPreferences complexPrefenreces = null;

	@Override
	public void onCreate() {
	    super.onCreate();
	    complexPrefenreces = ComplexPreferences.getComplexPreferences(getBaseContext(), "TGUDPreferenceKey", MODE_PRIVATE);
	    android.util.Log.i(TAG, "Preference Created.");
	}
	
	public ComplexPreferences getComplexPreference() {
	    if(complexPrefenreces != null) {
	        return complexPrefenreces;
	    }
	    return null;
	} 
}
