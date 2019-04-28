package io.github.hyeongkyeong.logi.Handler;

import android.app.Activity;

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Activity activity;
    public BackPressCloseHandler(Activity context){
        this.activity = context;
    }

    public void onBackPressed(){


        //if(System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
        //}else{
        //    backKeyPressedTime = System.currentTimeMillis();
        //}
    }
}
