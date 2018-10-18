package com.example.wasleysantos.flanelinha.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wasleysantos.flanelinha.R;
import util.ConexaoInternet;
import util.Constantes;
import util.RetornaEnderecoTask;

import com.example.wasleysantos.flanelinha.adapter.TipoVagaAdapter;
import com.example.wasleysantos.flanelinha.model.TipoVaga;
import com.example.wasleysantos.flanelinha.model.Vaga;
import util.RequisicaoHttp;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class NovaVagaFragment extends Fragment {

    private View mProgressView;

    public ProgressDialog pdialog = null;
    public Context context = null;

    private Spinner spinner;
    ArrayAdapter<TipoVaga> adapter;
    TipoVagaAdapter novoAdapter;

    private CriarVagaTask mAuthTask = null;

    private Vaga mVaga;
    private TipoVaga mTipoVaga;

    private ArrayList<TipoVaga> mTiposVaga;

    public NovaVagaFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVaga = (Vaga) getArguments().getSerializable(Constantes.EXTRA_VAGA);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this com.example.wasleysantos.flanelinha.fragment
        View view  =   inflater.inflate(R.layout.fragment_nova_vaga, container, false);

        mTiposVaga = new ArrayList<> ();

        TipoVagaTask mTipoVagaAuthTask = new TipoVagaTask();
        mTipoVagaAuthTask.execute();


        mProgressView = view.findViewById(R.id.registre_nova_vaga);
        ImageView btnCheckIn = (ImageView) view.findViewById(R.id.action_Check_In_New);
        spinner = (Spinner) view.findViewById(R.id.spinner);
//        btnCheckIn.setVisibility(view.GONE);
//        configurarSpinner();
        configurarNovoSpinner();

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean conexao = false;

                ConexaoInternet consultaConexaoInternet = new ConexaoInternet(getActivity());

                try {
                    conexao = consultaConexaoInternet.verificaConexao();
                } catch (ConnectException e) {
                    e.printStackTrace();
                }

                if (conexao ) {

                    if (mTipoVaga.getId() !=  0){
                        //Seta status que representa vaga ocupada pelo usuário
                        mVaga.setStatus(1);
                        mVaga.setEndereco("buscar na classe de RetornaEndereco");
                        //inicia barra de progrsso
                        pdialog = ProgressDialog.show(getActivity(), "", "Por favor aguarde, registrando vaga...", true);

                        //preenche endereço referente a localização
                        RetornaEnderecoTask retornaEnderecoTask = new RetornaEnderecoTask(getActivity(),mVaga);
                        retornaEnderecoTask.execute(mVaga);


                        mAuthTask = new CriarVagaTask();
                        mAuthTask.execute(mVaga);
                    }
                    else {
                        Toast.makeText(getActivity(), "Por favor selecione um tipo de vaga", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getActivity(), "Verifique sua conexão de internet!", Toast.LENGTH_SHORT).show();
                }

            }
        });



        return view;
    }

    //criar classe de tipo de vaga para retornar lista de tipos de vagas
    private void configurarSpinner() {
//        showProgress(true);
//        TipoVaga tpVaga = new TipoVaga(0,"Selecione um tipo de vaga");
//        mTiposVaga.add(0,tpVaga);


        //chama aqui o metodo que busca lista de tipo de vagas
        adapter =  new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mTiposVaga);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View arg1, int arg2, long arg3) {
//                spinner.getSelectedItemId();
                mTipoVaga = new TipoVaga(parent.getSelectedItemId()+1,"no application");
                mVaga.setTipovaga(mTipoVaga);

            }
            @Override
            public void onNothingSelected(AdapterView arg0) {

            }
        });

    }


    private void configurarNovoSpinner() {

        TipoVaga tpVaga = new TipoVaga(0,"Selecione um tipo de vaga");
        mTiposVaga.add(0,tpVaga);

        novoAdapter =  new TipoVagaAdapter(getActivity(),mTiposVaga);
        spinner.setAdapter(novoAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View arg1, int arg2, long arg3) {
//                spinner.getSelectedItemId();
                mTipoVaga = new TipoVaga(parent.getSelectedItemId()+1,"no application");
                mVaga.setTipovaga(mTipoVaga);

            }
            @Override
            public void onNothingSelected(AdapterView arg0) {

            }
        });

    }


    private void showProgress(final boolean show) {
        if (show ){
            mProgressView.setVisibility(View.VISIBLE);
        }
        else {
            mProgressView.setVisibility(View.GONE);
        }

    }


    public class CriarVagaTask extends AsyncTask<Vaga, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Vaga... params) {

            Date dataAgora = new Date(System.currentTimeMillis());
            mVaga.setData_vaga(dataAgora);

            RequisicaoHttp requisicaoHttp = new RequisicaoHttp();

            try {
                requisicaoHttp.criarVagaWebService(mVaga);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }



        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);

            showProgress(false);

            if (success) {

                Toast.makeText(getActivity(), "Vaga criada com sucesso!", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
            else {
                Toast.makeText(getActivity(), "Desculpe, mas não foi possivel registrar essa vaga. Tente novamente!", Toast.LENGTH_LONG).show();

            }

        }



    }

    public class TipoVagaTask extends AsyncTask<Void, Void, ArrayList<TipoVaga>> {

        private ArrayList<TipoVaga> mmTiposVagas = new ArrayList<>();

        @Override
        protected ArrayList<TipoVaga> doInBackground(Void... params) {
            RequisicaoHttp requisicaoHttp = new RequisicaoHttp();
            try {
                mmTiposVagas = requisicaoHttp.consultaTipoVagasWebService();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return  mmTiposVagas;
        }



        @Override
        protected void onPostExecute(ArrayList<TipoVaga> lista) {
            super.onPostExecute(lista);

            showProgress(false);
//            pdialog.dismiss();

            if (lista != null) {
                mTiposVaga.clear();
                // Removendo tipo de vaga empresarial:
                for(int i = 0; i <= lista.size(); i++)
                {
                    if(lista.get(i).getId() == 8)
                    {
                        lista.remove(i);
                       // Sai do loop.
                        break;
                    }
                }

                mTiposVaga.addAll(lista);


            }

        }

    }



}


