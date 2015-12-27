package space.amareth.mood;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HistoryManager
{
    /*---------------------------------------- Variables ---------------------------------------*/

    private Context context = null;          //Null if none
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
        history = new History();
    }

    /*----------------------------------------- /Init ------------------------------------------*/

    /*----------------------------------------- Load/Save --------------------------------------*/

    /** Returns the stream. Not that the read pointer starts before the header, so verifyPassword
     * must be called afterwards.
     */
    private InputStream getInputStream() throws IOException
    {
        return context.openFileInput("data");
    }

    /** modifies: data encryption state. */
    private OutputStream getOutputStream() throws IOException
    {
        return context.openFileOutput("data", Context.MODE_PRIVATE);
    }

    public HistoryEntry getEntry(long dateInMillis)
    {
        return history.getEntry(dateInMillis);
    }

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
            //If the file was not found, then we have not created it yet, so do nothing.
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
