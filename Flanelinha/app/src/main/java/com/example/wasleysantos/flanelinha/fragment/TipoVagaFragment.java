package com.example.wasleysantos.flanelinha.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wasleysantos.flanelinha.R;
import com.example.wasleysantos.flanelinha.adapter.TipoVagaAdapter;

import util.Constantes;

import com.example.wasleysantos.flanelinha.dialog.TipoVagaDialogFragment;
import com.example.wasleysantos.flanelinha.model.Usuario;
import util.RequisicaoHttp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.example.wasleysantos.flanelinha.model.TipoVaga;


public class TipoVagaFragment extends ListFragment {

    ListView mListView;

    private ArrayList<TipoVaga> mTiposVaga;
    private TipoVagaAdapter mAdapter;

    public ProgressDialog pdialog = null;
    public Context context = null;

    private Usuario mUsuario;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Intent intent = getActivity().getIntent();
        mUsuario = (Usuario) intent.getSerializableExtra(Constantes.EXTRA_USUARIO);

//        setHasOptionsMenu(true);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();

        final int PADDING = 8;
        TextView txtHeader = new TextView(getActivity());

        txtHeader.setBackgroundColor(Color.GRAY);
        txtHeader.setTextColor(Color.WHITE);
        txtHeader.setText(R.string.tipo_vaga);
        txtHeader.setPadding(PADDING, PADDING,0,PADDING);

        mListView.addHeaderView(txtHeader);


        mTiposVaga = new ArrayList<>();

            }

    public void limparBusca() {
        ordenar();
        mAdapter = new TipoVagaAdapter(getActivity(), mTiposVaga);
        setListAdapter(mAdapter);
    }

    private void ordenar() {
        Collections.sort(mTiposVaga, new Comparator<TipoVaga>() {
            @Override
            public int compare(TipoVaga tipo1, TipoVaga tipo2) {
                return tipo1.getDescricao().compareTo(tipo2.getDescricao());
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Activity activity = getActivity();

        if (activity instanceof AoClicarNoTipoDeVaga){
            TipoVaga tipoVaga = (TipoVaga)l.getItemAtPosition(position);

            AoClicarNoTipoDeVaga listner = (AoClicarNoTipoDeVaga) activity;
            listner.clicouNoTipoVaga(tipoVaga, mUsuario);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        TipoVagaTask mAuthTask = new TipoVagaTask();
        mAuthTask.execute();

        limparBusca();


    }




    public interface AoClicarNoTipoDeVaga {
        void clicouNoTipoVaga(TipoVaga tipoVaga, Usuario usuario);


    }


    public class TipoVagaTask extends AsyncTask<Void, Void, ArrayList<TipoVaga>> {

        private ArrayList<TipoVaga> mmTiposVagas = new ArrayList<>();;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = ProgressDialog.show(getActivity(), "", "Por favor aguarde, consultando tipos de vagas disponíveis", true);
        }

        @Override
        protected ArrayList<TipoVaga> doInBackground(Void... params) {
            RequisicaoHttp requisicaoHttp = new RequisicaoHttp();
               try {
                   mmTiposVagas = requisicaoHttp.consultaTipoVagasDisponiveisWebService();
               } catch (Exception e) {
                   e.printStackTrace();
               }

            return  mmTiposVagas;
        }



        @Override
        protected void onPostExecute(ArrayList<TipoVaga> lista) {
            super.onPostExecute(lista);

            pdialog.dismiss();


            if (lista != null) {
                mTiposVaga.clear();
                mTiposVaga.addAll(lista);
                mAdapter.notifyDataSetChanged();

                TipoVagaDialogFragment tipoVagaDialogFragment = new TipoVagaDialogFragment();
                tipoVagaDialogFragment.show(getFragmentManager(),"sobre");


            }
            else {
                Log.i("falha: ","falha ao carregar lista de tipos de vagas");
            }

        }
    }


}
