package android.tgudapp.ui;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.tgudapp.model.CardInfomation;
import android.tgudapp.utils.ComplexPreferences;
import android.tgudapp.utils.HttpClient;
import android.tgudapp.utils.TGUDApplication;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoActivity extends Activity {
	private TGUDApplication tgudApplication;
	private Uri uri;
	private String Url = "http://thegioiuudai.com.vn/apps/server.php/member/bill/upload";
	private String memberId;
	File mImageFile;
	String[] params;
	private String imageBlob = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		tgudApplication = (TGUDApplication)this.getApplication();
		ComplexPreferences pref = tgudApplication.getComplexPreference();
        if(pref != null){
        	CardInfomation cardInfo = pref.getObject(CardInfomation.CARDINFORMATION, CardInfomation.class);
        	if(cardInfo != null && cardInfo.getResponseCode().equalsIgnoreCase("0")){
        		memberId = "153";
        	}
        }
		uri = getIntent().getData();
		imageBlob = getIntent().getStringExtra("IMAGE_BLOB");
        setUpActionBar();
        ImageView photoView = (ImageView) findViewById(R.id.imgPhoto);
        photoView.setImageURI(uri);
        mImageFile = new File(uri.toString());
        Log.d("Image File path", mImageFile.toString());
	}
	private void setUpActionBar(){
		getActionBar().setTitle(R.string.send_order);
		
	}
	public void backPress(View view){
		onBackPressed();
	}
	
	public void takePicture(View view){
		Intent iTakePhoto = new Intent(PhotoActivity.this, TakeOrderActivity.class);
		startActivity(iTakePhoto);
	}
	public void uploadBuild(View view){
		UploadBuildRequest upload = new UploadBuildRequest();
        params = new String[]{Url,memberId,mImageFile.toString()};
        upload.execute(params);
	}
	private class UploadBuildRequest extends AsyncTask<String, Void, String>{
		
		@Override
		protected String doInBackground(String... params) {
			Log.v("asdasd","another chimnon upload");
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	        String fileName = "TGUD_"+timeStamp+".jpg";
			String url = params[0];
			String param1 = params[1];
			String param3 = params[2];
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap bitmap = BitmapFactory.decodeFile(param3, options);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, baos);
			try {
				HttpClient client = new HttpClient(url);
				client.connectForMultipart();
				client.addFormPart("memberId", param1);
				client.addFormPart("imageBlob", imageBlob);
				client.addFilePart("image", fileName, baos.toByteArray());
				client.finishMultipart();
				String data = client.getResponse();
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String data) {
			// TODO Auto-generated method stub
//			if(result.equalsIgnoreCase("0")){
//				Toast.makeText(PhotoActivity.this, result, Toast.LENGTH_LONG).show();
//			}else{
//				Toast.makeText(PhotoActivity.this, "FAIL", Toast.LENGTH_LONG).show();
//			}
			Log.d("response upload", data);
		}
		
	}
}
