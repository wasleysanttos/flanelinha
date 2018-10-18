package com.example.wasleysantos.flanelinha;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import util.ConexaoInternet;
import util.Constantes;
import util.RequisicaoHttp;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;

import util.CriptografarSenha;
import com.example.wasleysantos.flanelinha.model.Usuario;
import com.example.wasleysantos.flanelinha.view.EmailText;
import com.example.wasleysantos.flanelinha.view.PasswordText;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCOUNT_MANAGER;
import static android.Manifest.permission.GET_ACCOUNTS;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private Usuario usuario;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EmailText mEmailView;
    private PasswordText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        mEmailView = (EmailText) findViewById(R.id.email);
        mayRequestLocation();

        mPasswordView = (PasswordText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(this);

        Button btNovousuario = (Button) findViewById(R.id.novo_usuario);
        btNovousuario.setOnClickListener(this);

        Button btResgatarsenha = (Button) findViewById(R.id.resgatar_senha);
        btResgatarsenha.setOnClickListener(this);


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mEmailView.setText("wasleyguittar@gmail.com");
        mPasswordView.setText("wasleysanttos");

    }

    public void conexao (){
        Boolean conexao = false;

        ConexaoInternet consultaConexaoInternet = new ConexaoInternet(this);

        try {
            conexao = consultaConexaoInternet.verificaConexao();
        } catch (ConnectException e) {
            e.printStackTrace();
        }

        if (!conexao){
            Toast.makeText(this, "Verifique sua conexão de internet!", Toast.LENGTH_SHORT).show();
            finish();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        usuario = null;

        conexao ();


    }


    @Override
    protected void onRestart() {
        super.onRestart();
        usuario = null;

    }




    @Override
    protected void onStop() {
        super.onStop();
        showProgress(false);
    }


    @Override
    public void onClick(View v) {
        boolean conexao = false;
        ConexaoInternet consultaConexaoInternet = new ConexaoInternet(this);

        try {
            conexao = consultaConexaoInternet.verificaConexao();
        } catch (ConnectException e) {
            e.printStackTrace();
        }

        if (conexao) {
                Intent intent;

                switch (v.getId()) {
                    case R.id.email_sign_in_button:
                        attemptLogin();
                        break;
                    case R.id.novo_usuario:
                        intent = new Intent(getApplicationContext(), UsuarioActivity.class);
                        intent.putExtra(Constantes.EXTRA_NOVO_USUARIO, true);
                        startActivity(intent);
                        break;
                    case R.id.resgatar_senha:
                        intent = new Intent(getApplicationContext(), UsuarioActivity.class);
                        intent.putExtra(Constantes.EXTRA_RESGATAR_PALAVRA, true);
                        startActivity(intent);
                        break;
                }
            }
            else {
            Toast.makeText(this, "Verifique sua conexão de internet!", Toast.LENGTH_SHORT).show();
            }
    }


    private boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (
                (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }


        if (
                (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) &&
                (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION) )
                && (shouldShowRequestPermissionRationale(GET_ACCOUNTS) )
                ) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,GET_ACCOUNTS}, Constantes.EXTRA_REQUEST_LOCATION);
                        }
                    });
        }

        else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,ACCOUNT_MANAGER,GET_ACCOUNTS}, Constantes.EXTRA_REQUEST_LOCATION);
        }
        return false;
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == Constantes.EXTRA_REQUEST_LOCATION) {
            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mayRequestLocation();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && !mPasswordView.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmailView.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private String passwordCriptografado;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            CriptografarSenha criptografarSenha = new CriptografarSenha();


            try {
                passwordCriptografado = criptografarSenha.criptografaSenha(mPassword);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            usuario = new Usuario(1,mEmail,null, passwordCriptografado,null);

            //chama conexão com webservice - descomentar quando for necessario logar via webservice

            RequisicaoHttp requisicaoHttp = new RequisicaoHttp();


            try {
                usuario = requisicaoHttp.consultarUsuarioWebService(usuario);
            } catch (Exception e) {
                e.printStackTrace();
                return  false;
            }



            if (usuario != null){
                return true;
            }

            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
//            showProgress(false);

            if (success) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra(Constantes.EXTRA_USUARIO, usuario);
                startActivity(intent);

            } else {
                showProgress(false);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


}

