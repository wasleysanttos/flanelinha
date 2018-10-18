package com.example.wasleysantos.flanelinha;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.wasleysantos.flanelinha.fragment.NovaVagaFragment;
import com.example.wasleysantos.flanelinha.fragment.VagaExistenteFragment;
import util.ConexaoInternet;
import util.Constantes;
import com.example.wasleysantos.flanelinha.model.TipoVaga;
import com.example.wasleysantos.flanelinha.model.Usuario;
import com.example.wasleysantos.flanelinha.model.Vaga;

import util.RequisicaoHttp;
import util.RetornaEnderecoTask;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.ConnectException;
import java.util.ArrayList;

public class MapaActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private LatLng mOrigem;
    private Marker posicaoatual;
    private Vaga vagaExistente;
    private Vaga mVaga;
    private Vaga mVagaHistorico;
    private ArrayList<Vaga> listaVagaUsuarios = new ArrayList<>();
    private LatLng listadeVagasDisponiveis;
    private Usuario usuario;
    private AdView adView;

    private TipoVaga tipoVaga;
    LocationManager lManager;
    LocationListener lListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //recebe o parametro de usuario
        Intent intentMainActivity = getIntent();
        usuario = (Usuario) intentMainActivity.getSerializableExtra(Constantes.EXTRA_USUARIO);

        //recebe o parametro de tipo de vaga
        Intent intentTipoVaga = getIntent();
        tipoVaga = (TipoVaga) intentTipoVaga.getSerializableExtra(Constantes.EXTRA_TIPO_VAGA);
        Usuario usuarioVaga = (Usuario) intentTipoVaga.getSerializableExtra(Constantes.EXTRA_USUARIO);


        //recebe o parametro de vaga da tela de historico de vaga
        Intent intentHistorico = getIntent();
        mVagaHistorico = (Vaga) intentHistorico.getSerializableExtra(Constantes.EXTRA_HISTORICO);

        if (usuarioVaga != null) {
            usuario = usuarioVaga;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("TEST_DEVICE_ID")
                .build();
        adView.loadAd(adRequest);


    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }

        super.onPause();
    }


    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }


        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (adView != null) {
            adView.resume();
        }

        if (mVagaHistorico == null) {
            startGPS();
            //chama classe que atualiza marcadores na tela do usuario
            ListaDeVagasTask mAuthTask = new ListaDeVagasTask();
            mAuthTask.execute((Void) null);
        }

        super.onResume();
    }


    @Override
    protected void onRestart() {

        if (mVagaHistorico == null) {
            startGPS();

        }

        super.onRestart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (mVagaHistorico != null) {
            mMap.getUiSettings().setMapToolbarEnabled(true);
        } else {
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }


        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);

        if (mVagaHistorico != null) {
            adicionaMarkerHistorico();
        }

    }

    //Método que faz a leitura dos valores recebidos do GPS
    public void startGPS() {
        lListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                adicionaMarkerPosicaoAtual(location);
                //chama classe que atualiza marcadores na tela do usuario
                ListaDeVagasTask mAuthTask = new ListaDeVagasTask();
                mAuthTask.execute((Void) null);

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            public void onProviderEnabled(String provider) {

            }

            public void onProviderDisabled(String provider) {
                //falta adicionar um dialog informando que precisa ter acesso ao GPS
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 1);

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , 30000, 4, lListener);


    }

    private void adicionaMarkerPosicaoAtual(Location location) {
        //limpa  marcador
        mMap.clear();

        mOrigem = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigem, 18.0f));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigem, 18.0f));

        //adiciona marcador da posição atual do usuário
        posicaoatual = mMap.addMarker(new MarkerOptions()
                .position(mOrigem)
                .draggable(true)
                .title("Você está aqui")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_add))
        );
        posicaoatual.showInfoWindow();
    }

    //adiciona marcadores de vagas existentes
    private void addMarkersToMap() {
        for (int i = 0; i < listaVagaUsuarios.size(); i++) {
            //adiciona marcadores para vagas disponiveis
            if (listaVagaUsuarios.get(i).getStatus() == 0) {
                listadeVagasDisponiveis = new LatLng(listaVagaUsuarios.get(i).getLatitude(), listaVagaUsuarios.get(i).getLongitude());
/*
 * Os If's abaixo representam a identificação dos marcadores no mapa que simbolizarão tipos de vagas.
 */
                BitmapDescriptor bitmapDescriptor = null;

                if (listaVagaUsuarios.get(i).getTipovaga().getId() == 1) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_2);
                } else if (listaVagaUsuarios.get(i).getTipovaga().getId() == 2) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_5);
                } else if (listaVagaUsuarios.get(i).getTipovaga().getId() == 3) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_3);
                } else if (listaVagaUsuarios.get(i).getTipovaga().getId() == 4) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_4);
                } else if (listaVagaUsuarios.get(i).getTipovaga().getId() == 5) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_1);
                } else if (listaVagaUsuarios.get(i).getTipovaga().getId() == 6) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_6);
                } else if (listaVagaUsuarios.get(i).getTipovaga().getId() == 7) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_7);
                } else if (listaVagaUsuarios.get(i).getTipovaga().getId() == 8) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.parking);
                }

                //adiciona marcador
                mMap.addMarker(new MarkerOptions()
                        .position(listadeVagasDisponiveis)
                        .title(Constantes.EXTRA_VAGA + listaVagaUsuarios.get(i).getId() + " - " + listaVagaUsuarios.get(i).getTipovaga().getDescricao())
                        .icon(bitmapDescriptor));

            }

        }
    }



    //adiciona os marcadores conforme tipo de vaga selecionado na tela de lista de tipo de vagas
    private void addMarkersEspecificosToMap() {

        for (int i = 0; i < listaVagaUsuarios.size(); i++) {
            //if recebe tipo de vaga enviado como parametro
            if (listaVagaUsuarios.get(i).getStatus() == 0 && listaVagaUsuarios.get(i).getTipovaga().getId() == tipoVaga.getId()) {
                listadeVagasDisponiveis = new LatLng(listaVagaUsuarios.get(i).getLatitude(), listaVagaUsuarios.get(i).getLongitude());

                BitmapDescriptor bitmapDescriptor = null;

                if (tipoVaga.getId() == 1) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_2);
                } else if (tipoVaga.getId() == 2) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_5);
                } else if (tipoVaga.getId() == 3) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_3);
                } else if (tipoVaga.getId() == 4) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_4);
                } else if (tipoVaga.getId() == 5) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_1);
                } else if (tipoVaga.getId() == 6) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_6);
                } else if (tipoVaga.getId() == 7) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_7);
                } else if (tipoVaga.getId() == 8) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.parking);
                }

                mMap.addMarker(new MarkerOptions()
                        .position(listadeVagasDisponiveis)
                        .title(Constantes.EXTRA_VAGA + listaVagaUsuarios.get(i).getId() + " - " + listaVagaUsuarios.get(i).getTipovaga().getDescricao())
                        .icon(bitmapDescriptor));
            }

        }

    }

    private void adicionaMarkerHistorico() {
        mOrigem = new LatLng(mVagaHistorico.getLatitude(), mVagaHistorico.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigem, 19.0f));
        //adiciona marcador histórico

        BitmapDescriptor bitmapDescriptor = null;

        if (mVagaHistorico.getTipovaga().getId() == 1) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_2);
        } else if (mVagaHistorico.getTipovaga().getId() == 2) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_5);
        } else if (mVagaHistorico.getTipovaga().getId() == 3) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_3);
        } else if (mVagaHistorico.getTipovaga().getId() == 4) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_4);
        } else if (mVagaHistorico.getTipovaga().getId() == 5) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_1);
        } else if (mVagaHistorico.getTipovaga().getId() == 6) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_6);
        } else if (mVagaHistorico.getTipovaga().getId() == 7) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.carro_7);
        } else if (mVagaHistorico.getTipovaga().getId() == 8) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.parking);
        }


        posicaoatual = mMap.addMarker(new MarkerOptions()
                .position(mOrigem)
                .draggable(true)
                .icon(bitmapDescriptor)
        );
        posicaoatual.showInfoWindow();

    }


    @Override
    public boolean onMarkerClick(Marker marker) {


        if (mVagaHistorico == null) {
            boolean conexao = false;

            ConexaoInternet consultaConexaoInternet = new ConexaoInternet(this);

            try {
                conexao = consultaConexaoInternet.verificaConexao();
            } catch (ConnectException e) {
                e.printStackTrace();
            }

            if (conexao) {
                //identifica se marcador clicado foi o marcado da posição atual
                if (marker.getPosition().latitude == posicaoatual.getPosition().latitude) {
                    mVaga = new Vaga(0, marker.getPosition().latitude, marker.getPosition().longitude, 0, null, usuario, null, null, null, null, null, null);
                    getNovaVagaFragment();

                } else {
                    vagaExistente = new Vaga(0, marker.getPosition().latitude, marker.getPosition().longitude, 0, null, usuario, null, null, null, null, null, null);
                    getVagaExistenteFragment();
                }

            } else {
                Toast.makeText(this, "Verifique sua conexão de internet!", Toast.LENGTH_SHORT).show();
            }
        }

        return false;
    }

    public void getVagaExistenteFragment() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        lManager.removeUpdates(lListener);

        Bundle parametros = new Bundle();
        parametros.putSerializable(Constantes.EXTRA_VAGA, vagaExistente);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment vagaExistenteFragment = new VagaExistenteFragment();

        vagaExistenteFragment.setArguments(parametros);

        if (vagaExistenteFragment != null){
            this.getSupportFragmentManager().popBackStack();
        }

        transaction.replace(R.id.map, vagaExistenteFragment, "tag");
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void getNovaVagaFragment() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        Bundle parametros = new Bundle();
        parametros.putSerializable(Constantes.EXTRA_VAGA, mVaga);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment novaVagaFragment = new NovaVagaFragment();
        novaVagaFragment.setArguments(parametros);


        if (novaVagaFragment != null){
            this.getSupportFragmentManager().popBackStack();
        }


        transaction.replace(R.id.map, novaVagaFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        posicaoatual.remove();

        mOrigem = latLng;

//adiciona marcador da posição atual do usuário
        posicaoatual = mMap.addMarker(new MarkerOptions()
                .position(mOrigem)
                .draggable(true)
                .title("Você clicou aqui")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_add)));

        posicaoatual.showInfoWindow();


    }


    public class ListaDeVagasTask extends AsyncTask<Void, Void, ArrayList<Vaga>> {

        ListaDeVagasTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<Vaga> doInBackground(Void... params) {
            //buscar lista de vagas
            RequisicaoHttp requisicaoHttp = new RequisicaoHttp();
            try {
                listaVagaUsuarios = requisicaoHttp.consultaVagasWebService();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return listaVagaUsuarios;
        }


        @Override
        protected void onPostExecute(ArrayList<Vaga> listVagas) {
            super.onPostExecute(listVagas);

            if (tipoVaga != null) {
                addMarkersEspecificosToMap();
            } else {
                addMarkersToMap();
            }

//            showProgress(false);

        }


    }


}
