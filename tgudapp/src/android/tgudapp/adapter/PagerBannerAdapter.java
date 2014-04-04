package android.tgudapp.adapter;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.tgudapp.model.Banner;
import android.tgudapp.ui.fragment.FragmentBanner;

public class PagerBannerAdapter extends FragmentPagerAdapter{
	private List<Banner> listBanner;
	
	@Override
	public Fragment getItem(int arg0) {
		Fragment fragBanner = new FragmentBanner();
		final Banner banner = listBanner.get(arg0);
		Bundle args = new Bundle();
		args.putString("bannerImageUrl", banner.getBannerImageUrl());
		args.putString("bannerContentUrl", banner.getBannerUrl());
		args.putString("bannerName", banner.getBannerName());
		fragBanner.setArguments(args);
		return fragBanner;
	}

	@Override
	public int getCount() {
		return listBanner.size();
	}

	public PagerBannerAdapter(FragmentManager fm, List<Banner> listBanner) {
		super(fm);
		this.listBanner = listBanner;
	}

}
