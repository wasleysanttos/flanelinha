package com.example.wasleysantos.flanelinha.fragment;

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
import android.widget.Toast;

import com.example.wasleysantos.flanelinha.R;
import com.example.wasleysantos.flanelinha.adapter.VagasOcupadasAdapter;
import com.example.wasleysantos.flanelinha.dialog.VagasOcupadasDialogFragment;
import util.Constantes;
import com.example.wasleysantos.flanelinha.model.Usuario;
import com.example.wasleysantos.flanelinha.model.Vaga;
import util.RequisicaoHttp;

import java.io.IOException;
import java.util.ArrayList;


public class VagasOcupadasFragment extends ListFragment {



    public VagasOcupadasFragment() {
        // Required empty public constructor
    }

    private ArrayList<Vaga> mMinhasVagas;
    private VagasOcupadasAdapter mAdapter;

    private Usuario mUsuario;

    public ProgressDialog pdialog = null;
    public Context context = getActivity();
    public boolean contador;
    ListView mListView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);


        mUsuario = (Usuario) getArguments().getSerializable(Constantes.EXTRA_USUARIO);
        setHasOptionsMenu(true);

        contador = false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
        mMinhasVagas = new ArrayList<>();

        MinhaListaDeVagasOcupadasTask mAuthTask = new MinhaListaDeVagasOcupadasTask(mUsuario);
        mAuthTask.execute(mUsuario);

//click long sera usado para mostrar mais detalhes da vaga
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Vaga vaga = (Vaga) adapterView.getItemAtPosition(i);
                if(vaga != null){
                    LiberarVagaTask mAuthTask = new LiberarVagaTask(vaga);
                    mAuthTask.execute();
                }


                return true;
            }

        });

        limparBusca();

        final int PADDING = 8;
        TextView txtHeader = new TextView(getActivity());

        txtHeader.setBackgroundColor(Color.GRAY);
        txtHeader.setTextColor(Color.WHITE);
        txtHeader.setText(R.string.title_activity_checkout);
        txtHeader.setPadding(PADDING, PADDING,0,PADDING);

        mListView.addHeaderView(txtHeader);


    }

    public void limparBusca() {
        mAdapter = new VagasOcupadasAdapter(getActivity(), mMinhasVagas);
        setListAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }


    public class MinhaListaDeVagasOcupadasTask extends AsyncTask<Usuario, Void, ArrayList<Vaga> > {

        private ArrayList<Vaga> mmVagas = new ArrayList<>();;
        private Usuario mmUsuario ;

        public MinhaListaDeVagasOcupadasTask(Usuario mUsuario) {
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

            pdialog = ProgressDialog.show(getActivity(), "", "Por favor aguarde, estamos verificando se você está ocupando alguma vaga...", true);

        }

        @Override
        protected ArrayList <Vaga> doInBackground(Usuario... params) {

            //buscar lista de vagas
            RequisicaoHttp requisicaoHttp = new RequisicaoHttp();

            try {
                mmVagas =  requisicaoHttp.consultaVagasOcupadasUsuarioWebService(mmUsuario);
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

            if (mMinhasVagas.size()>0  && !contador ){
                VagasOcupadasDialogFragment vagasOcupadasDialogFragment = new VagasOcupadasDialogFragment();
                vagasOcupadasDialogFragment.show(getFragmentManager(),"sobre");
            }
            else if ( mMinhasVagas.size() == 0 ){
                mListView.setVisibility(View.VISIBLE);
            }


        }


    }


    public class LiberarVagaTask extends AsyncTask<Vaga, Void, Boolean> {
        private Vaga mVaga;

        public LiberarVagaTask(Vaga mVaga) {
            this.mVaga = mVaga;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdialog = ProgressDialog.show(getActivity(), "", "Por favor aguarde, essa vaga será liberada em breve...", true);

        }

        @Override
        protected Boolean doInBackground(Vaga... params) {

            mVaga = new Vaga(mVaga.getId(),mVaga.getLatitude(),mVaga.getLongitude(),0,null,mUsuario,null,null,null,null,null,null);

            RequisicaoHttp requisicaoHttp = new RequisicaoHttp();

            try {
                requisicaoHttp.ocuparVagaWebService(mVaga);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }



        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);

            pdialog.dismiss();

            if (success){
                mMinhasVagas.remove(mVaga);
                mAdapter.notifyDataSetChanged();

                contador = true;

                MinhaListaDeVagasOcupadasTask mAuthTask = new MinhaListaDeVagasOcupadasTask(mUsuario);
                mAuthTask.execute(mUsuario);

                Toast.makeText(getContext(), "Vaga liberada com sucesso!", Toast.LENGTH_SHORT).show();
            }



        }



    }


}
