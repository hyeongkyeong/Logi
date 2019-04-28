package io.github.hyeongkyeong.logi.Bluetooth;

public class BluetoothHelper {
    /* 상태 저장 변수 */
    public static boolean bluetoothPaired = false;
    public static boolean bluetoothSocketEnabled = false;
    private static String connected_device_name="";


    public static String getConnected_device_name() {
        return connected_device_name;
    }

    public static void setConnected_device_name(String connected_device_name) {
        BluetoothHelper.connected_device_name = connected_device_name;
    }
}
