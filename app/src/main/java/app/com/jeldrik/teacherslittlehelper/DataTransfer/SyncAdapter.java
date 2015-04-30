package app.com.jeldrik.teacherslittlehelper.DataTransfer;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import app.com.jeldrik.teacherslittlehelper.MyAdapter;
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
    //--------------------------------------------------------------------------------------------------
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        long timestamp=extras.getLong("timestamp");
        String useremail=extras.getString("useremail");
      /*  try {
            getData(provider);
        } catch (RemoteException | IOException e) {
            syncResult.hasHardError();
            Log.e(TAG,"sync error "+e);
        }*/
        try{
            sendUserInfo(provider, useremail, timestamp);
        }catch(IOException e) {
            Log.e(TAG, "sync error " + e);
        }
    }
    //--------------------------------------------------------------------------------------------------
    /*
    returns the timestamp related to the user email saved on the server
     */
    private void sendUserInfo(ContentProviderClient provider,String useremail, long timestamp) throws IOException{

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://www.jeldrik.com/dataSending/request.php");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("request", "checkUser"));
        nameValuePairs.add(new BasicNameValuePair("email", useremail));
        nameValuePairs.add(new BasicNameValuePair("timestamp", Long.toString(timestamp)));

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        // Execute HTTP Post Request
        HttpResponse response = httpclient.execute(httppost);
        String responseBody = EntityUtils.toString(response.getEntity());

        try {
            JSONArray jarr = new JSONArray(responseBody);
            JSONObject json = jarr.getJSONObject(0);
            String msg = json.getString("message");
            long oldTimeStamp=0;
            //User is new so no need to download data from server
            if(msg.equals("User set!")) {

            }
            //user exists and we get the timestamp from last server interaction
            else if(msg.equals("oldTimestamp")) {
                json = jarr.getJSONObject(1);
                oldTimeStamp = json.getLong("timestamp");

                if(oldTimeStamp<timestamp) {

                    try {
                        sendUserData(provider,useremail,timestamp);
                    } catch (RemoteException e) {
                        Log.e(TAG, "Error in sendUserInfo: " + e);
                    }

                }
                else if(oldTimeStamp>timestamp) {
                    //TODO: get data to server if local timestamp is older than server timestamp
                }

                try{
                    getUserData(provider, useremail);
                } catch (RemoteException e) {
                    Log.e(TAG, "Error in sendUserInfo: " + e);
                }

            }

           // Log.v(TAG, "Server answer: " + msg+" "+oldTimeStamp);
        }catch(JSONException e){Log.e(TAG, "Error parsing JSON object in sendUserInfo "+e);}
    }
    //--------------------------------------------------------------------------------------------------
    private void sendUserData(ContentProviderClient provider, String useremail, long timestamp) throws RemoteException,IOException{
        Cursor cursor= provider.query(DbContract.ClassEntry.CONTENT_URI,null, null, null, null);
        cursor.moveToFirst();
        String jsonClass="[";
        while(!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String location = cursor.getString(2);
            String endTime = cursor.getString(3);
            String info = cursor.getString(4);
            String level = cursor.getString(5);
            String startTime = cursor.getString(6);
            String date = cursor.getString(7);
            //date is already json formated we just have to delete the starting "{" to put it into our new jsonString
            date=date.substring(1);
            Log.v(TAG,"tuyen : "+date);


            jsonClass += "{\"" + DbContract.ClassEntry.TABLE_NAME + "\":{\"" + DbContract.ClassEntry._ID + "\":\"" + id + "\","
                    + "\"" + DbContract.ClassEntry.COLUMN_TITLE + "\":\"" + title + "\","
                    + "\"" + DbContract.ClassEntry.COLUMN_LOCATION + "\":\"" + location + "\","
                    + "\"" + DbContract.ClassEntry.COLUMN_EXTRA_INFO + "\":\"" + info + "\","
                    + "\"" + DbContract.ClassEntry.COLUMN_LEVEL + "\":\"" + level + "\","
                    + "\"" + DbContract.ClassEntry.COLUMN_TIME + "\":\"" + startTime + "\","
                    + "\"" + DbContract.ClassEntry.COLUMN_DURATION + "\":\"" + endTime + "\","
                    + date + "}";
            //Log.v(TAG, "JSONdata: "+json);
            if (!cursor.isLast())
                jsonClass += ",";
            cursor.moveToNext();
        }
        jsonClass+="]";
        jsonClass=jsonClass.replace("\n","\\n");
        Log.v(TAG,"tttjsonClass: "+jsonClass);

        cursor= provider.query(DbContract.StudentEntry.CONTENT_URI,null, null, null, null);
        cursor.moveToFirst();
        String jsonStudent="[";
        while(!cursor.isAfterLast()) {
            String name = cursor.getString(0);
            String phone = cursor.getString(1);
            String email = cursor.getString(2);
            String foreignKey = cursor.getString(3);
            int id = cursor.getInt(4);

            jsonStudent += "{\"" + DbContract.StudentEntry.TABLE_NAME + "\":{\"" + DbContract.StudentEntry._ID + "\":\"" + id + "\","
                    + "\"" + DbContract.StudentEntry.COLUMN_STUDENT_NAME + "\":\"" + name + "\","
                    + "\"" + DbContract.StudentEntry.COLUMN_PHONE + "\":\"" + phone + "\","
                    + "\"" + DbContract.StudentEntry.COLUMN_EMAIL + "\":\"" + email + "\","
                    + "\"" + DbContract.StudentEntry.COLUMN_FOREIGN_KEY_CLASS + "\":\"" + foreignKey + "\"}}";
           // Log.v(TAG, "JSONdata: "+jsonStudent);
            if (!cursor.isLast())
                jsonStudent += ",";
            cursor.moveToNext();
        }
        jsonStudent+="]";
        jsonStudent=jsonStudent.replace("\n","\\n");
        Log.v(TAG,"tttjsonStudent: "+jsonStudent);

        cursor= provider.query(DbContract.ClassContentEntry.CONTENT_URI,null, null, null, null);
        cursor.moveToFirst();
        String jsonClassContent="[";
        while(!cursor.isAfterLast()) {
            String book = cursor.getString(0);
            String date = cursor.getString(1);
            String tableTimestamp = cursor.getString(2);
            String page = cursor.getString(3);
            String info = cursor.getString(4);
            int id = cursor.getInt(5);
            int classId=cursor.getInt(6);

            jsonClassContent += "{\"" + DbContract.ClassContentEntry.TABLE_NAME + "\":{\"" + DbContract.ClassContentEntry._ID + "\":\"" + id + "\","
                    + "\"" + DbContract.ClassContentEntry.COLUMN_BOOK + "\":\"" + book + "\","
                    + "\"" + DbContract.ClassContentEntry.COLUMN_DATE + "\":\"" + date + "\","
                    + "\"" + DbContract.ClassContentEntry.COLUMN_TIMESTAMP + "\":\"" + tableTimestamp + "\","
                    + "\"" + DbContract.ClassContentEntry.COLUMN_INFO + "\":\"" + info + "\","
                    + "\"" + DbContract.ClassContentEntry.COLUMN_FOREIGN_KEY_CLASS + "\":\"" + classId + "\","
                    + "\"" + DbContract.ClassContentEntry.COLUMN_PAGE + "\":\"" + page + "\"}}";
            // Log.v(TAG, "JSONdata: "+jsonStudent);
            if (!cursor.isLast())
                jsonClassContent += ",";
            cursor.moveToNext();
        }
        jsonClassContent+="]";
        jsonClassContent=jsonClassContent.replace("\n","\\n");
        Log.v(TAG,"tttjsonClassContent: "+jsonClassContent);

        cursor= provider.query(DbContract.StudentAttendanceEntry.CONTENT_URI,null, null, null, null);
        cursor.moveToFirst();
        String jsonStudentAttendance="[";
        while(!cursor.isAfterLast()) {
            String foreignKeyClassContent = cursor.getString(0);
            String status = cursor.getString(1);
            String foreignKeyStudent = cursor.getString(2);
            int id = cursor.getInt(3);

            jsonStudentAttendance += "{\"" + DbContract.StudentAttendanceEntry.TABLE_NAME + "\":{\"" +  DbContract.StudentAttendanceEntry._ID + "\":\"" + id + "\","
                    + "\"" +  DbContract.StudentAttendanceEntry.COLUMN_FOREIGN_KEY_CLASSCONTENT + "\":\"" + foreignKeyClassContent + "\","
                    + "\"" + DbContract.StudentAttendanceEntry.COLUMN_STATUS + "\":\"" + status + "\","
                    + "\"" + DbContract.StudentAttendanceEntry.COLUMN_FOREIGN_KEY_STUDENT + "\":\"" + foreignKeyStudent + "\"}}";
            // Log.v(TAG, "JSONdata: "+jsonStudent);
            if (!cursor.isLast())
                jsonStudentAttendance += ",";
            cursor.moveToNext();
        }
        jsonStudentAttendance+="]";
        Log.v(TAG,"tttjsonStudentAttendance: "+jsonStudentAttendance);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://www.jeldrik.com/dataSending/request.php");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("request", "updateData"));
        nameValuePairs.add(new BasicNameValuePair("jsonClass", jsonClass));
        nameValuePairs.add(new BasicNameValuePair("jsonStudent", jsonStudent));
        nameValuePairs.add(new BasicNameValuePair("jsonClassContent", jsonClassContent));
        nameValuePairs.add(new BasicNameValuePair("jsonStudentAttendance", jsonStudentAttendance));
        nameValuePairs.add(new BasicNameValuePair("timestamp", Long.toString(timestamp)));
        nameValuePairs.add(new BasicNameValuePair("email", useremail));

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"utf-8"));

        // Execute HTTP Post Request
        HttpResponse response = httpclient.execute(httppost);
        String responseBody = EntityUtils.toString(response.getEntity());
        Log.v(TAG,"Server answer: "+responseBody);

    }
    //--------------------------------------------------------------------------------------------------
    private void getUserData(ContentProviderClient provider, String useremail) throws RemoteException,IOException{

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://www.jeldrik.com/dataSending/request.php");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("request", "getData"));
        nameValuePairs.add(new BasicNameValuePair("email", useremail));

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"utf-8"));

        // Execute HTTP Post Request
        HttpResponse response = httpclient.execute(httppost);
        String responseBody = EntityUtils.toString(response.getEntity());
        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray classArr = json.optJSONArray("class");
            if(classArr!=null){
                for (int i=0;i<classArr.length();i++) {
                    JSONObject classObj = classArr.getJSONObject(i);
                    /*
                    Log.v(TAG,"ttt "+classObj.getString("_id"));
                    Log.v(TAG,"ttt "+classObj.getString("title"));
                    Log.v(TAG,"ttt "+classObj.getString("location"));
                    Log.v(TAG,"ttt "+classObj.getString("date"));
                    Log.v(TAG,"ttt "+classObj.getString("startTime"));
                    Log.v(TAG,"ttt "+classObj.getString("endTime"));
                    Log.v(TAG,"ttt "+classObj.getString("level"));
                    Log.v(TAG,"ttt "+classObj.getString("info"));
                    */

                    String dateAsJson="{\"selectedDays\":"+classObj.getString("date")+"}";

                    ContentValues vals = new ContentValues();

                    vals.put(DbContract.ClassEntry.COLUMN_TITLE, classObj.getString("title"));
                    vals.put(DbContract.ClassEntry.COLUMN_TIME, classObj.getString("startTime"));
                    vals.put(DbContract.ClassEntry.COLUMN_DATE, dateAsJson);
                    vals.put(DbContract.ClassEntry.COLUMN_DURATION, classObj.getString("endTime"));
                    vals.put(DbContract.ClassEntry.COLUMN_LOCATION, classObj.getString("location"));
                    vals.put(DbContract.ClassEntry.COLUMN_LEVEL, classObj.getString("level"));
                    vals.put(DbContract.ClassEntry.COLUMN_EXTRA_INFO, classObj.getString("info"));

                    Uri uri= DbContract.ClassEntry.CONTENT_URI.buildUpon().appendPath(classObj.getString("_id")).build();

                    if(provider.update(uri,vals,null,null)==0) {
                        provider.insert(DbContract.ClassEntry.CONTENT_URI, vals);
                        //Log.v(TAG,"ttt "+classObj.getString("title")+" inserted");
                    }
                    else
                        Log.v(TAG,"getUserData() "+classObj.getString("title")+" updated");
                }
            }else
                Log.e(TAG, "Json that got ret returned from Server is not valid");

            JSONArray classContentArr = json.optJSONArray("classContent");
            if(classContentArr!=null){
                for (int i=0;i<classContentArr.length();i++) {
                    JSONObject classContentObj = classContentArr.getJSONObject(i);

                    ContentValues vals = new ContentValues();

                    vals.put(DbContract.ClassContentEntry.COLUMN_DATE, classContentObj.getString("date"));
                    vals.put(DbContract.ClassContentEntry.COLUMN_TIMESTAMP, classContentObj.getString("timestamp"));
                    vals.put(DbContract.ClassContentEntry.COLUMN_BOOK, classContentObj.getString("book"));
                    vals.put(DbContract.ClassContentEntry.COLUMN_PAGE, classContentObj.getString("page"));
                    vals.put(DbContract.ClassContentEntry.COLUMN_INFO, classContentObj.getString("info"));
                    vals.put(DbContract.ClassContentEntry.COLUMN_FOREIGN_KEY_CLASS, classContentObj.getString("classID"));

                    Uri uri = DbContract.ClassContentEntry.CONTENT_URI.buildUpon().appendPath(classContentObj.getString("_id")).build();

                    if (provider.update(uri, vals, null, null) == 0) {
                        provider.insert(DbContract.ClassContentEntry.CONTENT_URI, vals);
                        Log.v(TAG, "ttt " + classContentObj.getString("_id") + " inserted");
                    } else
                        Log.v(TAG, "ttt " + classContentObj.getString("_id") + " updated");
                }

            }else
                Log.e(TAG,"Json that got ret returned from Server is not valid");

            JSONArray studentArr = json.optJSONArray("student");
            if(studentArr!=null){
                for (int i=0;i<studentArr.length();i++) {
                    JSONObject studentObj = studentArr.getJSONObject(i);

                    ContentValues vals = new ContentValues();

                    vals.put(DbContract.StudentEntry.COLUMN_STUDENT_NAME, studentObj.getString("studentName"));
                    vals.put(DbContract.StudentEntry.COLUMN_EMAIL, studentObj.getString("email"));
                    vals.put(DbContract.StudentEntry.COLUMN_PHONE, studentObj.getString("phone"));
                    vals.put(DbContract.StudentEntry.COLUMN_FOREIGN_KEY_CLASS, studentObj.getString("classID"));


                    Uri uri = DbContract.StudentEntry.CONTENT_URI.buildUpon().appendPath(studentObj.getString("_id")).build();

                    if (provider.update(uri, vals, null, null) == 0) {
                        provider.insert(DbContract.StudentEntry.CONTENT_URI, vals);
                        Log.v(TAG, "ttt " + studentObj.getString("_id") + " inserted");
                    } else
                        Log.v(TAG, "ttt " + studentObj.getString("_id") + " updated");
                }

            }else
                Log.e(TAG,"Json that got ret returned from Server is not valid");

            JSONArray studentAttendanceArr = json.optJSONArray("studentAttendance");
            if(studentAttendanceArr!=null){
                for (int i=0;i<studentAttendanceArr.length();i++) {
                    JSONObject studentAttendanceObj = studentAttendanceArr.getJSONObject(i);

                    ContentValues vals = new ContentValues();

                    vals.put(DbContract.StudentAttendanceEntry.COLUMN_STATUS, studentAttendanceObj.getString("status"));
                    vals.put(DbContract.StudentAttendanceEntry.COLUMN_FOREIGN_KEY_STUDENT, studentAttendanceObj.getString("studentId"));
                    vals.put(DbContract.StudentAttendanceEntry.COLUMN_FOREIGN_KEY_CLASSCONTENT, studentAttendanceObj.getString("classContentId"));

                    Uri uri = DbContract.StudentAttendanceEntry.CONTENT_URI.buildUpon().appendPath(studentAttendanceObj.getString("_id")).build();

                    if (provider.update(uri, vals, null, null) == 0) {
                        provider.insert(DbContract.StudentAttendanceEntry.CONTENT_URI, vals);
                        Log.v(TAG, "ttt " + studentAttendanceObj.getString("_id") + " inserted");
                    } else
                        Log.v(TAG, "ttt " + studentAttendanceObj.getString("_id") + " updated");
                }

            }else
                Log.e(TAG,"Json that got ret returned from Server is not valid");

        }catch(JSONException e){Log.e(TAG, "Error in getUserData() "+e);}
        Log.v(TAG,"Server answerr: "+responseBody);

    }
    //--------------------------------------------------------------------------------------------------
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