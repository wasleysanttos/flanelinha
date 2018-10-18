package com.example.wasleysantos.flanelinha.fragment;



import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wasleysantos.flanelinha.HomeActivity;
import com.example.wasleysantos.flanelinha.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import util.ConexaoInternet;
import util.Contas;
import util.CriptografarSenha;
import com.example.wasleysantos.flanelinha.model.Usuario;
import com.example.wasleysantos.flanelinha.view.EmailText;
import com.example.wasleysantos.flanelinha.view.PalavraSecretaText;
import com.example.wasleysantos.flanelinha.view.PasswordText;
import util.RequisicaoHttp;

public class CriarUsuarioFragment extends Fragment {

    //label
    private PasswordText edPassword;
    private PalavraSecretaText edPalavra;

    private CriarUsuarioTask mAuthTask = null;
    private View mProgressView;
    private Boolean userExiste = false;

    private Spinner spinnerEmail;
    private ArrayList<Contas> accountsList;
    ArrayAdapter<Contas> adapter;
    private String email;
    private EmailText emailText;


    //contrutor padrão
    public CriarUsuarioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this com.example.wasleysantos.flanelinha.fragment
        View view  =  inflater.inflate(R.layout.fragment_criar_usuario, container, false);

        emailText = new EmailText(getActivity());
        getContas();
        spinnerEmail = (Spinner) view.findViewById(R.id.spinnerContas);
        configurarSpinner();



        mProgressView = view.findViewById(R.id.new_user_progress);
        edPassword = (PasswordText) view.findViewById(R.id.edPassword_criar_usuario);
        edPalavra = (PalavraSecretaText) view.findViewById(R.id.edPalavraSecreta_criar_usuario);

        Button btnConfirmar = (Button) view.findViewById(R.id.btnCadastrarUsuario);
        Button btnCancelar = (Button) view.findViewById(R.id.btnCancelarCadastroUsuario);

        edPassword.setText("wasleysanttos");
        edPalavra.setText("wasleysanttos");


        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean conexao = getConexaoInternet();

                if (conexao) {


                // Reset errors
                edPassword.setError(null);

                String password = edPassword.getText().toString();
                String palavra = edPalavra.getText().toString();

                boolean cancel = false;
                View focusView = null;

                 // Check for a valid password, if the user entered one.
                 if (TextUtils.isEmpty(password)) {
                        edPassword.setError(getString(R.string.error_field_required));
                        focusView = edPassword;
                        cancel = true;
                    }

                    // Check for a valid password, if the user entered one.
                    else if (!edPassword.isPasswordValid(password)) {
                        edPassword.setError(getString(R.string.error_invalid_password));
                        focusView = edPassword;
                        cancel = true;
                    }

                 if (edPalavra.getText().toString().equals("")) {
                     edPalavra.setError(getString(R.string.error_field_required));
                     focusView = edPalavra;
                     cancel = true;
                 }

                // Check for a valid email address.
                if (TextUtils.isEmpty(email)) {
                    focusView = spinnerEmail;
                    cancel = true;
                } else if (!emailText.isEmailValid(email)) {
                    focusView = spinnerEmail;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                }
                else {
                    String passwordCriptografado = getCriptogramaSenha(password);
                    Usuario usuario;
                    usuario = new Usuario(0, email, "wasley", passwordCriptografado,  palavra);

                    mAuthTask = new CriarUsuarioTask(usuario);
                    mAuthTask.execute((Usuario) null);


                }
            }
                else {
                    Toast.makeText(getActivity(), "Verifique sua conexão de internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
    }


    private ArrayList<Contas> getContas() {
        accountsList = new ArrayList<Contas>();
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            //Getting all registered Google Accounts;
            Account[] accounts = AccountManager.get(getActivity()).getAccountsByType("com.google");
//            Account[] accounts = AccountManager.get(getActivity()).getAccounts();
            for (Account account : accounts) {
                Contas item = new Contas(account.type, account.name);
//                Contas item = new Contas(account.name);
                accountsList.add(item);
            }
        } catch (Exception e) {
            Log.i("Exception", "Exception:" + e);
        }

        return accountsList;
    }

    //criar classe de tipo de vaga para retornar lista de tipos de vagas
    private void configurarSpinner() {
        Contas contas = new Contas("0","Selecione uma conta de email");
        accountsList.add(0,contas);

        //chama aqui o metodo que busca lista de contas
        adapter =  new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accountsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEmail.setAdapter(adapter);

        spinnerEmail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View arg1, int arg2, long arg3) {

                spinnerEmail.getSelectedItemPosition();
                email = spinnerEmail.getItemAtPosition(arg2).toString();
            }
            @Override
            public void onNothingSelected(AdapterView arg0) {

            }
        });

    }

    private boolean getConexaoInternet() {
        boolean conexao = false;

        ConexaoInternet consultaConexaoInternet = new ConexaoInternet(getActivity());

        try {
            conexao = consultaConexaoInternet.verificaConexao();
        } catch (ConnectException e) {
            e.printStackTrace();
        }
        return conexao;
    }

    @Nullable
    private String getCriptogramaSenha(String password) {
        String passwordCriptografado = null;

        CriptografarSenha criptografarSenha = new CriptografarSenha();

        try {
            passwordCriptografado = criptografarSenha.criptografaSenha(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return passwordCriptografado;
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().finish();

    }

    private void showProgress(final boolean show) {
        if (show){
            mProgressView.setVisibility(View.VISIBLE);
        }
        else {
            mProgressView.setVisibility(View.GONE);
        }

    }

    public class CriarUsuarioTask extends AsyncTask<Usuario, Void, Boolean> {
        private Usuario mUsuarioInterno;

        public CriarUsuarioTask(Usuario usuario) {
            mUsuarioInterno = new Usuario( usuario.getId(),
                    usuario.getEmail(),
                    usuario.getNome(),
                    usuario.getPassword(),
                    usuario.getPalavraSecreta()
            );
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(Usuario... params) {
            RequisicaoHttp requisicaoHttp = new RequisicaoHttp();
            Usuario users;

            users = new Usuario(mUsuarioInterno.getId(),
                    mUsuarioInterno.getEmail(),
                    mUsuarioInterno.getNome(),
                    mUsuarioInterno.getPassword(),
                    mUsuarioInterno.getPalavraSecreta());

            try {
                users = requisicaoHttp.consultarUsuarioWebService(mUsuarioInterno);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (users == null){ //retornando NULL significa que não existe usuario entao pode chamar o metodo para cadastrar
                try {
                    requisicaoHttp.cadastrarUsuarioWebService(mUsuarioInterno);
                    //set true para indicar que agora o usuario existe
                    userExiste = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return userExiste;
            }

            return false;
        }



        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);

            userExiste = success;

            if (success) {

                edPassword.setText("");
                edPalavra.setText("");

                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("usuariologado", mUsuarioInterno);
                startActivity(intent);
            }
            else {
                showProgress(false);
                Toast.makeText(getContext(), "Já existe uma conta com esse email!" , Toast.LENGTH_SHORT).show();
            }

        }



    }

}
