package com.example.rites.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.security.auth.Destroyable;

public class MainActivity extends AppCompatActivity {
    Button button;
    Button connect;
    String displayName = null;
    public final static int SOCKET_PORT = 43215;  // you may change this
    private final static int REQUEST_EXTERNAL_STORAGE=1;
    public static String FILE_TO_SEND = "/root/sdcard/CV.doc";  // you may change this
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button)findViewById(R.id.sendWan);
        connect=(Button)findViewById(R.id.Connect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int PICKFILE_RESULT_CODE = 1;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, PICKFILE_RESULT_CODE);
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
                    }
                }
                Toast.makeText(getBaseContext(),displayName,Toast.LENGTH_SHORT).show();

                UploadBackgroundTask backgroundTask = new UploadBackgroundTask(getBaseContext(), new UploadBackgroundTask.AsyncResponse() {
                    @Override
                    public void processFinish() {

                        Toast.makeText(getBaseContext(),"Reached",Toast.LENGTH_SHORT).show();
                    }
                });
                backgroundTask.execute(displayName);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uri.toString());
                    final String path = myFile.getName();


                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();

                    }


                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}

class ServerThread extends Thread {
    int Port;
    Context ctx;
    String displayName;
    ServerThread(String displayName,Context ctx,int port) {
        this.Port = port;
        this.ctx=ctx;
        this.displayName=displayName;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(Port);

            while (true) {
                socket = serverSocket.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(Environment.getExternalStorageDirectory(), "third.txt");

        byte[] bytes = new byte[(int) file.length()];
        //byte[] nameBytes=null;
        BufferedInputStream bis=null;
        OutputStream os=null;
        try {
            os = socket.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            bis.read(bytes, 0, bytes.length);

            os.write(bytes, 0, bytes.length);
            os.flush();
            socket.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
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
            if (socket!=null) try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
