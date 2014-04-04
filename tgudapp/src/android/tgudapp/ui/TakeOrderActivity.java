package android.tgudapp.ui;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.tgudapp.listener.CameraFragmentListener;
import android.tgudapp.model.CardInfomation;
import android.tgudapp.ui.fragment.CameraFragment;
import android.tgudapp.ui.fragment.PhotoFragment;
import android.tgudapp.utils.ComplexPreferences;
import android.tgudapp.utils.HttpClient;
import android.tgudapp.utils.TGUDApplication;
import android.tgudapp.utils.Util;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.tgud.R;

public class TakeOrderActivity extends Activity implements CameraFragmentListener {
	public static final String TAG = "TGUD/TakeOrderActivity";
	private static final int PICTURE_QUALITY = 90;
	private String imageBlob = "";
	private String Url = "http://thegioiuudai.com.vn/apps/server.php/member/bill/upload";
	private String memberId;
	File mImageFile;
	String[] params;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_order);
		setUpActionBar();
		Typeface mFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "SFUHelveticaLight.ttf"); 
		ViewGroup mContainer = (ViewGroup)findViewById(android.R.id.content).getRootView();
		Util.setAppFont(mContainer, mFont);
		
		TGUDApplication tgudApplication = (TGUDApplication) getApplication();
		ComplexPreferences pref = tgudApplication.getComplexPreference();
        if(pref != null){
        	CardInfomation cardInfo = pref.getObject(CardInfomation.CARDINFORMATION, CardInfomation.class);
        	if(cardInfo != null && cardInfo.getResponseCode().equalsIgnoreCase("0")){
        		memberId = cardInfo.getMemberId();
        	}
        }
	}
	private void setUpActionBar(){
		getActionBar().setTitle(R.string.send_order);
		
	}
	public void backPress(View view){
		onBackPressed();
	}
	public void sendOrder(View view){
		if (memberId == null) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Bạn chưa đăng nhập");
			alert.setMessage("Vui lòng nhập số điện thoại");

			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				UploadBuildRequest upload = new UploadBuildRequest();
		        params = new String[]{Url,value,""};
		        upload.execute(params);
			  }
			});

			alert.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			alert.show();
		} else {
			UploadBuildRequest upload = new UploadBuildRequest();
	        params = new String[]{Url,memberId,""};
	        upload.execute(params);
		}
		
		
//		Toast.makeText(this, R.string.please_take_photo_build, Toast.LENGTH_SHORT).show();
	}
	/**
     * The user wants to take a picture.
     *
     * @param view
     */
    public void takePicture(View view) {
        view.setEnabled(false);

        CameraFragment fragment = (CameraFragment) getFragmentManager().findFragmentById(
            R.id.fragmentCamera
        );

        fragment.takePicture();
    }
	@Override
	public void onCameraError() {
		Toast.makeText(
	            this,
	            getString(R.string.toast_error_camera_preview),
	            Toast.LENGTH_SHORT
	        ).show();

	        finish();
	}
	@Override
	public void onPictureTaken(Bitmap bitmap) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
	        byte[] b = baos.toByteArray();
	        imageBlob = Base64.encodeToString(b, Base64.DEFAULT);
		} catch (Exception e) {
			showUploadResultToast("Gửi hoá đơn không thành công.");
		}
		
		
//		File mediaStorageDir = new File(
//	            Environment.getExternalStoragePublicDirectory(
//	                Environment.DIRECTORY_PICTURES
//	            ),
//	            getString(R.string.app_name)
//	    );
//		if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                showSavingPictureErrorToast();
//                return;
//            }
//        }
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile = new File(
//            mediaStorageDir.getPath() + File.separator + "TGUD_"+ timeStamp + ".jpg"
//        );
//       
//        try {
//            FileOutputStream stream = new FileOutputStream(mediaFile);
//            bitmap.compress(CompressFormat.JPEG, PICTURE_QUALITY, stream);
//        } catch (IOException exception) {
//            showSavingPictureErrorToast();
//
//            Log.w(TAG, "IOException during saving bitmap", exception);
//            return;
//        }
//
//        MediaScannerConnection.scanFile(
//            this,
//            new String[] { mediaFile.toString() },
//            new String[] { "image/jpeg" },
//            null
//        );
//        Log.d("File image Path", mediaFile.toString());
        
//        Intent intent = new Intent(this, PhotoActivity.class);
//        intent.setData(Uri.fromFile(mediaFile));
//        intent.putExtra("IMAGE_BLOB", imageBlob);
//        startActivity(intent);
//
//        finish();
	}
	 private void showSavingPictureErrorToast() {
	        Toast.makeText(this, getText(R.string.toast_error_save_picture), Toast.LENGTH_SHORT).show();
	    }
	 
	 private void showUploadResultToast(String result) {
		 Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	 }
	
	 
	 private class UploadBuildRequest extends AsyncTask<String, Void, String>{
			
			@Override
			protected String doInBackground(String... params) {
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		        String fileName = "TGUD_"+timeStamp+".png";
				String url = params[0];
				String param1 = params[1];
				String param3 = params[2];
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//				Bitmap bitmap = BitmapFactory.decodeFile(param3, options);
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				bitmap.compress(CompressFormat.JPEG, 100, baos);
				try {
					HttpClient client = new HttpClient(url);
					client.connectForMultipart();
					client.addFormPart("memberId", param1);
					client.addFormPart("imageBlob", imageBlob);
					client.addFormPart("fileType","jpg");
//					client.addFilePart("image", fileName, baos.toByteArray());
					client.finishMultipart();
					String data = client.getResponse();
				} catch (Exception e) {
				}
				return null;
			}

			@Override
			protected void onPostExecute(String data) {
				// TODO Auto-generated method stub
				showUploadResultToast("Gửi hoá đơn thành công.");
//				if (data != null) {
//					Log.v("data","daaaaaaatttttaaaaaaaa = " + data);
//					if(data.equalsIgnoreCase("0")){
//						showUploadResultToast("Gửi hoá đơn thành công.");
//					}else{
//						showUploadResultToast("Gửi hoá đơn không thành công.");
//					}	
//				}
			}
			
		}
}
