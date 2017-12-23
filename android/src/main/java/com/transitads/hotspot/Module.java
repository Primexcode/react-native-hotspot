package com.transitads.hotspot;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;

public class Module extends ReactContextBaseJavaModule {

  /*private static final String DURATION_SHORT_KEY = "SHORT";
  private static final String DURATION_LONG_KEY = "LONG";*/


  boolean wasAPEnabled = false;
  static WifiAP wifiAp;
  private WifiManager wifi;

  public Module(ReactApplicationContext reactContext) {
    super(reactContext);

    wifiAp = new WifiAP();
    wifi = (WifiManager) reactContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

  }

  @Override
  public String getName() {
    return "Hotspot";
  }

  /*@Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
    constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
    return constants;
  }*/

  /*@ReactMethod
  public void show(String message, int duration) {
    Toast.makeText(getReactApplicationContext(), message, duration).show();
  }*/

  @ReactMethod
  public static String updateWifiStatus() {
      if (wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLED || wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLING) {
        return "Turn off";
      } else {
        return "Turn on";
      }
  }

  @ReactMethod
  public String onResume() {
    if (wasAPEnabled) {
      if (wifiAp.getWifiAPState()!=wifiAp.WIFI_AP_STATE_ENABLED && wifiAp.getWifiAPState()!=wifiAp.WIFI_AP_STATE_ENABLING){
        wifiAp.toggleWiFiAP(wifi, getReactApplicationContext());
      }
    }
    return updateWifiStatus();
  }

  @ReactMethod
  public String onPause() {
    boolean wifiApIsOn = wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLED || wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLING;
    if (wifiApIsOn) {
      wasAPEnabled = true;
      wifiAp.toggleWiFiAP(wifi, getReactApplicationContext());
    } else {
      wasAPEnabled = false;
    }
    return updateWifiStatus();
  }

  @ReactMethod
  private void startHotspot() {
    delayOftwentyMin();

    // enabling wifi tethering service
    wifiAp.toggleWiFiAP(wifi, getReactApplicationContext());

  }

  private void delayOftwentyMin() {

    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        //tethering will disable if enabled
        boolean wifiApIsOn = wifiAp.getWifiAPState() == wifiAp.WIFI_AP_STATE_ENABLED || wifiAp.getWifiAPState() == wifiAp.WIFI_AP_STATE_ENABLING;
        if (wifiApIsOn) {
          wasAPEnabled = false;
          wifiAp.toggleWiFiAP(wifi, getReactApplicationContext());
        }
      }
    }, 2 * 60 * 1000);
  }

}