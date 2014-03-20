package android.tgudapp.slidingmenu;

import java.util.List;

import android.content.Context;
import android.tgudapp.ui.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SlidingMenuAdapter extends ArrayAdapter<SlidingMenuItem>{

	private Context context;
	private List<SlidingMenuItem> items;
	private LayoutInflater li;
	public SlidingMenuAdapter(Context context, List<SlidingMenuItem> objects) {
		super(context, 0, objects);
		this.context = context;
		this.items = objects;
		li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public SlidingMenuItem getItem(int position) {
		return items.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final SlidingMenuItem item = items.get(position);
		if(item != null){
			if(item.isSection()){
				SlidingMenuSectionItem smsItem = (SlidingMenuSectionItem)item;
				v = li.inflate(R.layout.slidingmenu_section_item, null);
				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
				
				final TextView sectionView = (TextView)v.findViewById(R.id.tvSlidingMenuSection);
				sectionView.setText(smsItem.getTitleSectionItem());
			}else{
				SlidingMenuEntryItem smeItem = (SlidingMenuEntryItem)item;
				v = li.inflate(R.layout.slidingmenu_entry_item, null);
				
				final ImageView imgSlidingMenuEntry = (ImageView)v.findViewById(R.id.imgSlidingMenuEntry);
				final TextView tvSlidingMenuEntry = (TextView)v.findViewById(R.id.tvSlidingMenuEntry);
				if(imgSlidingMenuEntry != null){
					imgSlidingMenuEntry.setImageResource(smeItem.getImageResourceId());
				}
				if(tvSlidingMenuEntry != null){
					tvSlidingMenuEntry.setText(smeItem.getTitleEntryItem());
				}
			}
		}
		return v;
	}
	
	
}
