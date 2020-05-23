package com.example.covid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.covid.Adapter.PaisesAdapter;
import com.example.covid.Modelo.Country_Stats;
import com.example.covid.Modelo.Lat_lng;
import com.example.covid.Modelo.Paises;
import com.example.covid.api.RapidCountryStatsAPI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapaCovid extends FragmentActivity implements OnMapReadyCallback {

    public static ArrayList<Country_Stats> country_stats_arrayLists;
    ImageView gps_img ;
    private GoogleMap mMap;
    ArrayList<Lat_lng> lat_lng = new ArrayList<>();
    ArrayList<String> lat=new ArrayList<>();
    ArrayList<String> lng=new ArrayList<>();
    SupportMapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_covid);
        country_stats_arrayLists = new ArrayList<>();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        gps_img = findViewById(R.id.transition_current_scene);
        updateMAP();
        getLatLng();
        handelSearchBar();
        onClickListener();
        if (isNetworkAvailable()){
            getDataFromAPI();

        }
        else{
            startActivity(new Intent(this,InternetCheck.class));
            finish();
        }

    }

    private void onClickListener() {
        gps_img.setOnClickListener(v->{
            LatLng home = new LatLng(6.230833, -75.590553);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(home,3));
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void updateCount() {

        try {
            String response = new RapidCountryStatsAPI().execute("https://coronavirus-monitor.p.rapidapi.com/coronavirus/worldstat.php").get();
            JSONObject obj = new JSONObject(response);
            TextView details = findViewById(R.id.details);
            details.setVisibility(View.VISIBLE);
            details.setText(" Total Confirmed: "+obj.getString("total_cases")+"  |  Deaths: "+obj.getString("total_deaths")+"\n Recovered: "+obj.getString("total_recovered")+"  |  Countries Affected: "+country_stats_arrayLists.size());


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handelSearchBar() {

        androidx.appcompat.widget.SearchView search_bar = findViewById(R.id.search_bar);
        search_bar.setQueryHint("Buscar pais");
        search_bar.setOnClickListener(v->{
            Log.e("ButtonClicked","buttonclicked");
            search_bar.onActionViewExpanded();
        });
        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("Query Submitted",query);
                LatLng latlng=null;
                for(int j =0;j<lat_lng.size();j++){
                    if(lat_lng.get(j).getName().toLowerCase().contains(query.toLowerCase())){
                        latlng =new LatLng(lat_lng.get(j).getLat(),lat_lng.get(j).getLng());
                        break;
                    }
                }
                if (latlng != null){
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,4));

                }
                else{
                    Snackbar.make(findViewById(R.id.relative),"Intente nuevamente con un nombre de país apropiado.",Snackbar.LENGTH_SHORT).show();
                }
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getLatLng() {
        InputStream is = getResources().openRawResource(R.raw.lat_lng_country);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        try{
            while ((line = reader.readLine()) != null){
                String[] tokens = line.split(",");
                Lat_lng latLng = new Lat_lng();
                latLng.setCountry(tokens[0]);
                latLng.setName(tokens[3]);
                Log.e("Lat: ",tokens[0]);
                latLng.setLat(Double.parseDouble(tokens[1]));
                latLng.setLng(Double.parseDouble(tokens[2]));
                lat_lng.add(latLng);
            }

        }
        catch (Exception e){
            Log.wtf("Error Reading: ",line,e);
            e.printStackTrace();}

    }

    private void updateMAP() {

//        progressDialog.dismiss();
        mapFragment.getMapAsync(this);
    }

    private void getDataFromAPI() {
        String result;
        try {
            Log.e("API Call Map:","making API call");

            result = new RapidCountryStatsAPI().execute("https://coronavirus-monitor.p.rapidapi.com/coronavirus/cases_by_country.php").get();
            JSONObject obj= new JSONObject(result);
            JSONArray CountriesStat = obj.getJSONArray("countries_stat");
            Log.e("API Call Map:","data recieved");
            for(int i=0;i<CountriesStat.length();i++){
                String mJsonString = CountriesStat.getString(i);
                JsonParser parser = new JsonParser();
                JsonElement mJson =  parser.parse(mJsonString);
                Gson gson = new Gson();
                Country_Stats object = gson.fromJson(mJson, Country_Stats.class);
                country_stats_arrayLists.add(object);
            }
            updateMAP();
            updateCount();

        }catch (Exception e){e.printStackTrace();}

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }


            @Override
            public View getInfoContents(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,null);
                TextView country_name = infoWindow.findViewById(R.id.country_name);
                country_name.setText(marker.getTitle());
                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }

        });
        mMap.setOnInfoWindowClickListener(marker -> {
            /*Intent intent = new Intent(this,CountryDetails.class);
            intent.putExtra("country_name",marker.getTitle());
            startActivity(intent);*/
        });

        if(country_stats_arrayLists.size() >0){
            for (int i= 0;i<country_stats_arrayLists.size();i++){
                LatLng latlng = null;
                for(int j =0;j<lat_lng.size();j++){
                    if(lat_lng.get(j).getName().equalsIgnoreCase(country_stats_arrayLists.get(i).getCountry_name())){
                        latlng =new LatLng(lat_lng.get(j).getLat(),lat_lng.get(j).getLng());

                    }
                }
                if(latlng != null) {
                    if (latlng.equals(new LatLng(6.230833, -75.590553))){
                        mMap.addMarker(new MarkerOptions().position(latlng)
                                .title(country_stats_arrayLists.get(i).getCountry_name())
                                .snippet("Total Cases: " + country_stats_arrayLists.get(i).getCases() + "\n"
                                        + "Deaths: " + country_stats_arrayLists.get(i).getDeaths() + "\n"
                                        + "Total Recovered: " + country_stats_arrayLists.get(i).getTotal_recovered() + "\n"
                                        + "*Click for more information.*"
                                )).showInfoWindow();
                    }
                    else {
                        mMap.addMarker(new MarkerOptions().position(latlng)
                                .title(country_stats_arrayLists.get(i).getCountry_name())
                                .snippet("Total Cases: " + country_stats_arrayLists.get(i).getCases() + "\n"
                                        + "Deaths: " + country_stats_arrayLists.get(i).getDeaths() + "\n"
                                        + "Total Recovered: " + country_stats_arrayLists.get(i).getTotal_recovered() + "\n"
                                        + "*Click for more information.*"
                                ));
                    }

                }
            }
            LatLng home = new LatLng(6.230833, -75.590553);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home,3));


        }
        else {
            LatLng home = new LatLng(6.230833,  -75.590553);
            mMap.addMarker(new MarkerOptions().position(home).title("Colombia").snippet("Colombia")).showInfoWindow();
            mMap.addCircle(new CircleOptions().center(home).radius(99999).fillColor(5).strokeColor(8));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(home));

        }
    }

}
