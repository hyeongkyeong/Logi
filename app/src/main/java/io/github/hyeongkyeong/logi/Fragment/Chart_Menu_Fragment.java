package io.github.hyeongkyeong.logi.Fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;

import java.util.ArrayList;
import java.util.List;

import io.github.hyeongkyeong.logi.Data.SerialLogData;
import io.github.hyeongkyeong.logi.R;

import static java.lang.Thread.sleep;

public class Chart_Menu_Fragment extends Fragment {


    private static final String TAG = "Chart_Menu_Fragment";
    View view;

    private AnyChartView anyChartView;
    private Cartesian cartesian;

    public Chart_Menu_Fragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.chart_fragment_view, container, false);

        anyChartView = (AnyChartView)view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));


        cartesian = AnyChart.line();

        cartesian.animation(false);
        //상, 우, 하, 좌
        cartesian.padding(10d, 10d, 5d, 0d);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);
        //cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.title("Line Chart View");
        cartesian.yAxis(0).title("cm");
        //x좌표 padding 상, 우, 하, 좌
        cartesian.xAxis(0).labels().padding(0d, 0d, 20d, 0d);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Log.d(TAG, "Log_Menu_Fragment Thread Runable run()");
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(getActivity()!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Log.d(TAG, "Log_Menu_Fragment runOnUiThread 타스크");
                                int[] data=SerialLogData.getLastNumData(10);
                                List<DataEntry> data1 = new ArrayList<>();
                                int x_key=0;
                                for(int value : data){
                                    data1.add(new ValueDataEntry(x_key, value));
                                    x_key++;
                                }
                                cartesian.data(data1);
                                //cartesian.legend().enabled(false);
                                cartesian.legend().fontSize(13d);
                                cartesian.legend().padding(0d, 0d, 0d, 0d);

                                anyChartView.setChart(cartesian);
                            }
                        });
                    }
                }
            }
        });




        return (view);

    }
}

