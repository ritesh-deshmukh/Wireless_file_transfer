package com.example.rites.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.util.jar.Manifest;


public class Client extends Activity {
    Button connect;
    Button recieve;
    private final static int REQUEST_EXTERNAL_STORAGE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        connect=(Button)findViewById(R.id.Connect);
        recieve =(Button)findViewById(R.id.Recieve);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientBackgroundTask backgroundTask = new ClientBackgroundTask(getBaseContext(), new ClientBackgroundTask.AsyncResponse() {
                    @Override
                    public void processFinish(String Output) {

                        Toast.makeText(getBaseContext(),Output,Toast.LENGTH_SHORT).show();
                        String s[]=Output.split("#");

                        Intent intent=new Intent(getBaseContext(),InterfaceActivity.class);
                        intent.putExtra("files",s);
                        startActivity(intent);
                    }
                });

                backgroundTask.execute("");
            }

        });

        recieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_EXTERNAL_STORAGE:{
                if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }
                else{

                }
                return ;
            }
        }
    }

    public void getResult(String result)
    {
        Toast.makeText(getBaseContext(),"Reached",Toast.LENGTH_SHORT).show();
    }
}

class ClientThread extends Thread{
    String dstAddress;
    int dstPort;
    Context ctx;

    ClientThread(Context ctx,String address, int port) {
        dstAddress = address;
        dstPort = port;
        ctx=this.ctx;
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedReader br=null;
        try {
            socket = new Socket(dstAddress, dstPort);


            byte[] bytes = new  byte[2048];
            InputStream is = socket.getInputStream();

            StringBuilder sb=new StringBuilder();
            String line;
            br=new BufferedReader(new InputStreamReader(is));
            while ((line=br.readLine())!=null)
            {
                sb.append(line);
            }
            String temp=sb.toString();
            String fileName=temp.substring(0,temp.indexOf("#"));
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            BufferedWriter bwr=new BufferedWriter(new FileWriter(file));
            bwr.write(temp.substring(temp.indexOf("#")+1,temp.length()));
            bwr.close();
            socket.close();

        } catch (IOException e) {

            e.printStackTrace();

            final String eMsg = "Something wrong: " + e.getMessage();

        } finally {
            if(socket != null){
                try {
                    socket.close();
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
    }


}

