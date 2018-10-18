package util;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.ConnectException;

/**
 * Created by WasleySantos on 25/10/2016.
 */
public class ConexaoInternet {
    private Context context;

    public ConexaoInternet(Context context) {
        this.context = context;
    }

    public  boolean verificaConexao() throws ConnectException {

        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected();

    }


}
