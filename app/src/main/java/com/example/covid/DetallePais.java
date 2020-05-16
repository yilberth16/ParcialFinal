package com.example.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class DetallePais extends AppCompatActivity {

    private int posicion;
    TextView txtCasos,txtRecuperados,txtCritico,txtActivos,txtCasosHoy,txtTotalFallecidos,txtFallecidosHoy,txtPaisAfectado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pais);

        txtCasos = findViewById(R.id.txtCasos);
        txtRecuperados = findViewById(R.id.txtRecuperados);
        txtCritico = findViewById(R.id.txtCritico);
        txtActivos = findViewById(R.id.txtActivo);
        txtCasosHoy = findViewById(R.id.txtHoyCasos);
        txtTotalFallecidos = findViewById(R.id.txtTotalFallecidos);
        txtPaisAfectado = findViewById(R.id.txtAfectadoPais);
        txtFallecidosHoy = findViewById(R.id.txtFallecidosHoy);

        posicion = getIntent().getIntExtra("posicion",0);
        getSupportActionBar().setTitle("Detalles de "+PaisesAfectados.paisesList.get(posicion).getCountry());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtPaisAfectado.setText(PaisesAfectados.paisesList.get(posicion).getCountry());
        txtCasos.setText(PaisesAfectados.paisesList.get(posicion).getCases());
        txtCasosHoy.setText(PaisesAfectados.paisesList.get(posicion).getTodayCases());
        txtCritico.setText(PaisesAfectados.paisesList.get(posicion).getCritical());
        txtTotalFallecidos.setText(PaisesAfectados.paisesList.get(posicion).getDeaths());
        txtFallecidosHoy.setText(PaisesAfectados.paisesList.get(posicion).getTodayDeaths());
        txtActivos.setText(PaisesAfectados.paisesList.get(posicion).getActive());
        txtRecuperados.setText(PaisesAfectados.paisesList.get(posicion).getRecovered());

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
