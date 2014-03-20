package android.tgudapp.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;


import android.content.Context;
import android.text.TextUtils;
import android.tgudapp.model.Place;
import android.tgudapp.toolbox.FadeInImageListener;
import android.tgudapp.ui.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HorizontalListPlaceAdapter extends ArrayAdapter<Place> {
	private Context mContext;
	private List<Place> listPlace;
	private ImageLoader mImageLoader;
	public HorizontalListPlaceAdapter(Context context, List<Place> objects, ImageLoader imageLoader) {
		super(context, 0, objects);
		this.mContext = context;
		this.listPlace = objects;
		this.mImageLoader = imageLoader;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listPlace.size();
	}

	@Override
	public Place getItem(int position) {
		// TODO Auto-generated method stub
		return listPlace.get(position);
	}

	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		ViewHolder holder = null;
		if(v == null){
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.horizontal_listplace_item, null);
			holder = new ViewHolder();
			holder.tvNamePlace = (TextView)v.findViewById(R.id.tvNamePlace);
			holder.imgPlace = (ImageView)v.findViewById(R.id.imgPlace);
			holder.tvAddressPlace = (TextView)v.findViewById(R.id.tvAddressPlace);
			holder.tvPromotionPercent = (TextView)v.findViewById(R.id.tvPromotionPercent);
			v.setTag(holder);
		}else{
			holder = (ViewHolder)v.getTag();
		}
		final Place item = listPlace.get(position);
//		Picasso.with(mContext)
//				.load(item.getImageURL())
//				.resize(150, 140)
//				.placeholder(R.drawable.empty_photo)
//				.error(R.drawable.empty_photo)
//				.into(holder.imgPlace);
//		if(item.getProPercent() != null || item.getProPercent().trim().length()>0){
//			holder.tvPromotionPercent.setText(item.getProPercent()+ "%");
//		}else{
//			holder.tvPromotionPercent.setText("123 %");
//		}
		mImageLoader.get(item.getImageURL(), new FadeInImageListener(holder.imgPlace, mContext));
		String promotionPercent = "0";
		if (item.getProPercent() != "null") {
			promotionPercent = item.getProPercent();
		}
		holder.tvPromotionPercent.setText(promotionPercent + " %");
		holder.tvNamePlace.setText(item.getNamePlace());
		holder.tvAddressPlace.setText(item.getAddressPlace());
		return v;
	}
	@Override
	public int getItemViewType(int position) {
		
		return super.getItemViewType(position);
	}
	private static class ViewHolder{
		private ImageView imgPlace;
		private TextView tvPromotionPercent; 
		private TextView tvNamePlace; 
		private TextView tvAddressPlace;
	}
	
	
	

}
