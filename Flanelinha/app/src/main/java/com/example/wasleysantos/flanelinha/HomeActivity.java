package com.example.wasleysantos.flanelinha;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.ConnectException;

import com.example.wasleysantos.flanelinha.fragment.AlterarUsuarioFragment;
import com.example.wasleysantos.flanelinha.fragment.HistoricoVagasFragment;
import com.example.wasleysantos.flanelinha.fragment.VagasOcupadasFragment;
import com.example.wasleysantos.flanelinha.fragment.TipoVagaFragment;
import util.ConexaoInternet;
import util.Constantes;
import com.example.wasleysantos.flanelinha.model.TipoVaga;
import com.example.wasleysantos.flanelinha.model.Usuario;
import com.example.wasleysantos.flanelinha.model.Vaga;

public class HomeActivity extends AppCompatActivity
        implements
        TipoVagaFragment.AoClicarNoTipoDeVaga,
        HistoricoVagasFragment.AoClicarNoHistoricoDeVaga,
        NavigationView.OnNavigationItemSelectedListener{

    private Usuario mUsuario;
    public Context context = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);






        getTipoVagaListFragment();

        Intent intent = getIntent();
        mUsuario = (Usuario) intent.getSerializableExtra(Constantes.EXTRA_USUARIO);


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();


            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }


    }






@Override
    protected void onRestart() {
        super.onRestart();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Toast.makeText(HomeActivity.this, BuildConfig.VERSION_NAME , Toast.LENGTH_SHORT).show();
                break;
        }



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        boolean conexao = isConexao();

        if (conexao) {
                // Handle navigation view item clicks here.
            int id = item.getItemId();

                if (id == R.id.action_Buscar_Vaga) {
                    Intent intent = new Intent(this, MapaActivity.class);
                    intent.putExtra(Constantes.EXTRA_USUARIO, mUsuario);
                    startActivity(intent);
                }

                else if (id == R.id.action_Check_Out){
                    getVagasOcupadasUsuarioFragment();
                }
                else if (id == R.id.action_Historico_Vaga){
                    getHistoricoVagasFragment();
                }

                else if (id == R.id.action_Tipo_Vaga){
                    getTipoVagaListFragment();

                }

                else if (id == R.id.action_my_dados){
                    getAlterarUsuarioFragment();
                }
                else if (id == R.id.nav_send){
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setType("text/html");
                    intent.setData(Uri.parse("mailto:flanelinhadigital@gmail.com"));


                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }

                }
                else if (id == R.id.action_login_logout){
                    finish();

                }

            }

            else {
                Toast.makeText(this, "Verifique sua conexão de internet!", Toast.LENGTH_SHORT).show();
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private boolean isConexao() {
        boolean conexao = false;

        ConexaoInternet consultaConexaoInternet = new ConexaoInternet(this);

        try {
            conexao = consultaConexaoInternet.verificaConexao();
        } catch (ConnectException e) {
            e.printStackTrace();
        }
        return conexao;
    }

    public void getAlterarUsuarioFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment alterarUsuarioFragment = new AlterarUsuarioFragment();

        transaction.replace(R.id.fragment_home, alterarUsuarioFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void getTipoVagaListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        TipoVagaFragment tipoVagaListFragment = new TipoVagaFragment();


        transaction.replace(R.id.fragment_home, tipoVagaListFragment);
        transaction.addToBackStack(null);
        transaction.commit();


    }

    public void getHistoricoVagasFragment() {

        Bundle parametros = new Bundle();
        parametros.putSerializable(Constantes.EXTRA_USUARIO, mUsuario);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();


        Fragment historicoVagasListFragment = new HistoricoVagasFragment();
        historicoVagasListFragment.setArguments(parametros);

        transaction.replace(R.id.fragment_home, historicoVagasListFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void getVagasOcupadasUsuarioFragment() {

        Bundle parametros = new Bundle();
        parametros.putSerializable(Constantes.EXTRA_USUARIO, mUsuario);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();


        Fragment minhasVagasOcupadasFragment = new VagasOcupadasFragment();
        minhasVagasOcupadasFragment.setArguments(parametros);

        transaction.replace(R.id.fragment_home, minhasVagasOcupadasFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void clicouNoTipoVaga(TipoVaga tipoVaga, Usuario usuario) {
        Intent intent = new Intent(this,MapaActivity.class);
        intent.putExtra(Constantes.EXTRA_TIPO_VAGA, tipoVaga);

        intent.putExtra(Constantes.EXTRA_USUARIO, usuario);
        startActivity(intent);
    }

    @Override
    public void clicouNaVaga(Vaga vaga) {
        Intent intent = new Intent(this,MapaActivity.class);
        intent.putExtra(Constantes.EXTRA_HISTORICO, vaga);
        startActivity(intent);

    }

}

