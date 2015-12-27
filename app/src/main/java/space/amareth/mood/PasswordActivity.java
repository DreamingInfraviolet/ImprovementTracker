package space.amareth.mood;


import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class PasswordActivity extends Activity
{

    // UI references.
    private EditText mPassword1View;
    private EditText mPassword2View;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        mPassword1View = (EditText) findViewById(R.id.password1);
        mPassword2View = (EditText) findViewById(R.id.password2);

        mPassword2View.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptConfirm();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.password_submit_button).setOnClickListener(
            new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    attemptConfirm();
                }
            });
    }

    private void attemptConfirm()
    {
        mPassword1View.setError(null);
        mPassword2View.setError(null);

        // Store values at the time of the login attempt.
        String password1 = mPassword1View.getText().toString();
        String password2 = mPassword2View.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (password1.length()<4)
        {
            mPassword1View.setError(getString(R.string.error_invalid_password));
            focusView = mPassword1View;
            cancel = true;
        }

        if(!password1.equals(password2))
        {
            mPassword2View.setError("Passwords do not match");
            focusView=mPassword2View;
            cancel=true;
        }

        if (cancel)
            focusView.requestFocus();
        else
        {
            //Success: handle
        }
    }
}

