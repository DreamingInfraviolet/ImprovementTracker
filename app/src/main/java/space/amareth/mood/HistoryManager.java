package space.amareth.mood;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Volodymyr on 22/12/2015.
 */
public class HistoryManager
{
    //Whether the file should be saved with encryption, regardless of its current encryption setting.
    //This is used to allow the encryption saving to be changed without having to flush to file,
    //While allowing correct reading in case the program quits before writing.
    private enum DesiredEncryption { Yes, No, Unset };
    private DesiredEncryption desiredEncryption = DesiredEncryption.Unset;

    Context context = null;
    Crypto crypto = null;
    String password = null;
    Entity entity = null;

    History history = new History();

    public HistoryManager(Context context) throws Exception
    {
        this.context = context;
        crypto = new Crypto(new SharedPrefsBackedKeyChain(context),
                new SystemNativeCryptoLibrary());
        if(!crypto.isAvailable())
            throw new Exception("Crypto is unsupported.");
    }

    /** Changes the encryption mode. This value is used upon next save. */
    public void setEncrypted(boolean encrypt)
    {
        if(isEncrypted()==encrypt)
            return;
        else
            desiredEncryption = encrypt ? DesiredEncryption.Yes : DesiredEncryption.No;
    }

    /** Returns whether the data is encrypted. */
    public boolean isEncrypted()
    {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getBoolean("encryption", false);
    }

    public void setPassword(String md5Password)
    {
        password=md5Password;
        entity = new Entity("history");
    }

    public void load() throws Exception
    {
        try
        {
            FileInputStream fSettings = context.openFileInput("settings");

            InputStream cypherAwareStream = fSettings;
            if(isEncrypted())
                cypherAwareStream = crypto.getCipherInputStream(fSettings, entity);


            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = cypherAwareStream.read()) != -1)
                builder.append((char)ch);

            String str = builder.toString();

            history.fromXml(str);
        }
        catch (java.io.FileNotFoundException e)
        {
            //If the file was not found, then we have not created it yet.
        }
    }

    /** Returns what the application will do on next save: not necessarily the state of the data. */
    public boolean effectiveEncryption()
    {
        if(desiredEncryption==DesiredEncryption.Unset)
            return isEncrypted();
        else
            return desiredEncryption==DesiredEncryption.Yes ? true:false;

    }

    public void save() throws Exception
    {
        String xml = history.toXml();

        FileOutputStream fos = context.openFileOutput("settings", Context.MODE_PRIVATE);

        OutputStream cryptoAwareStream = fos;
        if(effectiveEncryption())
            cryptoAwareStream = crypto.getCipherOutputStream(fos, entity);

        cryptoAwareStream.write(xml.getBytes());

        SharedPreferences sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("encryption", effectiveEncryption());
        editor.commit();
    }
}
