package io.github.hyeongkyeong.logi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    /* 상수 */
    private static final int REQUEST_ENABLE_BT=1; //사용자 동의 다이얼로그
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //SerialPortServiceClass_UUID

    /* 상태 저장 변수 */
    private boolean bluetoothPaired = false;
    private boolean bluetoothSocketEnabled = false;

    /* 장치 관련 */
    private BluetoothAdapter mBluetoothAdapter = null;

    /* UI 관련 */
    Context mContext;
    Handler mHandler = null;
    private TextView contentView;
    private ImageButton bluetoothIcon;

    /* Log 관련 */
    private SerialLogData logdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG,"onCreate()");
        mContext = getApplicationContext();
        mHandler = new Handler();

        bluetoothIcon = findViewById(R.id.bluetoothIcon);
        contentView = findViewById(R.id.contentTextView);



        logdata = new SerialLogData();



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart()");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Log.d(TAG, "onCreate 타스크");
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Log.d(TAG, "runOnUiThread 타스크");
                            if(bluetoothPaired && bluetoothSocketEnabled){
                                bluetoothIcon.setColorFilter(getResources().getColor(R.color.colorAccent));
                                if(logdata != null){
                                    contentView.setText(logdata.toStringLastData(200));
                                }
                            }else{
                                bluetoothIcon.setColorFilter(android.R.color.white);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBluetoothButtonClicked(View v){
        if(bluetoothActivate()==true){
            AlertDialog bluetoothSelectAlert = bluetoothSelectDialog();
            bluetoothSelectAlert.show();
        }else{
            Toast.makeText(mContext, "블루투스를 활성화 해주셔야 어플을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClearButtonClicked(View v){
        logdata.clear();
    }

    public void onSaveButtonClicked(View v){
        try {
            String logfile = logdata.fileWrite();
            Toast.makeText(mContext, logfile+" 파일이 생성되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean bluetoothActivate(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            //장치가 블루투스를 지원하지 않는 경우.
            Toast.makeText(mContext, "이 기기는 블루투스를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"이 기기는 블루투스를 지원하지 않습니다.");
        }else { //장치가 블루투스를 지원하는 경우
            //블루투스 활성화 되어 있으면
            if (mBluetoothAdapter.isEnabled()) {
                AlertDialog bluetoothDialog = bluetoothSelectDialog();
                bluetoothDialog.show();
                bluetoothDialog.dismiss();
            }else{ //블루투스 활성화 되지 않았다면
                Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //블루투스 활성화
                startActivityForResult(bluetoothIntent, REQUEST_ENABLE_BT);   //사용자 동의 다이얼로그 출력
            }
        }
        return mBluetoothAdapter.isEnabled();
    }

    private AlertDialog bluetoothSelectDialog(){

        bluetoothPaired = false;
        bluetoothSocketEnabled = false;

        //다이얼로그 생성하기
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");
        builder.setCancelable(true);

        //페어링된 기기 찾기
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        final BluetoothDevice[] pairedDevicesArray=pairedDevices.toArray(new BluetoothDevice[0]);
        String[] btDeviceList=new String[pairedDevicesArray.length];
        if (pairedDevices.size() > 0) { //페어링된 기기가 있는 경우
            bluetoothPaired = true;
            for (int i=0;i<pairedDevicesArray.length;i++){
                btDeviceList[i] = pairedDevicesArray[i].getName()+"("+pairedDevicesArray[i].getAddress()+")";
            }
            builder.setItems(btDeviceList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int devNum) {
                    Toast.makeText(mContext, "기기를 연결합니다.", Toast.LENGTH_SHORT).show();
                    ConnectThread connectTask = new ConnectThread(pairedDevicesArray[devNum]);
                    connectTask.start();
                }
            });
        }else{ //페어링된 기기가 없는 경우
            bluetoothPaired=false;
            Toast.makeText(mContext, "페어링된 기기가 없습니다. 블루투스 설정에서 기기를 페어링해 주세요.", Toast.LENGTH_SHORT).show();
        }
        return builder.create();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket, because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                bluetoothPaired = false;
                bluetoothSocketEnabled = false;
                Log.d(TAG,"createRfcommSocketToServiceRecord() 실패");
                Log.e(TAG,e.toString());
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                bluetoothSocketEnabled = true;
                Log.d(TAG,"블루투스 소켓 연결");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                Log.d(TAG,"블루투스 소켓 연결 실패");
                Log.d(TAG,"mmSocket.connect() 실패");
                Log.e(TAG,connectException.toString());
                bluetoothPaired = false;
                bluetoothSocketEnabled = false;

                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.d(TAG,"블루투스 소켓 닫기 실패");
                    Log.d(TAG,"mmSocket.close() 실패");
                    bluetoothPaired = false;
                    bluetoothSocketEnabled = false;
                    Log.e(TAG,closeException.toString());

                }
                return;
            }
            if(bluetoothSocketEnabled==true) {
                try {
                    mmInStream = mmSocket.getInputStream();
                } catch (IOException e) {
                    Log.d(TAG,"mmSocket.getInputStream() 실패");
                    e.printStackTrace();
                }
            }
            Log.d(TAG,"bluetoothPaired: "+bluetoothPaired);
            Log.d(TAG,"bluetoothSocketEnabled: "+bluetoothSocketEnabled);

            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytesAvailable; // bytes returned from read()
            while (bluetoothSocketEnabled && bluetoothPaired) {
                Log.d(TAG, "블루투스 연결 타스크");
                try {
                    bytesAvailable = mmInStream.read(buffer);
                    if(bytesAvailable>0){
                        for(int i=0;  i<bytesAvailable;i++){
                            logdata.add(buffer[i]);
                        }
                    }
                } catch (IOException e) {
                    bluetoothSocketEnabled=false;
                    bluetoothPaired=false;
                    Log.d(TAG, "mmInStream.read(buffer) 실패");
                    Log.e(TAG,e.toString());
                }
            }
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
                bluetoothSocketEnabled = false;
            } catch (IOException e) { }
        }
    }



}
