package com.example.paindairy.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.paindairy.R;
import com.example.paindairy.databinding.MapFragmentBinding;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private MapFragmentBinding mapFragmentBinding;
    private MapView mapView;

//    Default Value
    double lat = -37.876823;
    double lon = 145.045837;

    private LatLng latLng;

    public MapsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String token = getString(R.string.mapbox_access_token);
        Mapbox.getInstance(getActivity(), token);
        mapFragmentBinding = MapFragmentBinding.inflate(inflater, container,false);
        View view = mapFragmentBinding.getRoot();
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        loadMap(lat,lon);

        mapFragmentBinding.addressLookUp.setOnClickListener(this);
        

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addressLookUp)
            getLatLonLocation();
    }

    private void getLatLonLocation() {
        String address = mapFragmentBinding.addressEditText.getText().toString();

        if (!address.isEmpty()) {
            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addressList = geocoder.getFromLocationName(address, 1);
                if (addressList.size() > 0) {
                    lat = addressList.get(0).getLatitude();
                    lon = addressList.get(0).getLongitude();
                }
                else {
                    mapFragmentBinding.addressEditText.setError("Please enter a valid address");
                    mapFragmentBinding.addressEditText.requestFocus();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            loadMap(lat,lon);
        }
        else {
            mapFragmentBinding.addressEditText.setError("Please enter a Address");
            mapFragmentBinding.addressEditText.requestFocus();
        }
    }

    private void loadMap(double lat, double lon) {
        latLng = new LatLng(lat,lon);

        mapView.getMapAsync(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapFragmentBinding = null;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                CameraPosition position = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(13)
                        .build();
                mapboxMap.setCameraPosition(position);
                mapboxMap.addMarker(new MarkerOptions().position(latLng)).setTitle(mapFragmentBinding.addressEditText.getText().toString());
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}
