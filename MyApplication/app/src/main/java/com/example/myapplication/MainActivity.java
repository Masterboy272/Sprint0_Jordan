package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.jdphifon.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 11223344;

    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private Medidas medida = new Medidas(1, 1, 1, 1);
    private Button mandarPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mandarPost = findViewById(R.id.mandarPost);
        mandarPost.setOnClickListener(v -> botonEnviarPulsado(v));

        inicializarBluetooth();
    }

    // Initialize Bluetooth
    private void inicializarBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check if the device supports Bluetooth
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check Bluetooth permissions for Android 12 and higher
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_CODE_PERMISSIONS);
            return;
        }

        // Enable Bluetooth if it's disabled
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
            Log.d(TAG, "Failed to obtain Bluetooth LE scanner.");
            return;
        }

        if (!hasPermissions()) {
            requestPermissions();
        } else {
            Log.d(TAG, "All required permissions are already granted.");
        }
    }

    // Check if permissions are granted
    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // Request necessary permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_CODE_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissions granted.");
            } else {
                Log.d(TAG, "Permissions denied.");
                Toast.makeText(this, "Permissions required for Bluetooth functionality.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Start scanning for all Bluetooth LE devices
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(TAG, "Starting scan for all BTLE devices.");

        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                mostrarInformacionDispositivoBTLE(result);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(TAG, "Batch scan results received.");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(TAG, "Scan failed with error code: " + errorCode);
            }
        };

        if (bluetoothLeScanner == null) {
            Log.d(TAG, "BluetoothLeScanner is null. Cannot start scan.");
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }

        bluetoothLeScanner.startScan(scanCallback);
    }

    // Display Bluetooth device information
    @SuppressLint("MissingPermission")
    private void mostrarInformacionDispositivoBTLE(ScanResult result) {
        if (result == null || result.getDevice() == null || result.getScanRecord() == null) {
            Log.d(TAG, "ScanResult or device is null.");
            return;
        }

        BluetoothDevice device = result.getDevice();
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locManager == null) {
            Log.d(TAG, "LocationManager is null.");
            return;
        }

        @SuppressLint("MissingPermission") Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        int rssi = result.getRssi();
        byte[] bytes = result.getScanRecord().getBytes();

        Log.d(TAG, "Device: " + device.getName() + ", RSSI: " + rssi);
        Log.d(TAG, "Bytes: " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);
        Log.d(TAG, "UUID: " + Utilidades.bytesToHexString(tib.getUUID()));

        // Update measurements
        if (loc != null) {
            medida.setLatitud(loc.getLatitude());
            medida.setLongitud(loc.getLongitude());
        }
        medida.setMedicion(Utilidades.bytesToInt(tib.getMajor()));
        medida.setTipoSensor(Utilidades.bytesToInt(tib.getMinor()));
    }

    // Stop scanning for Bluetooth devices
    private void detenerBusquedaDispositivosBTLE() {
        if (bluetoothLeScanner == null || scanCallback == null) {
            Log.d(TAG, "BluetoothLeScanner or ScanCallback is null. Cannot stop scan.");
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        bluetoothLeScanner.stopScan(scanCallback);
        scanCallback = null;
        Log.d(TAG, "Stopped scanning for devices.");
    }

    // Handle post button click
    public void botonEnviarPulsado(View view) {
        Log.d(TAG, "Sending POST request.");

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locManager == null) {
            Log.d(TAG, "LocationManager is null. Cannot get location.");
            return;
        }

        @SuppressLint("MissingPermission") Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        String urlDestino = "http://192.168.59.175/Proyecto_Biometria/src/api/v1.0/index.php";

        JSONObject postData = new JSONObject();
        try {
            postData.put("Medicion", medida.getMedicion());
            postData.put("TipoSensor", medida.getTipoSensor());
            if (loc != null) {
                postData.put("Latitud", loc.getLatitude());
                postData.put("Longitud", loc.getLongitude());
            }

           /* AndroidNetworking.post(urlDestino)
                    .addHeaders("Content-Type", "application/json; charset=utf-8")
                    .addJSONObjectBody(postData)
                    .setTag("post_data")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "Data saved successfully.");
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.d(TAG, "Error: " + error.getMessage());
                        }
                    });
*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Button actions
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        buscarTodosLosDispositivosBTLE();
    }

    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        detenerBusquedaDispositivosBTLE();
    }
}
