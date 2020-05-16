package com.example.covid.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.covid.Modelo.Paises;
import com.example.covid.PaisesAfectados;
import com.example.covid.R;

import java.util.ArrayList;
import java.util.List;

public class PaisesAdapter extends ArrayAdapter<Paises> {

    private Context context;
    private List<Paises> paisesList;
    private List<Paises> paisesListFilter;



    public PaisesAdapter(Context context, List<Paises> paisesList) {
        super(context, R.layout.item_paises,paisesList);

        this.context = context;
        this.paisesList = paisesList;
        this.paisesListFilter = paisesList;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paises,null,true);
        TextView txtNombrePais = view.findViewById(R.id.txtNombrePais);
        ImageView imgBandera = view.findViewById(R.id.bandera);

        txtNombrePais.setText(paisesListFilter.get(position).getCountry());
        Glide.with(context).load(paisesListFilter.get(position).getFlag()).into(imgBandera);
        return view;

    }

    @Override
    public int getCount() {
        return paisesListFilter.size();
    }

    @Override
    public Paises getItem(int position) {
        return paisesListFilter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0){
                    filterResults.count = paisesList.size();
                    filterResults.values = paisesList;

                }else {
                    List<Paises> resultPaises = new ArrayList<>();
                    String buscar = constraint.toString().toLowerCase();

                    for (Paises itemsPaises: paisesList){
                        if (itemsPaises.getCountry().toLowerCase().contains(buscar)){
                            resultPaises.add(itemsPaises);
                        }

                        filterResults.count = resultPaises.size();
                        filterResults.values = resultPaises;
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                paisesListFilter = (List<Paises>) results.values;
                PaisesAfectados.paisesList = (List<Paises>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
