package com.example.amnhotelsystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.example.amnhotelsystem.databinding.FragmentSearchBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SearchFragment extends Fragment implements GuestPickerDialog.GuestPickerListener {

    private FragmentSearchBinding binding;
    private MaterialButton locationButton;
    private MaterialButton datepickerButton;
    private MaterialButton guestpickerButton;
    private MaterialButton searchButton;
    private String checkInDate;
    private String checkOutDate;
    private int adultsCount = 0;
    private int childsCount = 0;
    private int checkInDay = 0;
    private int checkInMonth = 0;
    private int checkInYear = 0;
    private int checkOutDay = 0;
    private int checkOutMonth = 0;
    private int checkOutYear = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        requireActivity().setTitle("Search");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationButton = binding.locationButton;
        datepickerButton = binding.datepickerButton;
        guestpickerButton = binding.guestpickerButton;
        searchButton = binding.searchButton;

        locationButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LocationDialogActivity.class);
            startActivityForResult(intent, LOCATION_DIALOG_REQUEST_CODE);
        });

        datepickerButton.setOnClickListener(v -> showDatePickerDialog());

        guestpickerButton.setOnClickListener(v -> {
            GuestPickerDialog guestPickerDialog = new GuestPickerDialog();
            guestPickerDialog.setGuestPickerListener(this);
            guestPickerDialog.show(getChildFragmentManager(), "GuestPickerDialog");
        });


        searchButton.setOnClickListener(v -> {
            String location = locationButton.getText().toString();
            Intent intent = new Intent(requireContext(), SearchResultsActivity.class);
            intent.putExtra("location", location);
            intent.putExtra("adultsCount", adultsCount);
            intent.putExtra("childsCount", childsCount);
            intent.putExtra("checkInDay", checkInDay);
            intent.putExtra("checkInMonth", checkInMonth);
            intent.putExtra("checkInYear", checkInYear);
            intent.putExtra("checkOutDay", checkOutDay);
            intent.putExtra("checkOutMonth", checkOutMonth);
            intent.putExtra("checkOutYear", checkOutYear);
            intent.putExtra("checkInDate", checkInDate);
            intent.putExtra("checkOutDate", checkOutDate);
            startActivity(intent);
        });
    }

    private void showDatePickerDialog() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select dates");
        builder.setSelection(androidx.core.util.Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()));
        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();

        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            // Get the selected start and end dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            long startDate = selection.first;
            long endDate = selection.second;
            String formattedStartDate = dateFormat.format(startDate);
            String formattedEndDate = dateFormat.format(endDate);
            checkInDate = formattedStartDate;
            checkOutDate = formattedEndDate;

            Calendar startDateCalendar = Calendar.getInstance();
            startDateCalendar.setTimeInMillis(startDate);

            Calendar endDateCalendar = Calendar.getInstance();
            endDateCalendar.setTimeInMillis(endDate);

            checkInDay = startDateCalendar.get(Calendar.DAY_OF_MONTH);
            checkInMonth = startDateCalendar.get(Calendar.MONTH);
            checkInYear = startDateCalendar.get(Calendar.YEAR);

            checkOutDay = endDateCalendar.get(Calendar.DAY_OF_MONTH);
            checkOutMonth = endDateCalendar.get(Calendar.MONTH);
            checkOutYear = endDateCalendar.get(Calendar.YEAR);

            // Do something with the selected dates
            // For example, display them in a TextView
            String selectedDatesText = " " + formattedStartDate + " - " + formattedEndDate;
            datepickerButton.setText(selectedDatesText);
        });

        dateRangePicker.show(getParentFragmentManager(), "dateRangePicker");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_DIALOG_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String location = data.getStringExtra("location");
            if (location != null) {
                locationButton.setText(location);
            }
        }
    }

    @Override
    public void onGuestCountsSelected(int adultsCount, int childsCount) {
        this.adultsCount = adultsCount;
        this.childsCount = childsCount;

        updateGuestPickerButtonText();
    }

    private void updateGuestPickerButtonText() {
        String guestText = adultsCount + " Adults, " + childsCount + " Kids";
        guestpickerButton.setText(guestText);
    }

    private static final int LOCATION_DIALOG_REQUEST_CODE = 1;
}