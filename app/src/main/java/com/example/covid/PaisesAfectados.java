package com.example.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.covid.Adapter.PaisesAdapter;
import com.example.covid.Modelo.Paises;
import com.leo.simplearcloader.SimpleArcLoader;

import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PaisesAfectados extends AppCompatActivity {

    EditText edtBuscar;
    ListView listView;
    SimpleArcLoader simpleArcLoader;
    public static List<Paises> paisesList = new ArrayList<>();
    Paises paises;
    PaisesAdapter mPaisAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paises_afectados);

        edtBuscar = findViewById(R.id.edtBuscar);
        listView = findViewById(R.id.listView);
        simpleArcLoader = findViewById(R.id.cargando);

        getSupportActionBar().setTitle("Paises afectados");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        obtenerDatos();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(),DetallePais.class).putExtra("posicion",position));
            }
        });
        edtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPaisAdapter.getFilter().filter(s);
                mPaisAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void obtenerDatos() {
        String url = "https://corona.lmao.ninja/v2/countries/";
        simpleArcLoader.start();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i=0; i<jsonArray.length(); i++){

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String nombrePais = jsonObject.getString("country");
                            String casos = jsonObject.getString("cases");
                            String casosHoy = jsonObject.getString("todayCases");
                            String fallecidos = jsonObject.getString("deaths");
                            String fallecidosHoy = jsonObject.getString("todayDeaths");
                            String recuperados = jsonObject.getString("recovered");
                            String active = jsonObject.getString("active");
                            String critico = jsonObject.getString("critical");

                            JSONObject object = jsonObject.getJSONObject("countryInfo");
                            String banderaUrl = object.getString("flag");

                            paises = new Paises(banderaUrl,nombrePais,casos,casosHoy,fallecidos,fallecidosHoy,recuperados,active,critico);
                            paisesList.add(paises);

                        }

                        mPaisAdapter = new PaisesAdapter(PaisesAfectados.this,paisesList);
                        listView.setAdapter(mPaisAdapter);
                        simpleArcLoader.stop();
                        simpleArcLoader.setVisibility(View.GONE);



                    } catch (JSONException e) {
                        e.printStackTrace();
                        simpleArcLoader.stop();
                        simpleArcLoader.setVisibility(View.GONE);
                    }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(PaisesAfectados.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}
