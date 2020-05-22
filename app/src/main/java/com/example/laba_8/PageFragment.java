package com.example.laba_8;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PageFragment extends Fragment implements OnMapReadyCallback {
    public final static String X1_KEY = "X1";
    public final static String Y1_KEY = "Y1";
    public final static String X2_KEY = "X2";
    public final static String Y2_KEY = "Y2";
    public final static String MY_POSITION_X_KEY = "MY_POSITION_X";
    public final static String MY_POSITION_Y_KEY = "MY_POSITION_Y";

    public static final String PAGE_KEY = "NUMBER";
    public static final String MAPS_KEY = "AIzaSyAI4nxhTP5r6zfpS5cgEJ63k4uNw3wzaDs";
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    public static final String STATUS_OK = "OK";

    public static final int PAGE_FROM = 0;
    public static final int PAGE_TO = 1;

    private static String TAG = PageFragment.class.getSimpleName();
    private int pageNumber;

    private HashMap<String, ArrayList<Double>> placeWithCoords = new HashMap<>();
    private ArrayList<String> placesList = new ArrayList<>();

    private ListView listView;

    private GoogleMap map;
    private GeocodingAPI geocodingApi;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    public interface OnFragmentDataListener {
        void onFragmentDataListener(Double x, Double y);
    }

    private OnFragmentDataListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentDataListener) {
            mListener = (OnFragmentDataListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    public static PageFragment newInstance(int page) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_KEY, page);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments() != null ? getArguments().getInt(PAGE_KEY) : 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        geocodingApi = retrofit.create(GeocodingAPI.class);

        final EditText editText = view.findViewById(R.id.edit_text);
        Button button = view.findViewById(R.id.button);

        listView = view.findViewById(R.id.list);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputAddress = editText.getText().toString();

                if (inputAddress.length() != 0 ) {
                    new ProcessTask(inputAddress).execute();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String place = placesList.get(position);
                Double lat = placeWithCoords.get(place).get(0);
                Double lng = placeWithCoords.get(place).get(1);

                mListener.onFragmentDataListener(lat, lng);

                LatLng marker = new LatLng(lat, lng);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
                map.addMarker(new MarkerOptions().title("You here!").position(marker));
            }
        });

    }

    static String getTitle(int position) {
        return position == PAGE_FROM ? "Откуда" : "Куда";
    }

    private class ProcessTask extends AsyncTask<Void, Void, Void> {
        private String input;
        private Response<GeocodingResponse> response;

        public ProcessTask(String str) {
            input = str;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                response = geocodingApi.getAddress(input, MAPS_KEY).execute();
            } catch (IOException ex) {
                Log.e(TAG, "" + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            placesList.clear();
            for (Address i : response.body().addressList) {
                Double lat = i.geometry.coordinates.lat;
                Double lng = i.geometry.coordinates.lng;
                placeWithCoords.put(i.address, new ArrayList<>(Arrays.asList(lat, lng)));
                placesList.add(i.address);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, placesList);
            listView.setAdapter(adapter);
        }
    }
}