package android.tgudapp.adapter;

import java.util.List;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.tgudapp.model.Place;
import android.tgudapp.ui.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewPlaceAdapter extends ArrayAdapter<Place>{
	
	
	private Context mContext;
	private List<Place> listPlace;
	public ListViewPlaceAdapter(Context context, List<Place> objects) {
		super(context, 0, objects);
		this.mContext = context;
		this.listPlace= objects;
	}
	@Override
	public int getCount() {
		return listPlace.size();
	}
	@Override
	public Place getItem(int position) {
		return listPlace.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder = null;
		if(v == null){
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.listview_place_item, null);
			holder = new ViewHolder();
			holder.imgPlace = (ImageView)v.findViewById(R.id.imgPlaceVerticalList);
			holder.tvNamePlace = (TextView)v.findViewById(R.id.tvNamePlaceVerticalList);
			holder.tvAddressPlace = (TextView)v.findViewById(R.id.tvAddressPlaceVerticalList);
			holder.tvTelPlace = (TextView)v.findViewById(R.id.tvTelPlaceVerticalList);
			holder.tvPromotionPercent = (TextView)v.findViewById(R.id.tvPromotionPercentVerticalList);
			v.setTag(holder);
		}else{
			holder = (ViewHolder)v.getTag();
		}
		final Place item = listPlace.get(position);
		Picasso	.with(mContext)
				.load(item.getImageURL())
				.resize(130, 130)
				.placeholder(R.drawable.empty_photo)
				.error(R.drawable.empty_photo)
				.into(holder.imgPlace);
		holder.tvNamePlace.setText(item.getNamePlace());
		holder.tvAddressPlace.setText(item.getAddressPlace());
		holder.tvTelPlace.setText(item.getTelPlace());
		String promotionPercent = "0";
		if (item.getProPercent() != "null") {
			promotionPercent = item.getProPercent();
		}
		holder.tvPromotionPercent.setText(promotionPercent + " %");
		return v;
	}
	
	private static class ViewHolder{
		private ImageView imgPlace;
		private TextView tvNamePlace;
		private TextView tvAddressPlace;
		private TextView tvTelPlace;
		private TextView tvPromotionPercent;
	}
}
