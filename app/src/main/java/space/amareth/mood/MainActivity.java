package space.amareth.mood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CalendarView;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import android.widget.EditText;
import android.text.InputType;
import android.content.DialogInterface;

public class MainActivity extends AppCompatActivity
{
    public static MainActivity mainActivity;

    public MainActivity()
    {
        mainActivity=this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menubar, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            HistoryManager.create(this);

            CalendarView cv = (CalendarView)findViewById(R.id.calendarView);
                cv.setMinDate(getPackageManager()
                        .getPackageInfo("space.amareth.mood", 0)
                        .firstInstallTime);
                cv.setMaxDate(System.currentTimeMillis());
        }
        catch(Exception e)
        {
            Log.e("Error", e.toString());
        }
    }

    @Override
    public void onStart()
    {
        try {
            super.onStart();

            //Check if first launch
            boolean firstLaunch = getSharedPreferences("settings", Context.MODE_PRIVATE)
                    .getBoolean("first_launch", true);

            if(true)//if (firstLaunch)
            {
                getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("first_launch", false).commit();
                resolveFirstTimePasswordProtection();
            }
            else if (HistoryManager.instance().isEncrypted())
                askPassword();
        }
        catch(Exception e)
        {
            Log.e("ERROR", e.toString());
        }
    }

    public void alertUser(String message)
    {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage(message)
                .setPositiveButton("Okay", null)
                .show();
    }

    public void askPassword()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder
            .setCancelable(false)
            .setPositiveButton("Proceed",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            String password = input.getText().toString();

                            if (HistoryManager.instance().verifyPassword(password))
                            {
                                //Handle correct password
                            }
                            else
                            {
                                new AlertDialog.Builder(mainActivity)
                                        .setTitle("Error")
                                        .setMessage("The password that you have entered is incorrect.")
                                    .setPositiveButton("Cancel", null)
                                    .show();

                            }
                        }
                    })
                .show();

//        hm.setPassword(new String(
//                MessageDigest.getInstance("MD5").digest(password.trim().getBytes()),
//                java.nio.charset.Charset.forName("UTF-8")));
    }

    public static String hashPassword(String password)
    {
        return password;
    }

    public void resolveFirstTimePasswordProtection()
    {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Password")
                .setMessage("Do you want to enable password protection?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(mainActivity, PasswordActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HistoryManager.instance().setEncrypted(false);
                            }
                        }
                ).show();
    }

                    @Override
                    public boolean onOptionsItemSelected(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_settings: {
                                Intent intent = new Intent(this, SettingsActivity.class);
                                startActivity(intent);
                            }
                            return true;
                        }

                        return super.onOptionsItemSelected(item);
                    }
                }
