package com.android.sparksoft.smartguardwatch;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;



public class WeightActivity extends Activity {

    BluetoothDevice mmDevice;
    BluetoothAdapter Badap = BluetoothAdapter.getDefaultAdapter();
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);






        /*


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
*/

        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //unregisterReceiver(deviceReceiver);

        //registerReceiver(deviceReceiver, filter);


        Button btnScan = (Button)findViewById(R.id.btnWeightScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Badap.startDiscovery();
                Badap.startLeScan(leScanCallback);

                BluetoothServerSocket tmp = null;
                /*
                try {

                    // MY_UUID is the app's UUID string, also used by the client code
                    UUID uuid = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
                    //tmp = Badap.listenUsingInsecureRfcommWithServiceRecord("server", uuid);

                    //BluetoothSocket sock;
                    //sock = tmp.accept(10000);
                    //Log.d("BEACONBT", sock.getRemoteDevice().getName() + " connected");

                } catch (IOException e) {
                    Log.d("BEACONBT", "failed to connect");

                }*/





                }



        });


    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d("BEACONBT", device.getName());
            UUID uuid = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
            try {
                BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                socket.connect();
                Log.d("BEACONBT", "connected");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("BEACONBT", "failed");
            }

            //socket.connect();

            device.fetchUuidsWithSdp();


            ParcelUuid[] uuids =  device.getUuids();
            for(ParcelUuid uuidd: uuids)
            {
                //Log.d("BEACONBT", uuid.toString());
                Log.d("BEACONBT", device.getName() + " " + uuidd.toString());
            }



        }


    };





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
                Log.d("BEACONBT", device.getName() + " " + device.getAddress() + "\n");
                if(device.getName().contains("Electronic Scale"))
                {
                    //Badap.cancelDiscovery();


/*
                    Log.d("Bonded", device.getName());
                    byte[] pinBytes;
                    try {
                        pinBytes = ("0000").getBytes("UTF-8");
                        device.setPin(pinBytes);
                        device.setPairingConfirmation(true);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    ;
                    //setPairing confirmation if neeeded


                    try {


                        Class<?> clazz = device.getClass();// badap.getRemoteDevice(bd.getAddress()).getClass();
                        Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};

                        Method m = null;
                        m = clazz.getMethod("createRfcommSocket", paramTypes);
                        Object[] params = new Object[] {Integer.valueOf(1)};
                        UUID uuid = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");

                        BluetoothSocket socket = (BluetoothSocket) m.invoke(device, params);
                        //BluetoothServerSocket server = Badap.listenUsingRfcommWithServiceRecord(device.getName(), uuid);
                        //socket = server.accept(10000);

                        //device.fetchUuidsWithSdp();
                        //ParcelUuid[] puuids = device.getUuids();
                        //for (ParcelUuid puid: puuids) {
                        //    Log.d("BEACONBT", puid.getUuid().toString());
                        //}

                        //BluetoothServerSocket bluetoothServerSocket = null;
                        //BluetoothSocket socket = Badap.listenUsingRfcommWithServiceRecord(device.getName(), uuid);

                        //BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);

                        //socket.connect();

                            Log.d("BEACONBT", device.getName() + " is connected");




                        //fallbackSocket.connect();


                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        Log.d("BEACONBT", device.getName() + " is missing");
                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
*/
                }

            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {

            }
        }
    };


}


