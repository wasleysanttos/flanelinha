package util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.example.wasleysantos.flanelinha.model.Vaga;

import java.io.IOException;
import java.util.List;

/**
 * Created by WasleySantos on 04/11/2016.
 * Alterar para thred pois não é preciso uma vez que essa classe não atualiza interface nenhuma
 */

public class RetornaEnderecoTask extends AsyncTask<Vaga, Vaga, Boolean> {
    private Context context;
    private Vaga mVaga;

    public RetornaEnderecoTask(Context context, Vaga vaga) {
        this.context = context;
        this.mVaga = vaga;
    }

    @Override
    protected Boolean doInBackground(Vaga... params) {
        Boolean sucesso = true;


        //Classe que fornece a localização
        Geocoder geocoder = new Geocoder(context);
        List myLocation = null;

        try {
            //Obtendo os dados do endereço
            myLocation = geocoder.getFromLocation(mVaga.getLatitude(), mVaga.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (myLocation != null && myLocation.size() > 0) {
            Address a = (Address) myLocation.get(0);

            mVaga.setEndereco(a.getThoroughfare());
            mVaga.setBairro(a.getSubLocality());
            mVaga.setCidade(a.getLocality());
            mVaga.setEstado(a.getAdminArea());

            sucesso = false;
        }

        return sucesso;
    }


    @Override
    protected void onPostExecute(Boolean sucesso) {
        super.onPostExecute(sucesso);

//            showProgress(sucesso);

    }

}
