package space.amareth.mood;

import android.content.Context;
import android.util.Log;

import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class HistoryManager
{
    //Whether the file should be saved with encryption, regardless of its current encryption setting.
    //This is used to allow the encryption saving to be changed without having to flush to file,
    //While allowing correct reading in case the program quits before writing.
    private enum DesiredEncryption { Yes, No, Unset }

    /*---------------------------------------- Variables ---------------------------------------*/

    private DesiredEncryption desiredEncryption = DesiredEncryption.Unset;

    private Context context = null;          //Null if none
    private Crypto crypto = null;            //Not null
    private Entity entity = null;            //Not null
    private History history = null;          //Not null
    private byte[] password = null;          //Null if none

    /*--------------------------------------- /Variables ---------------------------------------*/

    /*---------------------------------------- Singleton ---------------------------------------*/

    private static HistoryManager inst = null;

    /** Creates a new instance. Throws an exception if one already exists. */
    public static void create(Context context) throws Exception
    {
        if(inst!=null)
            throw new Exception("HistoryManager instance already exists.");
        else
            inst = new HistoryManager(context);
    }

    /** Returns the global manager instance. */
    public static HistoryManager instance()
    {
        return inst;
    }

    /*--------------------------------------- /Singleton ---------------------------------------*/

    /*------------------------------------------ Init ------------------------------------------*/

    private HistoryManager(Context context) throws Exception
    {
        this.context = context;
        entity = new Entity("history");
        history = new History();
        crypto = new Crypto(new SharedPrefsBackedKeyChain(context), new SystemNativeCryptoLibrary());
        if(crypto==null || !crypto.isAvailable())
            throw new Exception("Crypto is unsupported.");
    }

    /*----------------------------------------- /Init ------------------------------------------*/

    /*------------------------------------------ Crypto ----------------------------------------*/

    /** Changes the desired encryption mode. This value is used upon next save. */
    public void setEncrypted(boolean encrypt)
    {
        if(isDataEncrypted()!=encrypt) //No action needed if they're equal.
            desiredEncryption = encrypt ? DesiredEncryption.Yes : DesiredEncryption.No;
    }

    /** Returns whether the data is encrypted. */
    public boolean isDataEncrypted()
    {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getBoolean("encryption", false);
    }

    /** Returns what the application will do on next save: not necessarily the state of the data. */
    public boolean getDesiredEncryption()
    {
        if(desiredEncryption==DesiredEncryption.Unset)
            return isDataEncrypted();
        else
            return desiredEncryption==DesiredEncryption.Yes ? true:false;

    }

    /** Hashes the input and sets it as the password to be used.
     * This method must be called if encryption is to be used before decrypting. */
    public void setPassword(String password)
    {
        this.password = hashPassword(password);
    }

    /** Checks that the given password is valid for loading the data.
     * requires: file exists, no output stream open. */
    public boolean verifyPassword(String password) throws Exception
    {
        byte[] bytes = hashPassword(password);
        InputStream is = getInputStream();
    }

    public boolean verifyPassword(InputStream stream) throws Exception
    {
        byte[] header = new byte[16];
        stream.read(header, 0, 16);
        for(int i = 0; i < 16; ++i)
            if(header[i]!=0)
                return false;
        return true;
    }

    public void createEmptyIfNotExists() throws Exception
    {
        if(!new File("data").exists())
        {
            OutputStream os = getOutputStream();
            os.write(new byte[16]); //Write header
        }
    }

    /** Returns the stream. Not that the read pointer starts before the header, so verifyPassword
     * must be called afterwards.
     */
    private InputStream getInputStream() throws KeyChainException, CryptoInitializationException, IOException
    {
        FileInputStream fSettings = context.openFileInput("data");

        InputStream cypherAwareStream = fSettings;
        if(isDataEncrypted())
            cypherAwareStream = crypto.getCipherInputStream(fSettings, entity);

        return cypherAwareStream;
    }

    /** modifies: data encryption state. */
    public OutputStream getOutputStream() throws Exception
    {
        FileOutputStream fos = context.openFileOutput("data", Context.MODE_PRIVATE);

        OutputStream cryptoAwareStream = fos;
        if(getDesiredEncryption())
            cryptoAwareStream = crypto.getCipherOutputStream(fos, entity);

        context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
                .putBoolean("encryption", getDesiredEncryption()).commit();

        return cryptoAwareStream;
    }

    private static byte[] hashPassword(String password)
    {
        try { return MessageDigest.getInstance("MD5").digest(password.trim().getBytes()); }
        catch(Exception e) { Log.e("ERROR", e.toString()); }
        assert false; return null; //Should never be reached
    }

    /*----------------------------------------- /Crypto ----------------------------------------*/

    /*----------------------------------------- Load/Save --------------------------------------*/

    public void load() throws Exception
    {
        try
        {
            InputStream stream = getInputStream();

            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = stream.read()) != -1)
                builder.append((char)ch);

            String str = builder.toString();
            stream.close();

            history.fromXml(str);
        }
        catch (java.io.FileNotFoundException e)
        {
            //If the file was not found, then we have not created it yet.
        }
    }


    public void save() throws Exception
    {
        OutputStream stream = getOutputStream();
        stream.write(history.toXml().getBytes());
        stream.close();
    }

    /*---------------------------------------- /Load/Save --------------------------------------*/
}
