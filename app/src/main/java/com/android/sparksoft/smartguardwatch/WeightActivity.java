package com.android.sparksoft.smartguardwatch;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import java.util.Set;

public class WeightActivity extends Activity {

    BluetoothDevice mmDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        BluetoothAdapter Badap = BluetoothAdapter.getDefaultAdapter();







            Set<BluetoothDevice> pairedDevices= Badap.getBondedDevices();

            if (pairedDevices.size() > 0) {

                for (BluetoothDevice device : pairedDevices) {

                    Log.d("BLUETOOTH", device.getName() + " ::" + device.getAddress() + "\n");
                    if(device.getName().equals("Electronic Scale"))
                    {
                        mmDevice = device;
                        break;
                    }

                }
            }



        /*
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(MydeviceReceiver, filter);
        */

    }


    /*
    private final BroadcastReceiver deviceReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if( !PairedDeviceNames.contains(device) && !newDevices.contains(device))
                    newDevices.add(device);
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                Log.v(" ","discovery Finished ");
                if(newDevices.size() != 0)
                {
                    deviceList.invalidateViews();
                    sectionAdapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(YourActivity.this, "No New Devices Found", Toast.LENGTH_LONG).show();
                }
            }
        }
    };
    */

}
