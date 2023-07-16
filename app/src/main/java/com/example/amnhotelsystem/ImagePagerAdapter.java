package com.example.amnhotelsystem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    private List<ImageHotel> images;

    public ImagePagerAdapter(FragmentManager fragmentManager, List<ImageHotel> images) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Fragment getItem(int position) {
        return ImageFragment.newInstance(images.get(position).getImageUrlEach());
    }

    public void updateImages(List<ImageHotel> newImages) {
        images = newImages;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Assuming ImageHotel has a 'description' property
        return images.get(position).getImageUrlEach();
    }
}
