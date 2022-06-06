package com.example.natour21;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AddressAutoSearchComponent extends java.util.Observable{

    private boolean isSearchingAddress = false;
    private boolean isEditFromDropDown = false;
    private Handler delayedCallHandler = new Handler();

    private String inputAddress;
    private List<GeoPoint> dropdownAddressesGeopoints;
    private List<String> dropdownAddresses;
    private final int MAX_RESULTS;
    private AutoCompleteTextView addressAutoCompleteTextView;

    //false se il testo nell'autoCompleteTextView non è stato scelto tramite dropdown
    private boolean isResultReady = false;

    private Geocoder geocoder;

    public AddressAutoSearchComponent(Context context,
                                      AdapterView.OnItemClickListener addressAutoCompleteTextViewClickListener,
                                      int MAX_RESULTS){
        addressAutoCompleteTextView = new AutoCompleteTextView(context);
        geocoder = new Geocoder(context);
        this.MAX_RESULTS = MAX_RESULTS;
        addressAutoCompleteTextView.addTextChangedListener(textWatcher);
        addressAutoCompleteTextView.setOnItemClickListener(addressAutoCompleteTextViewClickListener);
    }

    public AddressAutoSearchComponent(Context context,
            AutoCompleteTextView autoCompleteTextView,
            AdapterView.OnItemClickListener addressAutoCompleteTextViewClickListener,
            int MAX_RESULTS){
        addressAutoCompleteTextView = autoCompleteTextView;
        geocoder = new Geocoder(context);
        this.MAX_RESULTS = MAX_RESULTS;
        addressAutoCompleteTextView.addTextChangedListener(textWatcher);
        addressAutoCompleteTextView.setOnItemClickListener(addressAutoCompleteTextViewClickListener);
    }

    public void setAddressAutoCompleteTextViewCLickListener
            (AdapterView.OnItemClickListener onItemClickListener){
        addressAutoCompleteTextView.setOnItemClickListener(onItemClickListener);
    }

    public GeoPoint getChosenGeoPoint(int position){
        return dropdownAddressesGeopoints.get(position);
    }

    public String getChosenAddressString(int position){
        return dropdownAddresses.get(position);
    }

    public AutoCompleteTextView getAddressAutoCompleteTextView(){
        return addressAutoCompleteTextView;
    }

    private Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            try {
                isSearchingAddress = true;
                setChanged();
                notifyObservers(isSearchingAddress);
                List<Address> addressList = geocoder
                        .getFromLocationName(inputAddress, MAX_RESULTS);
                ArrayList<String> stringAddresses = new ArrayList<String>();
                ArrayList<GeoPoint> geopoints = new ArrayList<GeoPoint>();
                for (Address a : addressList) {
                    String address = a.getAddressLine(0);
                    String result = address;
                    stringAddresses.add(result);
                    geopoints.add(new GeoPoint(a.getLatitude(), a.getLongitude()));
                }
                dropdownAddresses = stringAddresses;
                dropdownAddressesGeopoints = geopoints;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                                (addressAutoCompleteTextView.getContext(),
                                        android.R.layout.simple_list_item_1, stringAddresses);
                        arrayAdapter.getFilter().filter(null);
                        addressAutoCompleteTextView.setAdapter(arrayAdapter);
                        addressAutoCompleteTextView.showDropDown();
                    }
                });
            } catch (IOException ioe) {
                throw new RuntimeException(ioe.getMessage());
            }
            finally{
                isSearchingAddress = false;
                setChanged();
                notifyObservers(isSearchingAddress);
            }
        }
    };


    private TextWatcher textWatcher = new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            addressAutoCompleteTextView.dismissDropDown();
            addressAutoCompleteTextView.setAdapter(null);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (addressAutoCompleteTextView.isPerformingCompletion()) {
                isEditFromDropDown = true;
            }
            isResultReady = false;
            delayedCallHandler.removeCallbacks(searchRunnable);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (isEditFromDropDown){
                isEditFromDropDown = false;
                isResultReady = true;
                return;
            }
            inputAddress = editable.toString();
            if (inputAddress.length() >= 5){
                if (!isSearchingAddress){
                    delayedCallHandler.postDelayed(searchRunnable, 1000);
                }

            }
        }
    };

    public boolean isResultReady(){
        return isResultReady;
    }

}
