package com.example.covid.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class esquemaDB extends SQLiteOpenHelper {

    private static final String NOMBRE_DB = "detallePaciente.bd";
    private static final int VERSION_DB = 1;
    public static final String COL_1 = "cedula";
    public static final String COL_2 = "nombreCompleto";
    public static final String COL_3 = "direccion";
    public static final String COL_4 = "fechaDeIngreso";
    public static final String COL_5 = "fotoCedula";
    private static final String TABLA_PACIENTE = "CREATE TABLE INFORMACION (cedula TEXT PRIMARY KEY,  nombreCompleto TEXT, direccion TEXT, fechaDeIngreso TEXT, fotoCedula TEXT)";

    public esquemaDB(Context context) {
        super(context, NOMBRE_DB, null, VERSION_DB);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_PACIENTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLA_PACIENTE);
        db.execSQL(TABLA_PACIENTE);

    }

    public void agregarDatos(String cedula,String nombre, String direccion, String fecha, String foto){
        SQLiteDatabase db = getWritableDatabase();
        if (db != null){
            db.execSQL("INSERT INTO INFORMACION VALUES('"+cedula+"','"+nombre+"','"+direccion+"','"+fecha+"','"+foto+"')");
            db.close();
        }
    }
    public Integer eliminarDatos(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("PARCIAL", "IDENTIFICACION = ?", new String[]{id});
    }

}
