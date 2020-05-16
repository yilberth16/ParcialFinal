package com.example.covid;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import pl.droidsonroids.gif.GifTextView;


public class ver_img_completa extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_img_completa);


        String url = getIntent().getStringExtra("url");

        Button btn_cerrar_foto = (Button)findViewById(R.id.btn_ver_completa_imagen);
        final ImageView img_ver_completa_imagen = (ImageView)findViewById(R.id.img_ver_completa_imagen);
        final TextView txt_no_img = (TextView)findViewById(R.id.txt_img_no);

        btn_cerrar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (url ==  null){
        txt_no_img.setVisibility(View.GONE);
        }

        final GifTextView loading_img = (GifTextView)findViewById(R.id.gif_cargando_video_imagen);
        if (estaConectado()){
            cargar_img();

        }else {
            final RelativeLayout conectarse_internet = (RelativeLayout) findViewById(R.id.rela_conectarse_internet_ver);
            Button btn_conectar_internet = (Button) findViewById(R.id.btn_conectarse_internet_ver);
            TextView txt_conectarse_internet = (TextView) findViewById(R.id.txt_conectarse_internet_ver);
            final GifTextView gif_cargando_conexion_inicio = (GifTextView) findViewById(R.id.gif_cargando_conexion_ver);

            conectarse_internet.setVisibility(View.VISIBLE);
            btn_conectar_internet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gif_cargando_conexion_inicio.setVisibility(View.VISIBLE);
                    if (estaConectado()) {
                        gif_cargando_conexion_inicio.setVisibility(View.INVISIBLE);
                        conectarse_internet.setVisibility(View.GONE);
                        cargar_img();

                    } else {
                        gif_cargando_conexion_inicio.setVisibility(View.INVISIBLE);
                        conectarse_internet.setVisibility(View.VISIBLE);
                    }

                }
            });
        }


    }


    private void cargar_img(){

        String url = getIntent().getStringExtra("url");


        final ImageView img_ver_completa_imagen = (ImageView)findViewById(R.id.img_ver_completa_imagen);
        Picasso.get().load(url).into(img_ver_completa_imagen);
        final GifTextView loading_img = (GifTextView)findViewById(R.id.gif_cargando_video_imagen);
        loading_img.setVisibility(View.GONE);
    }
    protected Boolean estaConectado() {
        if (conectadoWifi()) {

            return true;
        } else {
            if (conectadoRedMovil()) {

                return true;
            } else {

                return false;
            }
        }
    }


    protected Boolean conectadoWifi() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Boolean conectadoRedMovil() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
}
