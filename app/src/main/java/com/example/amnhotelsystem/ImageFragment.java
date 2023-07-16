package com.example.amnhotelsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.amnhotelsystem.databinding.FragmentImageBinding; // Create this in your project

public class ImageFragment extends Fragment {
    private FragmentImageBinding binding;

    private static final String ARG_IMAGE_URL = "imageUrl";

    public static ImageFragment newInstance(String imageUrl) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentImageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String imageUrl = getArguments().getString(ARG_IMAGE_URL);
        if (imageUrl != null) {
            // Use Glide to load the image
            Glide.with(this)
                .load(imageUrl)
                .into(binding.imageView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
