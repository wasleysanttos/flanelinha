package com.example.wasleysantos.flanelinha.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wasleysantos.flanelinha.R;
import util.ConexaoInternet;
import util.Constantes;
import util.RetornaEnderecoTask;
import com.example.wasleysantos.flanelinha.model.Vaga;
import util.RequisicaoHttp;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class VagaExistenteFragment extends Fragment {

    private Vaga mVaga;

    public ProgressDialog pdialog = null;
    public Context context = null;

    public VagaExistenteFragment() {

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
        View view  =   inflater.inflate(R.layout.fragment_vaga_existente, container, false);

        ImageView imgCheckIn = (ImageView) view.findViewById(R.id.action_Check_In);
        ImageView imgCheckInOther = (ImageView) view.findViewById(R.id.action_Check_In_others);
        ImageView imgInexistente = (ImageView) view.findViewById(R.id.imgInexistente);

        imgCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean conexao = isConexao();
                if (conexao ) {
                    //Seta status que representa vaga ocupada pelo usuário
                    mVaga.setStatus(1);
                    preencheEndereco ();
                    realizaCheckIn ();

                }
                else {
                    Toast.makeText(getActivity(), "Verifique sua conexão de internet!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        imgCheckInOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean conexao = isConexao();

                if (conexao ) {
                    //Seta status que representa vaga ocupada por outro usuário
                    mVaga.setStatus(2);
                    preencheEndereco ();
                    realizaCheckIn ();

                }
                else {
                    Toast.makeText(getActivity(), "Verifique sua conexão de internet!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        imgInexistente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean conexao = isConexao();
                if (conexao) {
                    //Seta status que representa vaga inexistente
                    mVaga.setStatus(3);
                    preencheEndereco ();
                    realizaCheckIn ();

                }
                else {
                    Toast.makeText(getActivity(), "Verifique sua conexão de internet!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return view;
    }

    private boolean isConexao() {
        boolean conexao = false;
        ConexaoInternet consultaConexaoInternet = new ConexaoInternet(getActivity());


        try {
            conexao = consultaConexaoInternet.verificaConexao();
        } catch (ConnectException e) {
            e.printStackTrace();
        }
        return conexao;
    }

    public void preencheEndereco (){

        //inicia barra de progresso (só será visivel se conexão estiver lenta)
        pdialog = ProgressDialog.show(getActivity(), "", "Por favor aguarde...", true);

//        mVaga.setBairro("teste");
        RetornaEnderecoTask retornaEnderecoTask = new RetornaEnderecoTask(getActivity(),mVaga);
        retornaEnderecoTask.execute(mVaga);



    }

    public void realizaCheckIn (){

        OcuparVagaTask mAuthTask = new OcuparVagaTask();
        mAuthTask.execute(mVaga);

    }


//pode ser transformada em uma thred pois essa informação nao atualiza a UI
    public class OcuparVagaTask extends AsyncTask<Vaga, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Vaga... params) {

            Date dataAgora = new Date(System.currentTimeMillis());
            mVaga.setData_ultima_ocupacao(dataAgora);


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

//            showProgress(false);
            pdialog.dismiss();

            if (success) {

                if ( mVaga.getStatus() == 1){
                    Toast.makeText(getActivity(), "Check-In realizado! Essa vaga ficará indisponivel para outro veiculo.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
                else if (mVaga.getStatus() == 2) {
                    Toast.makeText(getActivity(), "Obrigado por nos informar. Nós colocaremos essa vaga como indisponível.", Toast.LENGTH_LONG).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                else if (mVaga.getStatus() == 3){
                    Toast.makeText(getActivity(), "Obrigado por nos informar. Nós colocaremos essa como inexistente.", Toast.LENGTH_LONG).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }

            else {
                Toast.makeText(getActivity(), "Desculpe, mas não foi possivel realizar o check-in nessa vaga!", Toast.LENGTH_LONG).show();
            }

        }



    }

}



