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
import android.widget.TextView;


import io.github.hyeongkyeong.logi.Activities.MainActivity;
import io.github.hyeongkyeong.logi.Bluetooth.BluetoothHelper;
import io.github.hyeongkyeong.logi.Data.SerialLogData;
import io.github.hyeongkyeong.logi.R;
import io.github.hyeongkyeong.logi.Sensor.SensorHelper;

import static java.lang.Thread.getAllStackTraces;
import static java.lang.Thread.sleep;

public class Disp_Menu_Fragment extends Fragment {


    private static final String TAG = "Disp_Menu_Fragment";
    View view;

    BluetoothHelper bluetoothHelper;

    /* 센서 관련 */
    private SensorHelper sensorHelper;
    private TextView sens_acc;
    private TextView sens_gro;
    private TextView sens_mag;

    private TextView dispBT;

    public Disp_Menu_Fragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.disp_fragment_view, container, false);
        dispBT = (TextView) view.findViewById(R.id.disp_btdata);
        sens_acc = (TextView) view.findViewById(R.id.accelorometer_val);
        sens_gro = (TextView) view.findViewById(R.id.gyroscopr_val);
        sens_mag = (TextView) view.findViewById(R.id.magnetic_val);




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
                                dispBT.setText(SerialLogData.toStringLastData(1));
                                sens_gro.setText(Double.toString(sensorHelper.gyroSensor_val_x)+"\n"
                                        +Double.toString(sensorHelper.gyroSensor_val_y)+"\n"
                                        +Double.toString(sensorHelper.gyroSensor_val_z)+"\n");
                                sens_acc.setText(Double.toString(sensorHelper.accSensor_val_x)+"\n"
                                        +Double.toString(sensorHelper.accSensor_val_y)+"\n"
                                        +Double.toString(sensorHelper.accSensor_val_z)+"\n");
                                sens_mag.setText(Double.toString(sensorHelper.magSensor_val_x)+"\n"
                                        +Double.toString(sensorHelper.magSensor_val_y)+"\n"
                                        +Double.toString(sensorHelper.magSensor_val_z)+"\n");

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
        }
    }
}
