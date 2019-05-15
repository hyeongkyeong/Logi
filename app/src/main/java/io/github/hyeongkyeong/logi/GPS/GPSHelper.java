package io.github.hyeongkyeong.logi.GPS;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GPSHelper {

    private static final String TAG = "GPSHelper";

    public double longitude; //경도
    public double latitude;   //위도
    public double altitude;   //고도

    public GPSHelper(Context context){
        // 위치 관리자 객체 참조
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // 리스너 객체 생성
        LocationListener gpsListener = new LocationListener(){
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude(); //경도
                latitude = location.getLatitude();   //위도
                altitude = location.getAltitude();   //고도
                Log.d(TAG,"[GPS]"+ "Latitude: "+ latitude + ", Longitude:"+ longitude + ", Altitude:"+ altitude);
            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        try {
            // GPS 기반 위치 요청
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,gpsListener);
            // 네트워크 기반 위치 요청
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,gpsListener);
        } catch(SecurityException ex) {
            ex.printStackTrace();
        }


    }


}


