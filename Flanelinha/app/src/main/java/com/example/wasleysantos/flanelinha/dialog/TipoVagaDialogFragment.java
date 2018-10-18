package com.example.wasleysantos.flanelinha.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.wasleysantos.flanelinha.R;

/**
 * Created by WasleySantos on 30/10/2016.
 */
public class TipoVagaDialogFragment extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,null);

            }
        };

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.sobre_titulo)
                .setMessage(R.string.sobre_mensagem)
                .setPositiveButton(android.R.string.ok,listener)
                .create();

        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
