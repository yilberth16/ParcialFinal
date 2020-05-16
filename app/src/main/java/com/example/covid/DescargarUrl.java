package com.example.covid;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DescargarUrl {

    public String leerUrl(String miUrl) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try{
            URL url = new URL(miUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line ="";
            while((line = br.readLine()) !=  null){
                stringBuffer.append(line);
            }

            data = stringBuffer.toString();
            br.close();
        }catch (MalformedURLException e){
            Log.i("DescargaUrl","leerUrl: "+e.getMessage());
        }

        catch (IOException e){
            e.printStackTrace();
        }

        finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return data;
    }

}
