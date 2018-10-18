package com.example.wasleysantos.flanelinha.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by WasleySantos on 14/07/2016.
 */
public class PalavraSecretaText extends android.support.v7.widget.AppCompatEditText {

    private String palavra;


    public PalavraSecretaText(Context context) {
        super(context);
    }

    public PalavraSecretaText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isPasswordValid(String palavra) {
        //TODO: Replace this with your own loic
        return palavra.length() > 6;
    }
}
