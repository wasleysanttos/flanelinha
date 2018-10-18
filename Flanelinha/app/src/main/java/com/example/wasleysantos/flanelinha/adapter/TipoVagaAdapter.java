package com.example.wasleysantos.flanelinha.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.wasleysantos.flanelinha.R;
import com.example.wasleysantos.flanelinha.model.TipoVaga;

/**
 * Created by WasleySantos on 09/05/2016.
 */
public class TipoVagaAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<TipoVaga> tipoVaga;

    public TipoVagaAdapter(Context ctx, ArrayList<TipoVaga> tipovaga) {
        this.ctx = ctx;
        this.tipoVaga = tipovaga;
    }

    @Override
    public int getCount() {
        return tipoVaga.size();
    }

    @Override
    public Object getItem(int position) {
        return tipoVaga.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TipoVaga tipovaga = tipoVaga.get(position);

        ViewHolder holder = null;

        if (convertView == null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.tipo_vaga_adapter, null);
            holder = new ViewHolder();
            holder.txtDescricao = (TextView) convertView.findViewById(R.id.txtdescricao_tipovaga);
            holder.imageView = (ImageView) convertView.findViewById(R.id.marcadores);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Resources res = ctx.getResources();
        TypedArray imagemMarcadores = res.obtainTypedArray(R.array.imagemMarcadores);



        holder.imageView.setImageDrawable(imagemMarcadores.getDrawable((int) tipovaga.getId()));

        holder.txtDescricao.setText(tipovaga.getDescricao());


        return convertView;
    }

    static class ViewHolder {
        TextView txtDescricao;
        ImageView imageView;

    }

}
