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

import com.example.wasleysantos.flanelinha.R;
import util.ConverteData;
import com.example.wasleysantos.flanelinha.model.Vaga;

import java.util.ArrayList;


/**
 * Created by WasleySantos on 02/05/2016.
 */
public class VagasOcupadasAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<Vaga> vagascadastras;

    public VagasOcupadasAdapter(Context ctx, ArrayList<Vaga> vagas) {
        this.ctx = ctx;
        this.vagascadastras = vagas;
    }

    @Override
    public int getCount() {
        return vagascadastras.size();
    }

    @Override
    public Object getItem(int position) {
        return vagascadastras.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Vaga vaga = vagascadastras.get(position);

        ViewHolderVagas holder = null;

        if (convertView == null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.minhas_vagas_ocupadas_adapter, null);

            holder = new ViewHolderVagas();
            holder.txtStatus = (TextView) convertView.findViewById(R.id.txt_vc_status);
            holder.imageView = (ImageView) convertView.findViewById(R.id.marcadores);
            holder.txtEndereco = (TextView) convertView.findViewById(R.id.txt_vc_Endereco);
            holder.txtBairro = (TextView) convertView.findViewById(R.id.txt_vc_Bairro);
            holder.txtCidade = (TextView) convertView.findViewById(R.id.txt_vc_Cidade);
            holder.txtEstado = (TextView) convertView.findViewById(R.id.txt_vc_Estado);
            holder.txtData = (TextView) convertView.findViewById(R.id.txt_data_vaga);
            holder.txtHora = (TextView) convertView.findViewById(R.id.txt_hora_vaga);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderVagas)convertView.getTag();
        }


        Resources res = ctx.getResources();
        TypedArray imagemMarcadores = res.obtainTypedArray(R.array.imagemMarcadores);

        holder.imageView.setImageDrawable(imagemMarcadores.getDrawable((int) vaga.getTipovaga().getId()));
        holder.txtEndereco.setText(vaga.getEndereco());
        holder.txtBairro.setText(vaga.getBairro());
        holder.txtCidade.setText(vaga.getCidade());
        holder.txtEstado.setText(vaga.getEstado());

        ConverteData converteData = new ConverteData();
        String datanew = null;
        try {
            datanew = String.valueOf(converteData.extraiData(vaga.getData_vaga()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.txtData.setText(datanew);
        String horanew = null;
        try {
            horanew = String.valueOf(converteData.extraiHoraDeDta(vaga.getData_vaga()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.txtHora.setText(horanew);

        holder.txtStatus.setText(R.string.statusVaga);

        return convertView;
    }

    static class ViewHolderVagas {
        TextView txtStatus;
        ImageView imageView;
        TextView txtEndereco;
        TextView txtBairro;
        TextView txtCidade;
        TextView txtEstado;
        TextView txtData;
        TextView txtHora;
    }


}

