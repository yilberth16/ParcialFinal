package com.example.covid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.covid.Modelo.Pacientes;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Transformation;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MapaPaciente extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    double actualLatitud, actualLongitud;
    Location miLocation;

    FirebaseDatabase database;
    DatabaseReference reference;
    TextView nombrePaciente, cedulaPaciente, direccionPaciente,fechaIngreso;
    ImageView imgCedula;
    String id="";
    Pacientes pacientes;

    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_paciente);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        nombrePaciente = findViewById(R.id.txtNombrePaciente);
        cedulaPaciente = findViewById(R.id.txtCedulaPaciente);
        direccionPaciente = findViewById(R.id.txtDireccionPaciente);
        fechaIngreso = findViewById(R.id.txtFechaIngreso);
        imgCedula = findViewById(R.id.imgCedula);
        Bundle parametros = this.getIntent().getExtras();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Pacientes");
        if (parametros != null) {
            id = parametros.getString("id");
        }
        if (!id.isEmpty()){
            cargarInformacionPaciente(id);
        }


        mapFragment.getMapAsync(this);

        String imagen = getIntent().getStringExtra("url");

        imgCedula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ver_foto = new Intent(MapaPaciente.this, ver_img_completa.class);
                ver_foto.putExtra("url",imagen);
                startActivity(ver_foto);
            }
        });
        establecerUPGClient();
    }

   public void cargarInformacionPaciente(String id){
        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pacientes = dataSnapshot.getValue(Pacientes.class);
                if(dataSnapshot.exists()) {
                    nombrePaciente.setText(pacientes.getNombreCompleto());
                    direccionPaciente.setText(pacientes.getDireccion());
                    cedulaPaciente.setText(pacientes.getCedula());
                    fechaIngreso.setText(pacientes.getFechaDeIngreso());
                    Glide.with(MapaPaciente.this).load(pacientes.getFotoCedula()).into(imgCedula);

                }
                else{

                    Toast.makeText(MapaPaciente.this, "", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void establecerUPGClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,0,this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermission();
    }

    private void checkPermission() {
        int permissionLocation = ContextCompat.checkSelfPermission(MapaPaciente.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermisos = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED){
            listPermisos.add(Manifest.permission.ACCESS_FINE_LOCATION);

            if (!listPermisos.isEmpty()){
                ActivityCompat.requestPermissions(this,listPermisos.toArray(new String[listPermisos.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }
        else {
            obtenerMiLocalizacion();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionLocation == PackageManager.PERMISSION_GRANTED){

            obtenerMiLocalizacion();

        }else{
            checkPermission();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        miLocation = location;

        if (miLocation != null){
            actualLatitud = location.getLatitude();
            actualLongitud = location.getLongitude();

            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.signs);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(actualLatitud,actualLongitud), 15.0f));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(actualLatitud,actualLongitud));
            markerOptions.title("Tú");
            markerOptions.icon(icon);
            mMap.addMarker(markerOptions);

            obtenerHospitalesCerca();
        }

    }



    private void obtenerHospitalesCerca() {
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+String.valueOf(actualLatitud)+","+String.valueOf(actualLongitud));
        stringBuilder.append("&radius=1000");
        stringBuilder.append("&type=hospital");
        stringBuilder.append("&key="+getResources().getString(R.string.google_maps_key));

        String url = stringBuilder.toString();
        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(dataTransfer);

    }

    private void obtenerMiLocalizacion(){
        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MapaPaciente.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    miLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    //locationRequest.setInterval(3000);
                    //locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(mGoogleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(mGoogleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    //Se cumplen todas las configuraciones de ubicación.
                                    //inicializamos las solicitudes aqui
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(MapaPaciente.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {


                                        miLocation = LocationServices.FusedLocationApi
                                                .getLastLocation(mGoogleApiClient);


                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                    try {
                                        // Muestra el diálogo llamando a startResolutionForResult(),
                                        // y verifica el resultado en onActivityResult
                                        // pide encender el gps automaticamente
                                        status.startResolutionForResult(MapaPaciente.this,
                                                REQUEST_CHECK_SETTINGS_GPS);


                                    } catch (IntentSender.SendIntentException e) {
                                    }


                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                                    break;
                            }
                        }
                    });

                }
            }
        }
    }


}

