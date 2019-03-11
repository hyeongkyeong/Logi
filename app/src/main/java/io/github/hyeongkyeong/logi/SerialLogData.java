package io.github.hyeongkyeong.logi;

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

public class SerialLogData {
    private static final String TAG = "SerialLogData";

    /* 데이터 */
    private ArrayList<Byte> storedData;

    /* 파일 처리 */
    public static final String rootDirString = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Logi";

    public SerialLogData(){
        storedData = new ArrayList<>();
        File rootDir = new File(rootDirString);
        if(rootDir.exists()!=true){
            rootDir.mkdir();
        }
    }

    public boolean add(Byte data){
        return storedData.add(data);
    }

    public int size(){
        return storedData.size();
    }

    public void clear(){
        storedData.clear();
    }

    public boolean isEmply(){
        return storedData.isEmpty();
    }

    public String fileWrite() throws IOException {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        final String recordFileName =  rootDirString + File.separator + "Record_"+ df.format(Calendar.getInstance().getTime())+".txt";
        File fRecordFile = new File(recordFileName);
        fRecordFile.createNewFile();
        FileOutputStream fosRecordFile = new FileOutputStream(fRecordFile);
        OutputStreamWriter oswRecordFile = new OutputStreamWriter(fosRecordFile, "UTF-8");
        BufferedWriter bwRecordFile = new BufferedWriter(oswRecordFile);
        bwRecordFile.write(toString());
        bwRecordFile.close();
        String retval=null;
        if(fRecordFile.exists()) {
            Log.d(TAG, recordFileName + " 파일이 저장되었습니다.");
            retval = recordFileName;
        }
        return retval;
    }


    public Byte[] getByteArray() {
        Byte byteData[] = new Byte[storedData.size()];
        if(storedData.isEmpty()!=true) {
            for (int i = 0; i < storedData.size(); i++) {
                byteData[i] = storedData.get(i);
            }
        }else{
            Log.d(TAG, "getByteArray() 데이터가 없음");
        }
        return byteData;
    }

    public String toStringLastData(int num){
        StringBuilder sb = new StringBuilder();
        if(storedData.isEmpty()!=true) {
            int start_index=0;
            if(num>storedData.size()){
                start_index = 0;
            }else{
                start_index = storedData.size() - num;
            }
            for (int i = start_index; i < storedData.size(); i++) {
                final byte byteData = storedData.get(i);
                sb.append(String.format("%02x ", byteData & 0xff));
            }
        }else{
            Log.d(TAG, "toStringLastData() 데이터가 없음");
        }
        return sb.toString();
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(storedData.isEmpty()!=true) {
            for (int i =  0; i < storedData.size(); i++) {
                final byte byteData = storedData.get(i);
                sb.append(String.format("%02x ", byteData & 0xff));
            }
        }else{
            Log.d(TAG, "toString() 데이터가 없음");
        }
        return sb.toString();

    }


}

