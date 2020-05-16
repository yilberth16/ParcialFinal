package com.example.covid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.leo.simplearcloader.SimpleArcLoader;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

public class InicioActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtCasos,txtRecuperados,txtCritico,txtActivos,txtCasosHoy,txtTotalFallecidos,txtFallecidosHoy,txtPaisAfectado;
    SimpleArcLoader carga;
    ScrollView scrollView;
    PieChart pieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        String nombre = getIntent().getStringExtra("nombre");
        View headerView = navigationView.getHeaderView(0);
        TextView txtNombre = (TextView)headerView.findViewById(R.id.txtNombre);
        txtNombre.setText(nombre);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
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
                Toast.makeText(InicioActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    public void rastrarPais(View view) {

        startActivity(new Intent(getApplicationContext(),PaisesAfectados.class));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(InicioActivity.this,IngresarPacientes.class));
        } else if (id == R.id.nav_gallery) {
            FirebaseAuth user = FirebaseAuth.getInstance();
            user.signOut();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
