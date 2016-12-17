package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import static com.example.myapplication.Constants.HIGH;
import static com.example.myapplication.Constants.LOW;
import static com.example.myapplication.Constants.WEATHER_DATA;
import static com.example.myapplication.Constants.WEATHER_ID;

/**
 * Created by 3zcs on 10/12/16.
 */

public class WearWeatherService extends WearableListenerService implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    public final String TAG = "WearWeatherService" ;
    public GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: " + "onConnected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed failed");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo(WEATHER_DATA) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateWeather(dataMap.getInt(WEATHER_ID) ,dataMap.getDouble(HIGH) ,dataMap.getDouble(LOW));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    // Our method to update the weather
    private void updateWeather(int id , double high , double low){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(WEATHER_DATA,this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(WEATHER_ID , id);
        editor.putString(HIGH , String.valueOf(high));
        editor.putString(LOW , String.valueOf(low));
        editor.commit();
    }

}
