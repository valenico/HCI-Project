package com.example.huc_project;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class InputValidator extends Activity implements TextWatcher {
    private EditText et;
    private Drawable error_indicator;
    private Resources r;

    InputValidator(EditText editText, Resources r) {
        this.et = editText;
        this.r = r;
        this.error_indicator = r.getDrawable(R.drawable.error);
        int left = 0;
        int top = 0;
        int right = error_indicator.getIntrinsicHeight();
        int bottom = error_indicator.getIntrinsicWidth();
        error_indicator.setBounds(new Rect(left, top, right, bottom));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
                              int count) {
        if (s.length() != 0) {
            switch (et.getId()) {
                case R.id.email: {
                    if (!Patterns.EMAIL_ADDRESS.matcher(et.getText().toString().trim()).matches()) {
                        et.setError("This does not look like a valid e-mail.");
                    }
                }
                break;

                case R.id.password_signup: {
                    if (s.length() < 5 ) {
                        et.setError("Password must be alpha-numeric and at least 5 characters long.");
                    }
                }
                break;
                case R.id.username_signup: {
                    if ( s.toString().matches("[0-9]+" )){
                        et.setError("Username can't be only numeric.");
                    }
                }
                break;
                default:
                    break;

            }
        }
    }
}

class EmptyTextListener implements TextView.OnEditorActionListener {
    private EditText et;
    private Drawable error_indicator;

    public EmptyTextListener(EditText editText, Resources r) {
        this.et = editText;
        error_indicator = r.getDrawable(R.drawable.error);
        int left = 0;
        int top = 0;
        int right = error_indicator.getIntrinsicHeight();
        int bottom = error_indicator.getIntrinsicWidth();
        error_indicator.setBounds(new Rect(left, top, right, bottom));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            // Called when user press Next button on the soft keyboard

            if (et.getText().toString().equals(""))
                et.setError("Oops! empty.");
        }
        return false;
    }
}

