package com.example.wasleysantos.flanelinha.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;


/**
 * Created by admin on 11/06/2016.
 */
public class EmailText extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    private String email;

    public EmailText(Context context) {
        super(context);
    }

    public EmailText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isEmailValid(String email) {

        String expressaoRegular = "[A-Za-z0-9\\._-]+@[A-Za-z]+\\.[A-Za-z]+";

        return email.matches(expressaoRegular);
    }


}
