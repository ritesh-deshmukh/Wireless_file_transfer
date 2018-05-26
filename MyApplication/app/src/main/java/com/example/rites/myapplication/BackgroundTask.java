package com.example.rites.myapplication;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedInputStream;
import java.io.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class BackgroundTask extends AsyncTask<String,Void,Void> {
    public final static int SOCKET_PORT = 43215;
    public static String FILE_TO_SEND = "/root/sdcard/CV.doc";

    public interface AsyncResponse {
        void processFinish();
    }

    public AsyncResponse delegate = null;
    Context ctx;
    public BackgroundTask(Context ctx,AsyncResponse delegate) {
        this.ctx=ctx;
        this.delegate=delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        String FILE_TO_SEND = params[0];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        ServerSocket servsock = null;
        Socket sock = null;
        BufferedReader br=null;
        byte[] nameBytes;
        try {
            servsock = new ServerSocket(SOCKET_PORT);
            File myFile = new File (Environment.getExternalStorageDirectory(),FILE_TO_SEND);

            while (true) {
                try {
                    sock = servsock.accept();
                    os = sock.getOutputStream();
                    StringBuilder sb=new StringBuilder();
                    String line;
                    br=new BufferedReader(new FileReader(myFile));
                    while ((line=br.readLine())!=null)
                    {
                        sb.append(line);
                    }
                    String temp=sb.toString();
                    String finalString=FILE_TO_SEND+"#"+temp;
                    byte [] mybytearray  = finalString.getBytes("");
                    os.write(mybytearray,0,mybytearray.length);
                    os.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                } finally {
                    if (bis != null) try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (os != null) try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (sock!=null) try {
                        sock.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            if (servsock != null)
                try {
                    servsock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }



        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}