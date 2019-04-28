package io.github.hyeongkyeong.logi.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.github.hyeongkyeong.logi.R;

public class IntroActivity extends AppCompatActivity {
    private static final String TAG = "IntroActivity";

    boolean StorageReadPermission = false;
    boolean StorageWritePermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate()");

        setContentView(R.layout.activity_intro);

        checkDangerousPermissions();


    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");
        Log.d(TAG,"StorageReadPermission: "+StorageReadPermission);
        Log.d(TAG,"StorageWritePermission: "+StorageWritePermission);
        if(StorageReadPermission || StorageWritePermission) {
            Log.d(TAG,"MainActivity Start");
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart()");
    }


    //권한확인
    private boolean checkDangerousPermissions() {
        Log.d(TAG,"checkDangerousPermissions()");
        boolean result = false;
        int ReadPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int WritePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        StorageReadPermission = (ReadPermissionCheck == PackageManager.PERMISSION_GRANTED);
        StorageWritePermission = (WritePermissionCheck == PackageManager.PERMISSION_GRANTED);
        if ((StorageReadPermission==true)&&(StorageWritePermission==true)) {
            result = true;
        } else {
            result = false;
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
        return result;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult()");
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                    StorageReadPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    StorageWritePermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    onRestart();
                } else {
                    //Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
}
