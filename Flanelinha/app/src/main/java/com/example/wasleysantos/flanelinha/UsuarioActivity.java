package com.example.wasleysantos.flanelinha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.wasleysantos.flanelinha.fragment.CriarUsuarioFragment;
import com.example.wasleysantos.flanelinha.fragment.EsqueciMinhaSenhaFragment;
import util.Constantes;

/*
* Activity responsavel por gerenciar fragments de novo usuario, alterar dados de usuario e resgatar senha
*
*/

public class UsuarioActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_usuario);

        Intent intent = getIntent();

        Boolean btn_novoUsuario = intent.getBooleanExtra(Constantes.EXTRA_NOVO_USUARIO, false);
        Boolean btn_recuperarSenha = intent.getBooleanExtra(Constantes.EXTRA_RESGATAR_PALAVRA, false);

        if (btn_novoUsuario) {
            //metodo que chama o com.example.wasleysantos.flanelinha.fragment que será responsável para registrar novo usuario
            getRegistrarNovoUsuarioFragment();
        } else if (btn_recuperarSenha) {
            getRecuperarSenhaFragment();
        }

    }

    public void getRegistrarNovoUsuarioFragment() {

        Fragment cadastrarUsuarioFragment = new CriarUsuarioFragment();

        if (cadastrarUsuarioFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // Replace whatever is in the fragment_container view with this com.example.wasleysantos.flanelinha.fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.fragment_usuario, cadastrarUsuarioFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }

    }

    public void getRecuperarSenhaFragment() {


        Fragment recuperarSenhaFragment = new EsqueciMinhaSenhaFragment();

        if (recuperarSenhaFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Replace whatever is in the fragment_container view with this com.example.wasleysantos.flanelinha.fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.fragment_usuario, recuperarSenhaFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }

    }


}

