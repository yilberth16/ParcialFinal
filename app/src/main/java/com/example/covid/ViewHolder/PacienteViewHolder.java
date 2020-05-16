package com.example.covid.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.covid.R;

import info.hoang8f.widget.FButton;

public class PacienteViewHolder extends RecyclerView.ViewHolder  {

    public TextView trabajador_name;
    public FButton btn_edit, btn_remove;

    public PacienteViewHolder(View itemView) {
        super(itemView);

        trabajador_name = (TextView)itemView.findViewById(R.id.nombre_trabajador);

        btn_edit = (FButton)itemView.findViewById(R.id.btnEdit);
        btn_remove = (FButton)itemView.findViewById(R.id.btnEliminar);
    }




}
