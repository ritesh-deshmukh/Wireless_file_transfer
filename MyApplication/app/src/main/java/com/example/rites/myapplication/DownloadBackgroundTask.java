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
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class DownloadBackgroundTask extends AsyncTask<String,Void,String> {
    // Initiating socket details
    public final static int SOCKET_PORT = 8094;
    public static String SERVER;
    public final static int FILE_SIZE = 17000000;

    public interface AsyncResponse {
        void processFinish(String Output);
    }

    public AsyncResponse delegate = null;
    Context ctx;
    public DownloadBackgroundTask(Context ctx,AsyncResponse delegate) {
        this.ctx=ctx;
        this.delegate=delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        //SERVER = new ServerThread("192.168.43.87",8088);
        SERVER=ctx.getString(R.string.ip_address);
        String FILE_TO_RECEIVED = params[0];
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        BufferedReader br=null;
        OutputStream os = null;
        Socket sock = null;
        try {
            sock = new Socket(SERVER, SOCKET_PORT);
            os = sock.getOutputStream();
            BufferedWriter bwr=new BufferedWriter(new PrintWriter(os));
            byte [] mybytearray  = FILE_TO_RECEIVED.getBytes("UTF-8");
            os.write(mybytearray,0,mybytearray.length);
            os.flush();
            sock.close();

        } catch (IOException e) {

        } finally {
            if(sock != null){
                try {
                    sock.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }if(br!=null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        ServerSocket servsock=null;
        try{
        servsock = new ServerSocket(8088);
        File myFile = new File (Environment.getExternalStorageDirectory(),FILE_TO_RECEIVED);
        while (true) {
            try {
                sock = servsock.accept();

                InputStream is = sock.getInputStream();
                StringBuilder sb=new StringBuilder();
                String line;
                br=new BufferedReader(new InputStreamReader(is));
                while ((line=br.readLine())!=null)
                {
                    sb.append(line);
                }
                String temp=sb.toString();
                byte [] mybytearray  = temp.getBytes("UTF-8");
                //System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");*/
                BufferedWriter bwrr=new BufferedWriter(new FileWriter(myFile));
                bwrr.write(temp);
                bwrr.close();
                os.flush();
                servsock.close();
                sock.close();
                break;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
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
                if (servsock != null)
                    try {
                        servsock.close();
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
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}