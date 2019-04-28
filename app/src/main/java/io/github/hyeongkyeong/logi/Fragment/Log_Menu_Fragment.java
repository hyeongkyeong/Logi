package io.github.hyeongkyeong.logi.Fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.TextView;

import io.github.hyeongkyeong.logi.Bluetooth.BluetoothHelper;
import io.github.hyeongkyeong.logi.Data.SerialLogData;
import io.github.hyeongkyeong.logi.R;
import io.github.hyeongkyeong.logi.Sensor.SensorHelper;

import static java.lang.Thread.sleep;

public class Log_Menu_Fragment extends Fragment {


    private static final String TAG = "Log_Menu_Fragment";
    View view;

    private TextView contentView;


    public Log_Menu_Fragment() {


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup view = (ViewGroup)  inflater.inflate(R.layout.log_fragment_view, container, false);

        contentView = view.findViewById(R.id.contentTextView);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Log.d(TAG, "Log_Menu_Fragment Thread Runable run()");
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(getActivity()!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Log.d(TAG, "Log_Menu_Fragment runOnUiThread 타스크");
                                contentView.setText(SerialLogData.toStringLastData(500));


                            }
                        });
                    }
                }
            }
        });

        thread.start();



        return (view);

    }
}
