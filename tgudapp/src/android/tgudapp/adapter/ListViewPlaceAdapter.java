package android.tgudapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.tgudapp.model.Place;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tgud.R;


public class ListViewPlaceAdapter extends ArrayAdapter<Place>{
	
	
	private Context mContext;
	private List<Place> listPlace;
	private List<Place> originalListPlace;
	public ListViewPlaceAdapter(Context context, List<Place> objects) {
		super(context, 0, objects);
		this.mContext = context;
		this.listPlace= objects;
		this.originalListPlace= objects;
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
			holder.llPromotionPercentageWrapper = (LinearLayout)v.findViewById(R.id.llPromotionPercentageWrapperVerticalList);
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
		holder.tvPromotionPercent.setText(promotionPercent + "%");
		if (promotionPercent == "0") {
			holder.llPromotionPercentageWrapper.setVisibility(LinearLayout.GONE);
		}
		return v;
	}
	
	private static class ViewHolder{
		private ImageView imgPlace;
		private TextView tvNamePlace;
		private TextView tvAddressPlace;
		private TextView tvTelPlace;
		private TextView tvPromotionPercent;
		private LinearLayout llPromotionPercentageWrapper;
	}
	
	@Override
	public android.widget.Filter getFilter() {
		return new android.widget.Filter() {
			
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
//				if (results.count == 0)
//			        notifyDataSetInvalidated();
//			    else {
//			        
//			    }
				listPlace = (List<Place>) results.values;
		        notifyDataSetChanged();
			}
			
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				listPlace = originalListPlace;
				FilterResults results = new FilterResults();
			    // We implement here the filter logic
			    if (constraint == null || constraint.length() == 0) {
			        // No filter implemented we return all the list
			        results.values = listPlace;
			        results.count = listPlace.size();
			    }
			    else {
			        // We perform filtering operation
			        List<Place> nListPlace = new ArrayList<Place>();
			         
			        for (Place p : listPlace) {
			            if (p.getNamePlace().toUpperCase().contains((constraint.toString().toUpperCase())) ||
			            		p.getAddressPlace().toUpperCase().contains((constraint.toString().toUpperCase()))) {
			                nListPlace.add(p);
			            }
			        }
			        results.values = nListPlace;
			        results.count = nListPlace.size();
			 
			    }
			    return results;
			}
		};
	}
}
