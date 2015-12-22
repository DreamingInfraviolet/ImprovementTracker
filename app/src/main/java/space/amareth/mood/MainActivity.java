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
    HistoryManager hm;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        CalendarView cv = (CalendarView)findViewById(R.id.calendarView);
        try
        {
            cv.setMinDate(getPackageManager()
                    .getPackageInfo("space.amareth.mood", 0)
                    .firstInstallTime);
            cv.setMaxDate(System.currentTimeMillis());
        }
        catch(Exception e)
        {
            Log.e("Error", e.toString());
        }

        try
        {
            hm = new HistoryManager(this);
        }
        catch(Exception e)
        {
            Log.e("History Manager", e.toString());
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
                //Set first launch to false
                SharedPreferences sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("first_launch", false);
                editor.commit();

                resolveFirstTimePasswordProtection();

//            hm.setEncrypted(should encrypt?);
//            hm.setPassword(md5(get password with confirmation));
            } else {
                if (hm.isEncrypted()) {
                    String password = "";

                    hm.setPassword(new String(
                            MessageDigest.getInstance("MD5").digest(password.trim().getBytes()),
                            StandardCharsets.UTF_8));

                }
            }
        }
        catch(Exception e)
        {
            Log.e("ERROR", e.toString());
        }
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
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(mainActivity, PasswordActivity.class);
                        startActivity(intent);
                    }})
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
            {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
