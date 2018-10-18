package com.example.wasleysantos.flanelinha.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by WasleySantos on 14/07/2016.
 */
public class PasswordText  extends android.support.v7.widget.AppCompatEditText {

    private String password;


    public PasswordText(Context context) {
        super(context);
    }

    public PasswordText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isPasswordValid(String password) {
        //TODO: Replace this with your own loic
        return password.length() > 6;
    }
}
