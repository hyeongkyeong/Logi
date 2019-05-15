package io.github.hyeongkyeong.logi.Data;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.github.hyeongkyeong.logi.Sensor.SensorHelper;

public class SerialLogData {
    private static final String TAG = "SerialLogData";

    /* 데이터 */
    //private static ArrayList<Byte> storedData = new ArrayList<>();
    private static ArrayList<DataPacket> storedData = new ArrayList<DataPacket>();

    /* 파일 처리 */
    public static final String rootDirString = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Logi";



    public static String fileWrite() throws IOException {
        File rootDir = new File(rootDirString);
        if(rootDir.exists()!=true){
            rootDir.mkdir();
        }
        DateFormat dformat = new SimpleDateFormat("yyyyMMddHHmmss");
        final String recordFileName =  rootDirString + File.separator + "Record_"+ dformat.format(Calendar.getInstance().getTime())+".csv";
        File fRecordFile = new File(recordFileName);
        fRecordFile.createNewFile();
        FileOutputStream fosRecordFile = new FileOutputStream(fRecordFile);
        OutputStreamWriter oswRecordFile = new OutputStreamWriter(fosRecordFile, "UTF-8");
        BufferedWriter bwRecordFile = new BufferedWriter(oswRecordFile);
        bwRecordFile.write("time, bluetooth, acc_x,acc_y, acc_z, gyro_x, gyro_y, gyro_z, gps_longitude, gps_latitude, gps_altitude\n");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        for(int i=0;i<storedData.size();i++){
            final DataPacket data=storedData.get(i);
            bwRecordFile.write(timeFormat.format(data.datetime)+","+data.btData+","
                    +data.accSensor_val_x+","+data.accSensor_val_y+","+data.accSensor_val_z+","
                    +data.gyroSensor_val_x+","+data.gyroSensor_val_y+","+data.gyroSensor_val_z+","
                    +data.gps_longitude+","+data.gps_latitude+","+data.gps_altitude+"\n");
        }
        bwRecordFile.close();
        String retval=null;
        if(fRecordFile.exists()) {
            Log.d(TAG, recordFileName + " 파일이 저장되었습니다.");
            retval = recordFileName;
        }
        return retval;
    }



    public static String toStringLastData(int num){
        StringBuilder sb = new StringBuilder();
        final int last_index = storedData.size();
        if(!storedData.isEmpty()) {
            int start_index = 0;
            if(num > last_index){
                start_index = 0;
            }else{
                start_index = last_index - num;
            }

            for (int i = start_index; i < last_index; i++) {
                final int intData = storedData.get(i).btData;
                sb.append(String.format(intData+" "));
            }
        }else{
            Log.d(TAG, "toStringLastData(): 데이터가 없음");
        }
        return sb.toString();
    }

    public static int[] getLastNumData(int num){
        //StringBuilder sb = new StringBuilder();
        int[] dataArray = new int[num];
        final int last_index=storedData.size();
        if(!storedData.isEmpty()) {
            int start_index = 0;

            if(num > last_index){
                start_index = 0;
            }else{
                start_index = last_index - num;
            }
            int dataArrayIndex=0;
            for (int i = start_index; i < last_index; i++) {
                final int intData = storedData.get(i).btData;
                dataArray[dataArrayIndex]=intData;
                dataArrayIndex++;
            }
        }else{
            Log.d(TAG, "getLastNumData(): 데이터가 없음");
        }
        //return sb.toString();
        return dataArray;
    }

/*
    public static boolean add(Byte data){
        return storedData.add(data);
    }
*/
    public static boolean add(DataPacket data){
        data.datetime = new Date(System.currentTimeMillis());
        return storedData.add(data);
    }

    public static int size()
    {
        return storedData.size();
    }

    public static void clear()
    {
        storedData.clear();
    }

    public static boolean isEmply()
    {
        return storedData.isEmpty();
    }



    public static String getStringData() {
        StringBuilder sb = new StringBuilder();
        if(storedData.isEmpty()!=true) {
            for (int i =  0; i < storedData.size(); i++) {
                final int intData = storedData.get(i).btData;
                sb.append(String.format(intData+" "));
            }
        }else{
            Log.d(TAG, "toString() 데이터가 없음");
        }
        return sb.toString();

    }


}

