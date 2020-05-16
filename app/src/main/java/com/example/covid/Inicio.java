package com.example.covid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.leo.simplearcloader.SimpleArcLoader;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

public class Inicio extends AppCompatActivity {

    TextView txtCasos,txtRecuperados,txtCritico,txtActivos,txtCasosHoy,txtTotalFallecidos,txtFallecidosHoy,txtPaisAfectado;
    SimpleArcLoader carga;
    ScrollView scrollView;
    PieChart pieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        txtCasos = findViewById(R.id.txtCasos);
        txtRecuperados = findViewById(R.id.txtRecuperados);
        txtCritico = findViewById(R.id.txtCritico);
        txtActivos = findViewById(R.id.txtActivo);
        txtCasosHoy = findViewById(R.id.txtHoyCasos);
        txtTotalFallecidos = findViewById(R.id.txtTotalFallecidos);
        txtPaisAfectado = findViewById(R.id.txtAfectadoPais);
        txtFallecidosHoy = findViewById(R.id.txtFallecidosHoy);

        carga = findViewById(R.id.carga);
        scrollView = findViewById(R.id.scrollEstadistica);
        pieChart = findViewById(R.id.piechart);

        obtenerDatos();
    }

    private void obtenerDatos() {
        String url = "https://corona.lmao.ninja/v2/all/";
        carga.start();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            txtCasos.setText(jsonObject.getString("cases"));
                            txtRecuperados.setText(jsonObject.getString("recovered"));
                            txtCritico.setText(jsonObject.getString("critical"));
                            txtCasosHoy.setText(jsonObject.getString("todayCases"));
                            txtFallecidosHoy.setText(jsonObject.getString("todayDeaths"));
                            txtTotalFallecidos.setText(jsonObject.getString("deaths"));
                            txtActivos.setText(jsonObject.getString("active"));
                            txtPaisAfectado.setText(jsonObject.getString("affectedCountries"));


                            pieChart.addPieSlice(new PieModel("Casos",Integer.parseInt(txtCasos.getText().toString()), Color.parseColor("#FFA726")));
                            pieChart.addPieSlice(new PieModel("Recuperados",Integer.parseInt(txtRecuperados.getText().toString()), Color.parseColor("#66BB6A")));
                            pieChart.addPieSlice(new PieModel("Fallecidos",Integer.parseInt(txtTotalFallecidos.getText().toString()), Color.parseColor("#EF5350")));
                            pieChart.addPieSlice(new PieModel("Activo",Integer.parseInt(txtActivos.getText().toString()), Color.parseColor("#29B6F6")));
                            pieChart.startAnimation();

                            carga.stop();
                            carga.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            carga.stop();
                            carga.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                carga.stop();
                carga.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                Toast.makeText(Inicio.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


    public void rastrarPais(View view) {

        startActivity(new Intent(getApplicationContext(),PaisesAfectados.class));
    }
}
