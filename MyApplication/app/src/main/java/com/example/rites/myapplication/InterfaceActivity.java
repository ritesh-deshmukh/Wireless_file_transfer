package com.example.rites.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class InterfaceActivity extends Activity {
    String temp;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface);
        relativeLayout=(RelativeLayout)findViewById(R.id.interface_RelativeLayout);

        Intent intent=getIntent();
        String s[]=(String[])intent.getSerializableExtra("files");
        Button title1[]=new Button[s.length];
        for(int i=0;i<s.length;i++)
        {

            title1[i]=new Button(getBaseContext());
            title1[i].setText(s[i]);
            title1[i].setId(i);
            RelativeLayout.LayoutParams titleparams1=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            titleparams1.setMargins(20,i*100,20,0);
            temp=title1[i].getText().toString();
            relativeLayout.addView(title1[i],titleparams1);
            title1[i].setOnClickListener(handleClick(title1[i]));

        }

    }
    View.OnClickListener handleClick(final Button button){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        DownloadBackgroundTask backgroundTask = new DownloadBackgroundTask(getBaseContext(), new DownloadBackgroundTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                Toast.makeText(getBaseContext(),button.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });
        backgroundTask.execute(button.getText().toString().trim());
            }
        };
    }
}
