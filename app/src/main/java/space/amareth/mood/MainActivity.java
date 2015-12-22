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

            if (firstLaunch) {
                //Set first launch to false
                SharedPreferences sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("first_launch", false);
                editor.commit();

                askUserPasswordConfirmation();
                if(userWantsPasswordProtection)
                {

                }
                else
                {

                }

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

        /*
          * else
          * {
          *   if(hm.isEncrypted)
          *   {
          *     hm.setPassword(md5(get password));
          *   }
          * }
          *
          *
         *
         */
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

    boolean userWantsPasswordProtection;
    DialogInterface.OnClickListener userWantsPasswordListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    userWantsPasswordProtection=true;
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    userWantsPasswordProtection=false;
                    break;
            }
        }
    };

    public void askUserEncryption()
    {
        ///Stopped here. Should I create a subroutine to ask for password confirmation+password in a loop?
    }

    DialogInterface.OnClickListener userPasswordListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    userWantsPasswordProtection=true;
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    userWantsPasswordProtection=false;
                    break;
            }
        }
    };
    public String askUserPasswordConfirmation()
    {

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
