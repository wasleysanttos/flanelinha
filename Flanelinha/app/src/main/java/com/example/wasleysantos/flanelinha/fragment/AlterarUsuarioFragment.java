package com.example.wasleysantos.flanelinha.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.wasleysantos.flanelinha.R;

import util.ConexaoInternet;
import util.Constantes;
import util.CriptografarSenha;
import com.example.wasleysantos.flanelinha.model.Usuario;
import com.example.wasleysantos.flanelinha.view.PalavraSecretaText;
import com.example.wasleysantos.flanelinha.view.PasswordText;
import util.RequisicaoHttp;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlterarUsuarioFragment extends Fragment {

    //label
    private PasswordText edPassword;
//    private EditText edNome;
    private PalavraSecretaText edPalavra;
    private Usuario mUsuario;

    private AlterarUsuarioTask mAlteraUsuarioTask = null;
    private View mProgressView;


    public AlterarUsuarioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getActivity().getIntent();
        mUsuario = (Usuario) intent.getSerializableExtra(Constantes.EXTRA_USUARIO);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.example.wasleysantos.flanelinha.fragment
        View view  =  inflater.inflate(R.layout.fragment_alterar_usuario, container, false);

        mProgressView = view.findViewById(R.id.alterar_usuario_progress);

        edPassword = (PasswordText) view.findViewById(R.id.edPassword_alterar_usuario);
//        edNome = (EditText) view.findViewById(R.id.edNome_alterar_usuario);
        edPalavra = (PalavraSecretaText) view.findViewById(R.id.edPalavraSecreta_alterar_usuario);

        Button btnConfirmar = (Button) view.findViewById(R.id.btnConfirmaAlteracao);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean conexao = false;

                ConexaoInternet consultaConexaoInternet = new ConexaoInternet(getActivity());

                try {
                    conexao = consultaConexaoInternet.verificaConexao();
                } catch (ConnectException e) {
                    e.printStackTrace();
                }

                if (conexao) {
                        final String password = edPassword.getText().toString();
//                        final String nome = edNome.getText().toString();
                        final String palavra = edPalavra.getText().toString();
                        String passwordCriptografado = null;

                        CriptografarSenha criptografarSenha = new CriptografarSenha();

                        try {
                            passwordCriptografado = criptografarSenha.criptografaSenha(password);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        showProgress(true);

                        Usuario usuarioAlterado = new Usuario(mUsuario.getId(), mUsuario.getEmail(), null, passwordCriptografado,  palavra );

                        mAlteraUsuarioTask = new AlterarUsuarioTask(usuarioAlterado);
                        mAlteraUsuarioTask.execute((Usuario) null);
                    }

                    else {
                        Toast.makeText(getActivity(), "Verifique sua conexão de internet!", Toast.LENGTH_SHORT).show();
                    }

            }
        });


        return view;
    }

    private void showProgress(final boolean show) {
        if (show ){
            mProgressView.setVisibility(View.VISIBLE);
        }
        else {
            mProgressView.setVisibility(View.GONE);
        }

    }

/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GerenciarUsuarioListener) {
            gerenciarUsuarioListener = (GerenciarUsuarioListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement gerenciarUsuarioListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        gerenciarUsuarioListener = null;
    }

*/


    public class AlterarUsuarioTask extends AsyncTask<Usuario, Void, Void> {
        private Usuario usuario;

        AlterarUsuarioTask(Usuario mmUsuario) {
            usuario = new Usuario(mmUsuario.getId(),
                    mmUsuario.getEmail(),
                    mmUsuario.getNome(),
                    mmUsuario.getPassword() ,
                    mmUsuario.getPalavraSecreta());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Usuario... params) {

            RequisicaoHttp requisicaoHttp = new RequisicaoHttp();

                try {
                    usuario = requisicaoHttp.alterarUsuarioWebService(usuario);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mAlteraUsuarioTask = null;
            showProgress(false);

            if (usuario != null){
                mUsuario = usuario;
                Toast.makeText(getActivity(), "Usuário alterado com sucesso!", Toast.LENGTH_SHORT).show();

                getTipoVagaListFragment();
            }


       }

    void getTipoVagaListFragment() {
        Fragment tipoVagaListFragment = new TipoVagaFragment();

        if (tipoVagaListFragment != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_home, tipoVagaListFragment);
            transaction.addToBackStack(null);
            transaction.commit();


        }

    }


    }



}
