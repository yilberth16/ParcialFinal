package com.example.covid;


import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GetNearbyPlacesData extends AsyncTask<Object,String,String> {

    String googlePlaceData;
    GoogleMap googleMap;
    String url;

    @Override
    protected String doInBackground(Object... objects) {
            googleMap = (GoogleMap) objects[0];
            url = (String) objects[1];
            DescargarUrl descargarUrl = new DescargarUrl();

            try{
                googlePlaceData = descargarUrl.leerUrl(url);

            }catch (IOException e){
                e.printStackTrace();
            }

            return googlePlaceData;
        }

    @Override
    protected void onPostExecute(String s) {
        try{
            JSONObject parentObject = new JSONObject(s);
            JSONArray resultArray = parentObject.getJSONArray("results");

            for (int i = 0; i< resultArray.length(); i++){
                JSONObject jsonObject = resultArray.getJSONObject(i);
                JSONObject locationObj = jsonObject.getJSONObject("geometry").getJSONObject("location");
                String lat = locationObj.getString("lat");
                String lng = locationObj.getString("lng");

                JSONObject nombreObject = resultArray.getJSONObject(i);

                String nombre = nombreObject.getString("name");

                LatLng latLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(nombre);
                markerOptions.position(latLng);

                googleMap.addMarker(markerOptions);

            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
