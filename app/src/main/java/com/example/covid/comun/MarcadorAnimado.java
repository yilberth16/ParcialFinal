package com.example.covid.comun;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Property;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.Transaction;


public class MarcadorAnimado {
    public static void marcadoAnimadoAGB(final Marker marker, LatLng posicionFinal, LatLngInterpolator latLngInterpolator){

        LatLng iniciarPosicion = marker.getPosition();
        Handler handler = new Handler();
        long iniciar = SystemClock.uptimeMillis();
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        float duracionEnMs = 3000;//3 segundos

        handler.post(new Runnable() {
            long elapsed;
            float t,v;
            @Override
            public void run() {
                elapsed = SystemClock.uptimeMillis() - iniciar;
                t = elapsed/duracionEnMs;
                v = interpolator.getInterpolation(t);

                marker.setPosition(latLngInterpolator.interpolate(v,iniciarPosicion,posicionFinal));

                //repita hasta que se complete el progreso
                if (t<1){
                    handler.postDelayed(this,16);
                }
            }
        });

    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void marcadoAnimadoAHC(final Marker marker, LatLng posicionFinal, LatLngInterpolator latLngInterpolator)
    {
        LatLng iniciarLocalizacion = marker.getPosition();
        ValueAnimator valueAnimator = new ValueAnimator();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = animation.getAnimatedFraction();
                LatLng nuevaPosicion = latLngInterpolator.interpolate(v,iniciarLocalizacion,posicionFinal);

                marker.setPosition(nuevaPosicion);
            }
        });

        valueAnimator.setFloatValues(0,1);
        valueAnimator.setDuration(3000);
        valueAnimator.start();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void marcadoAnimadoAICS(final Marker marker, LatLng posicionFinal, LatLngInterpolator latLngInterpolator)
    {
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return latLngInterpolator.interpolate(fraction,startValue,endValue);
            }
        };
        Property<Marker,LatLng> property = Property.of(Marker.class,LatLng.class,"position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marker,property,typeEvaluator,posicionFinal);
        animator.setDuration(3000);
        animator.start();
    }
}
