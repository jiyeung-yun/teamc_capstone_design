package com.capstone.plantplant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.plantplant.util.PreferenceManager;


public class DriveActivity extends AppCompatActivity {

    Context context;

    EditText etxt_username;
    Button btn_user_regi;

    ProgressBar drive_progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        context = this;

        drive_progressBar = findViewById(R.id.drive_progressBar);
        onViewPrograss(false);
        etxt_username = findViewById(R.id.etxt_username);
        btn_user_regi = findViewById(R.id.btn_user_regi);
        btn_user_regi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewPrograss(true);
                String username = etxt_username.getText().toString();
                if(username.length() < 2){
                    Toast.makeText(getApplicationContext(),"아이디의 길이가 너무 짧습니다.",Toast.LENGTH_SHORT).show();
                    onViewPrograss(false);
                    return;
                }

                PreferenceManager preferenceManager = new PreferenceManager();
                double dValue = Math.random();
                char cLetter = (char)((dValue * 26) + 65);
                int iValue = (int)(dValue * 10000);
                String user_id = Character.toString(cLetter)+ iValue;

                preferenceManager.setString(context,"USER_ID",user_id);
                preferenceManager.setString(context, "USER_NAME",username);
                preferenceManager.setBoolean(context, "FirstDrive",true);


                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    void onViewPrograss(boolean state){
        drive_progressBar.setIndeterminate(state);
        if(state)
            drive_progressBar.setVisibility(View.VISIBLE);
        else
            drive_progressBar.setVisibility(View.GONE);

    }

}
