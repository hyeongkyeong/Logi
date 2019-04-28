package io.github.hyeongkyeong.logi.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;
import static java.lang.Thread.sleep;

import io.github.hyeongkyeong.logi.Bluetooth.BluetoothHelper;
import io.github.hyeongkyeong.logi.Fragment.Chart_Menu_Fragment;
import io.github.hyeongkyeong.logi.Fragment.Disp_Menu_Fragment;
import io.github.hyeongkyeong.logi.Fragment.Log_Menu_Fragment;
import io.github.hyeongkyeong.logi.Handler.BackPressCloseHandler;
import io.github.hyeongkyeong.logi.R;
import io.github.hyeongkyeong.logi.Sensor.SensorHelper;
import io.github.hyeongkyeong.logi.Data.SerialLogData;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static final String TAG = "MainActivity";

    /* 상수 */
    private static final int REQUEST_ENABLE_BT=1; //사용자 동의 다이얼로그
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //SerialPortServiceClass_UUID

    /* 장치 관련 */
    private BluetoothAdapter mBluetoothAdapter = null;

    private SensorHelper sensorHelper;

    /* UI 관련 */
    Context mContext;
    Handler mHandler = null;

    private ImageButton bluetoothIcon;
    private TextView statusMessage;

    /* Thread 관련 */
    private MainActivity.ConnectThread connectTask;

    /* 앱 조작 관련 */
    private BackPressCloseHandler backPressCloseHandler;

    private BottomNavigationView navigation;

    private FragmentManager fragmentManager;
    private Disp_Menu_Fragment disp_menu_frag;
    private Chart_Menu_Fragment chart_menu_frag;
    private Log_Menu_Fragment log_menu_frag;

    private int previous_item;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.menu_item1:
                    transaction.replace(R.id.fragment_view, disp_menu_frag).commitAllowingStateLoss();
                    previous_item = item.getItemId();
                    return true;
                case R.id.menu_item2:
                    transaction.replace(R.id.fragment_view, log_menu_frag).commitAllowingStateLoss();
                    previous_item = item.getItemId();
                    return true;
                case R.id.menu_item3:
                    transaction.replace(R.id.fragment_view, chart_menu_frag).commitAllowingStateLoss();
                    previous_item = item.getItemId();
                    return true;

            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        previous_item=R.id.menu_item1;

        mHandler = new Handler();

        init_fragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_view, disp_menu_frag).commitAllowingStateLoss();

        bluetoothIcon = findViewById(R.id.bluetoothIcon);
        statusMessage = findViewById(R.id.status_msg);

        backPressCloseHandler = new BackPressCloseHandler(this);
        sensorHelper = new SensorHelper(this);
    }
    @Override
    public void onBackPressed(){
        connectTask.cancel();
        //finish();
        backPressCloseHandler.onBackPressed();
        Toast.makeText(mContext, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart()");

        Thread bluethothThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Log.d(TAG, "Thread Runable run()");
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Log.d(TAG, "runOnUiThread 타스크");
                            /* 블루투스 정보 갱신 */
                            if(BluetoothHelper.bluetoothPaired && BluetoothHelper.bluetoothSocketEnabled){
                                bluetoothIcon.setColorFilter(getResources().getColor(R.color.colorAccent));
                                if(!SerialLogData.isEmply()){
                                    //contentView.setText(SerialLogData.toStringLastData(200));
                                }
                            }else{
                                bluetoothIcon.setColorFilter(android.R.color.white);
                            }
                            if(BluetoothHelper.bluetoothPaired==true && BluetoothHelper.bluetoothSocketEnabled==true){
                                statusMessage.setText(BluetoothHelper.getConnected_device_name()+" is connected");
                            }else{
                                statusMessage.setText("Device is not connected");
                            }

                        }
                    });
                }
            }
        });

        bluethothThread.start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");

        sensorHelper.register();

    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorHelper.unregister();

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

        BluetoothHelper.bluetoothPaired = false;
        BluetoothHelper.bluetoothSocketEnabled = false;

        //다이얼로그 생성하기
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bluetooth Devices");
        builder.setCancelable(true);

        //페어링된 기기 찾기
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        final BluetoothDevice[] pairedDevicesArray=pairedDevices.toArray(new BluetoothDevice[0]);
        String[] btDeviceList=new String[pairedDevicesArray.length];
        if (pairedDevices.size() > 0) { //페어링된 기기가 있는 경우
            BluetoothHelper.bluetoothPaired = true;
            for (int i=0;i<pairedDevicesArray.length;i++){
                btDeviceList[i] = pairedDevicesArray[i].getName()+"("+pairedDevicesArray[i].getAddress()+")";
            }
            builder.setItems(btDeviceList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int devNum) {
                    Toast.makeText(mContext, "기기를 연결합니다.", Toast.LENGTH_SHORT).show();
                    connectTask = new MainActivity.ConnectThread(pairedDevicesArray[devNum]);
                    connectTask.start();

                }
            });
        }else{ //페어링된 기기가 없는 경우
            BluetoothHelper.bluetoothPaired=false;
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
                BluetoothHelper.bluetoothPaired = false;
                BluetoothHelper.bluetoothSocketEnabled = false;
                Log.d(TAG,"createRfcommSocketToServiceRecord() fail");
                Log.e(TAG,e.toString());
                statusMessage.setText("createRfcommSocketToServiceRecord fail");
            }
            mmSocket = tmp;
            BluetoothHelper.setConnected_device_name(device.getName());
            //statusMessage.setText(device.getName()+" is connected");
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                BluetoothHelper.bluetoothSocketEnabled = true;
                Log.d(TAG,"Bluetooth Socket Connecting..");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                Log.d(TAG,"Fail to connect to socket");
                Log.d(TAG,"mmSocket.connect() Fail");
                Log.e(TAG,connectException.toString());
                BluetoothHelper.bluetoothPaired = false;
                BluetoothHelper.bluetoothSocketEnabled = false;
                statusMessage.setText("Fail to connect to socket");

                try {
                    mmSocket.close();
                    BluetoothHelper.bluetoothSocketEnabled = false;
                } catch (IOException closeException) {
                    Log.d(TAG,"File to close socket");
                    Log.d(TAG,"mmSocket.close() Fail");
                    statusMessage.setText("File to close socket");
                    BluetoothHelper.bluetoothPaired = false;
                    BluetoothHelper.bluetoothSocketEnabled = false;
                    Log.e(TAG,closeException.toString());
                }
                return;
            }
            if(BluetoothHelper.bluetoothSocketEnabled==true) {
                try {
                    mmInStream = mmSocket.getInputStream();
                } catch (IOException e) {
                    Log.d(TAG,"mmSocket.getInputStream() Fail");
                    e.printStackTrace();
                    statusMessage.setText("Fail to Read Data");
                }
            }
            Log.d(TAG,"bluetoothPaired: "+BluetoothHelper.bluetoothPaired);
            Log.d(TAG,"bluetoothSocketEnabled: "+BluetoothHelper.bluetoothSocketEnabled);



            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytesAvailable; // bytes returned from read()
            while (BluetoothHelper.bluetoothSocketEnabled && BluetoothHelper.bluetoothPaired) {
                Log.d(TAG, "Bluetooth Connection Task");
                try {
                    bytesAvailable = mmInStream.read(buffer);
                    if(bytesAvailable>0){
                        for(int i=0;  i<bytesAvailable;i++){
                            SerialLogData.add(buffer[i]);
                        }
                    }
                } catch (IOException e) {
                    BluetoothHelper.bluetoothSocketEnabled=false;
                    BluetoothHelper.bluetoothPaired=false;
                    Log.d(TAG, "mmInStream.read(buffer) Fail");
                    //statusMessage.setText("Fail to Read Data");
                    Log.e(TAG,e.toString());
                }
            }
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
                BluetoothHelper.bluetoothSocketEnabled = false;
            } catch (IOException e) { }
        }
    }

    void init_fragment(){
        fragmentManager = getSupportFragmentManager();
        disp_menu_frag = new Disp_Menu_Fragment();
        chart_menu_frag = new Chart_Menu_Fragment();
        log_menu_frag = new Log_Menu_Fragment();
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

    public void onSaveIconButtonClicked(View v){
        if(SerialLogData.isEmply()){
            Toast.makeText(mContext, "저장할 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        }else {
            try {
                String logfile = SerialLogData.fileWrite();
                Toast.makeText(mContext, logfile + " 파일이 생성되었습니다.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void onClearIconButtonClicked(View v){
        SerialLogData.clear();
        Toast.makeText(mContext, "데이터를 초기화 하였습니다.", Toast.LENGTH_SHORT).show();
    }



    public SensorHelper getSensorHelper() {
        return this.sensorHelper;
    }
}
