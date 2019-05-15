package io.github.hyeongkyeong.logi.Fragment;

import android.content.Context;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import io.github.hyeongkyeong.logi.Activities.MainActivity;
import io.github.hyeongkyeong.logi.Bluetooth.BluetoothHelper;
import io.github.hyeongkyeong.logi.Data.SerialLogData;
import io.github.hyeongkyeong.logi.GPS.GPSHelper;
import io.github.hyeongkyeong.logi.R;
import io.github.hyeongkyeong.logi.Sensor.SensorHelper;

import static java.lang.Thread.getAllStackTraces;
import static java.lang.Thread.sleep;

public class Disp_Menu_Fragment extends Fragment {


    private static final String TAG = "Disp_Menu_Fragment";
    View view;

    //BluetoothHelper bluetoothHelper;

    /* 센서 관련 */

    private TextView sens_acc_x;
    private TextView sens_acc_y;
    private TextView sens_acc_z;
    private TextView sens_gro_x;
    private TextView sens_gro_y;
    private TextView sens_gro_z;
    private TextView sens_gps_la;
    private TextView sens_gps_long;
    private TextView sens_gps_al;
    private  ProgressBar progress;
    private TextView dispBT;
    private SensorHelper sensorHelper;
    private GPSHelper gpsHelper;

    public Disp_Menu_Fragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.disp_fragment_view, container, false);

        dispBT = (TextView) view.findViewById(R.id.disp_btdata);
        progress = (ProgressBar) view.findViewById(R.id.progressBar);
        sens_acc_x = (TextView) view.findViewById(R.id.accelorometer_val_x);
        sens_acc_y = (TextView) view.findViewById(R.id.accelorometer_val_y);
        sens_acc_z = (TextView) view.findViewById(R.id.accelorometer_val_z);
        sens_gro_x = (TextView) view.findViewById(R.id.gyroscopr_val_x);
        sens_gro_y = (TextView) view.findViewById(R.id.gyroscopr_val_y);
        sens_gro_z = (TextView) view.findViewById(R.id.gyroscopr_val_z);
        sens_gps_la = (TextView) view.findViewById(R.id.gps_val_latitude);
        sens_gps_long = (TextView) view.findViewById(R.id.gps_val_longitude);
        sens_gps_al = (TextView) view.findViewById(R.id.gps_val_altitude);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Log.d(TAG, "Disp_Menu_Fragment Thread Runable run()");
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(getActivity()!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Log.d(TAG, "Disp_Menu_Fragment runOnUiThread 타스크");
                                dispBT.setText(String.valueOf(SerialLogData.getLastNumData(1)[0]));
                                progress.setProgress(SerialLogData.getLastNumData(1)[0]) ;
                                sens_acc_x.setText(String.format("%5.3f", sensorHelper.accSensor_val_x));
                                sens_acc_y.setText(String.format("%5.3f", sensorHelper.accSensor_val_y));
                                sens_acc_z.setText(String.format("%5.3f", sensorHelper.accSensor_val_z));
                                sens_gro_x.setText(String.format("%5.3f", sensorHelper.gyroSensor_val_x));
                                sens_gro_y.setText(String.format("%5.3f", sensorHelper.gyroSensor_val_y));
                                sens_gro_z.setText(String.format("%5.3f", sensorHelper.gyroSensor_val_z));
                                sens_gps_la.setText(String.format("%6.3f", gpsHelper.latitude));
                                sens_gps_long.setText(String.format("%6.3f", gpsHelper.longitude));
                                sens_gps_al.setText(String.format("%6.3f", gpsHelper.altitude));
                            }
                        });
                    }
                }
            }
        });

        thread.start();
        return (view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity()!=null && getActivity() instanceof MainActivity){
            sensorHelper = ((MainActivity)getActivity()).getSensorHelper();
            gpsHelper = ((MainActivity)getActivity()).getGPSHelper();
        }
    }
}
