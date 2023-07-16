package com.example.amnhotelsystem;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class GuestPickerDialog extends BottomSheetDialogFragment {
    private int adultsCount = 1;
    private int childsCount = 0;
    private GuestPickerListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_guest_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton adultsMinusButton = view.findViewById(R.id.adultsMinButton);
        ImageButton adultsPlusButton = view.findViewById(R.id.adultsPlusButton);
        TextView adultsCountText = view.findViewById(R.id.adultsCountText);

        ImageButton childsMinusButton = view.findViewById(R.id.childsMinButton);
        ImageButton childsPlusButton = view.findViewById(R.id.childsPlusButton);
        TextView childsCountText = view.findViewById(R.id.childsCountText);
        ImageButton closeButton = view.findViewById(R.id.closeButton);
        MaterialButton confirmButton = view.findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(v -> {
        listener.onGuestCountsSelected(adultsCount, childsCount);
        dismiss();
    });

        closeButton.setOnClickListener(v -> dismiss());

        adultsMinusButton.setOnClickListener(v -> {
        if (adultsCount > 1) {
            adultsCount--;
            adultsCountText.setText(String.valueOf(adultsCount));
        }
    });

        adultsPlusButton.setOnClickListener(v -> {
        if (adultsCount < 2) {
            adultsCount++;
            adultsCountText.setText(String.valueOf(adultsCount));
        }
    });

        childsMinusButton.setOnClickListener(v -> {
        if (childsCount > 0) {
            childsCount--;
            childsCountText.setText(String.valueOf(childsCount));
        }
    });

        childsPlusButton.setOnClickListener(v -> {
        if (childsCount < 2) {
            childsCount++;
            childsCountText.setText(String.valueOf(childsCount));
        }
    });
    }

    public void setGuestPickerListener(GuestPickerListener listener) {
        this.listener = listener;
    }

    public interface GuestPickerListener {
        void onGuestCountsSelected(int adultsCount, int childsCount);
    }
}
