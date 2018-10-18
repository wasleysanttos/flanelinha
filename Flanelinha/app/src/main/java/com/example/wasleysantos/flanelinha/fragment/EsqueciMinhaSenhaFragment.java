package com.example.wasleysantos.flanelinha.fragment;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.wasleysantos.flanelinha.HomeActivity;
import com.example.wasleysantos.flanelinha.R;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import util.ConexaoInternet;
import util.Constantes;
import com.example.wasleysantos.flanelinha.model.Usuario;
import com.example.wasleysantos.flanelinha.view.EmailText;
import com.example.wasleysantos.flanelinha.view.PalavraSecretaText;
import util.RequisicaoHttp;

/**
 * essa com.example.wasleysantos.flanelinha.fragment deverá enviar um email para o usuario com a senha atual dele
 */
public class EsqueciMinhaSenhaFragment extends Fragment {

    private EmailText edEmail;
    private PalavraSecretaText edPalavra;

    private Button btnEnviarPalavraSecreta;
    private Button btnConfirmar;

    private Usuario mUsuario;
    private RecuperaPalavraSecretaTask recuperaPalavraSecretaTask = null;
    private String email = null;
    public Session session = null;
    public ProgressDialog pdialog = null;
    public Context context = null;


    public EsqueciMinhaSenhaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_esqueci_minha_senha, container, false);

        //oculta campos e botões
        edPalavra = (PalavraSecretaText) view.findViewById(R.id.edPalavraSecreta);
        edPalavra.setVisibility(View.GONE);
        btnConfirmar = (Button) view.findViewById(R.id.btnConfirmaUsuario);
        btnConfirmar.setVisibility(View.GONE);
        Button btnCancelar = (Button) view.findViewById(R.id.btnRetornaUsuario);
        btnCancelar.setVisibility(View.GONE);

       //exibindo campos e botões
        edEmail = (EmailText) view.findViewById(R.id.edEmail);
        btnEnviarPalavraSecreta = (Button) view.findViewById(R.id.btnReceberPalavraSecreta);

        //evento de clique nos botões
        btnEnviarPalavraSecreta.setOnClickListener(new View.OnClickListener(){
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
                    email = edEmail.getText().toString();

                    // Reset errors.
                    edEmail.setError(null);

                    boolean cancel = false;
                    View focusView = null;

                    // Check for a valid email address.
                    if (TextUtils.isEmpty(email)) {
                        edEmail.setError(getString(R.string.error_field_required));
                        focusView = edEmail;
                        cancel = true;
                    } else if (!edEmail.isEmailValid(email)) {
                        edEmail.setError(getString(R.string.error_invalid_email));
                        focusView = edEmail;
                        cancel = true;
                    }

                    if (cancel) {
                        focusView.requestFocus();
                    } else {
                        pdialog = ProgressDialog.show(getActivity(), "", "Por favor aguarde...", true);
//                    showProgress(true);
                        enviarEmail();

                        recuperaPalavraSecretaTask = new RecuperaPalavraSecretaTask(email);
                        recuperaPalavraSecretaTask.execute();


                    }
                } else {
                    Toast.makeText(getActivity(), "Verifique sua conexão de internet!", Toast.LENGTH_SHORT).show();
                }

            }
        });


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

                    String palavraSecreta = edPalavra.getText().toString();
                    Usuario usuarioExiste = new Usuario(0, email, null, null, palavraSecreta);

                    if (mUsuario.getEmail().equals(usuarioExiste.getEmail()) && mUsuario.getPalavraSecreta().equals(usuarioExiste.getPalavraSecreta())) {
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.putExtra(Constantes.EXTRA_USUARIO, mUsuario);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getActivity(), "Palavra secreta invalida!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Verifique sua conexão de internet!", Toast.LENGTH_SHORT).show();
                }
                }
        });

        return view;

    }


    @Override
    public void onPause() {
        super.onPause();

        getActivity().finish();

    }
    public void enviarEmail()  {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Constantes.EXTRA_EMAIL, Constantes.EXTRA_SENHA_EMAIL);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public class RecuperaPalavraSecretaTask extends AsyncTask<String, Void, Usuario> {
        private String email;

        RecuperaPalavraSecretaTask(String email) {
                this.email = email;
        }



        @Override
        protected Usuario doInBackground(String... params) {
            Usuario userExistente;

            userExistente = new Usuario(0,
                    email,
                    null,
                    null,
                    null);



            RequisicaoHttp requisicaoHttp = new RequisicaoHttp();


            try {
                userExistente = requisicaoHttp.consultarUsuarioWebService(userExistente);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (userExistente != null && userExistente.getId() != 0){
                try{
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(Constantes.EXTRA_EMAIL));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                    message.setSubject(Constantes.EXTRA_PALAVRA_SECRETA);
                    message.setContent("Essa é sua palavra secreta: " + userExistente.getPalavraSecreta() ,
                            "text/html; charset=utf-8");
                    Transport.send(message);
                } catch(Exception e) {
                    e.printStackTrace();
                }

            }

            return userExistente;
        }


        @Override
        protected void onPostExecute(Usuario userResult) {
            recuperaPalavraSecretaTask = null;

            if (userResult != null){
                mUsuario = new Usuario(userResult.getId(),userResult.getEmail(),userResult.getNome(),userResult.getPassword(),userResult.getPalavraSecreta());
                edEmail.setEnabled(false);
                edPalavra.setVisibility(View.VISIBLE);
                btnEnviarPalavraSecreta.setText(R.string.reenviar_palavra);
                btnConfirmar.setVisibility(View.VISIBLE);
            }
            else {

                Toast.makeText(getActivity(),"Não existe usuário cadastrado com esse email",Toast.LENGTH_SHORT).show();
                edEmail.requestFocus();
            }

//            showProgress(false);
            pdialog.dismiss();




        }
    }




}
