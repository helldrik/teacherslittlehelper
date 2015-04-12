package app.com.jeldrik.teacherslittlehelper.DataTransfer;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.JsonReader;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import app.com.jeldrik.teacherslittlehelper.data.DbContract;

/**
 * Created by jeldrik on 28/03/15.
 /**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static  final String TAG="TeachersLittleHelper_SyncAdapter";
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        long timestamp=extras.getLong("timestamp");
        String useremail=extras.getString("useremail");
        try {
            getData(provider);
        } catch (RemoteException | IOException e) {
            syncResult.hasHardError();
            Log.e(TAG,"sync error "+e);
        }
        try{
            sendUserData(useremail,timestamp);
        }catch(IOException e) {
            Log.e(TAG, "sync error " + e);
        }
    }
    /*
    returns the timestamp related to the user email saved on the server
     */
    private void sendUserData(String useremail, long timestamp) throws IOException{

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://www.jeldrik.com/dataSending/request.php");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("request", "adUser"));
        nameValuePairs.add(new BasicNameValuePair("email", useremail));
        nameValuePairs.add(new BasicNameValuePair("timestamp", Long.toString(timestamp)));

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        // Execute HTTP Post Request
        HttpResponse response = httpclient.execute(httppost);
        String responseBody = EntityUtils.toString(response.getEntity());
        Log.v(TAG,"Server answer: "+responseBody);
    }
    private void getData(ContentProviderClient contentProviderClient) throws RemoteException, IOException{
        URL url=null;
        try{url =new URL("http","jeldrik.com",80," /dataSending");}
        catch(MalformedURLException e){Log.e(TAG,"Error in the url structure "+e);}

        URLConnection conn=url.openConnection();
        BufferedReader bufReader=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
        JsonReader reader=new JsonReader(bufReader);
        try{
            reader.beginObject();
            while(reader.hasNext()) {
                if (reader.nextName().equals("class")){
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        ContentValues vals = new ContentValues(7);
                        while (reader.hasNext()) {
                            switch(reader.nextName()){
                                case "title":
                                    vals.put(DbContract.ClassEntry.COLUMN_TITLE, reader.nextString());
                                    break;
                                case "time":
                                    vals.put(DbContract.ClassEntry.COLUMN_TIME, reader.nextString());
                                    break;
                                case "selectedDays":
                                    vals.put(DbContract.ClassEntry.COLUMN_DATE, reader.nextString());
                                    break;
                                case "endTime":
                                    vals.put(DbContract.ClassEntry.COLUMN_DURATION, reader.nextString());
                                    break;
                                case "location":
                                    vals.put(DbContract.ClassEntry.COLUMN_LOCATION, reader.nextString());
                                    break;
                                case "level":
                                    vals.put(DbContract.ClassEntry.COLUMN_LEVEL, reader.nextString());
                                    break;
                                case "info":
                                    vals.put(DbContract.ClassEntry.COLUMN_EXTRA_INFO, reader.nextString());
                                    break;
                                default:
                                    break;
                            }
                        }
                        Log.v(TAG, "JSON "+ vals.toString());

                        Uri returnUri = contentProviderClient.insert(DbContract.ClassEntry.CONTENT_URI, vals);
                        int id=Integer.parseInt(returnUri.getLastPathSegment());
                        Log.v("MainActivity", "JSON The ID: "+returnUri.getLastPathSegment());
                        reader.endObject();
                    }
                    reader.endArray();
                }
            }
            reader.endObject();
        }catch(IOException e){Log.e(TAG,"Could not read JSON "+e);}
        finally {
            reader.close();
        }

    }
}