package com.example.wasleysantos.flanelinha.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wasleysantos.flanelinha.R;
import com.example.wasleysantos.flanelinha.adapter.HistoricoVagasAdapter;
import com.example.wasleysantos.flanelinha.dialog.HistoricoVagaDialogFragment;
import util.Constantes;

import com.example.wasleysantos.flanelinha.model.Usuario;
import com.example.wasleysantos.flanelinha.model.Vaga;
import util.RequisicaoHttp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by WasleySantos on 14/04/2016.
 *
 * Falta implementar metodo que retorna endereço para preencher lista
 *
 */
public class HistoricoVagasFragment extends ListFragment {

    private ArrayList<Vaga> mMinhasVagas;
    private HistoricoVagasAdapter mAdapter;

    private Usuario mUsuario;

    public ProgressDialog pdialog = null;
    public Context context = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);


        mUsuario = (Usuario) getArguments().getSerializable(Constantes.EXTRA_USUARIO);
        setHasOptionsMenu(true);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView mListView = getListView();
        mMinhasVagas = new ArrayList<>();

        final int PADDING = 8;
        TextView txtHeader = new TextView(getActivity());

        txtHeader.setBackgroundColor(Color.GRAY);
        txtHeader.setTextColor(Color.WHITE);
        txtHeader.setText(R.string.historico_vagas);
        txtHeader.setPadding(PADDING, PADDING,0,PADDING);

        mListView.addHeaderView(txtHeader);

        ListaDeVagasTask mAuthTask = new ListaDeVagasTask(mUsuario);
        mAuthTask.execute(mUsuario);


//click long sera usado para mostrar mais detalhes da vaga
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                return false;
            }
        });

        limparBusca();
    }

    public void limparBusca() {
        mAdapter = new HistoricoVagasAdapter(getActivity(), mMinhasVagas);
        setListAdapter(mAdapter);

    }





        private void ordenar() {
            Collections.sort(mMinhasVagas, new Comparator<Vaga>() {
                @Override
                public int compare(Vaga tipo1, Vaga tipo2) {
                    return tipo1.getId() <tipo2.getId() ? +1 : (tipo1.getId() > tipo2.getId() ? -1 : 0);
                }
            });
        }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);


        Activity activity = getActivity();
        if (activity instanceof HistoricoVagasFragment.AoClicarNoHistoricoDeVaga){
            Vaga vaga = (Vaga) l.getItemAtPosition(position);

            HistoricoVagasFragment.AoClicarNoHistoricoDeVaga listner = (HistoricoVagasFragment.AoClicarNoHistoricoDeVaga) activity;
            listner.clicouNaVaga(vaga);
        }

    }

    public interface AoClicarNoHistoricoDeVaga {

        void clicouNaVaga(Vaga vaga);
    }

    public class ListaDeVagasTask extends AsyncTask<Usuario, Void, ArrayList<Vaga> > {

        private ArrayList<Vaga> mmVagas = new ArrayList<>();;
        private Usuario mmUsuario ;

        public ListaDeVagasTask(Usuario mUsuario) {
            mmUsuario = new Usuario(
                    mUsuario.getId(),
                    mUsuario.getEmail(),
                    mUsuario.getNome(),
                    mUsuario.getPassword(),
                    mUsuario.getPalavraSecreta());

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = ProgressDialog.show(getActivity(), "", "Por favor aguarde, estamos consultando seu histórico...", true);

        }

        @Override
        protected ArrayList <Vaga> doInBackground(Usuario... params) {

            //buscar lista de vagas
            RequisicaoHttp requisicaoHttp = new RequisicaoHttp();

            try {
                mmVagas =  requisicaoHttp.consultaVagasUsuarioWebService(mmUsuario);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return mmVagas;
        }



        @Override
        protected void onPostExecute(ArrayList<Vaga> listVagas) {
            super.onPostExecute(listVagas);


            pdialog.dismiss();


            if (listVagas != null) {
                mMinhasVagas.clear();
                mMinhasVagas.addAll(listVagas);
                mAdapter.notifyDataSetChanged();


            }

            if (mMinhasVagas.size()>0) {
                HistoricoVagaDialogFragment historicoVagaDialogFragment = new HistoricoVagaDialogFragment();
                historicoVagaDialogFragment.show(getFragmentManager(),"sobre");
            }

        }


    }





}
