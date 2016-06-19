package proyectopdm.videodirect.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import proyectopdm.videodirect.AppReceiver;
import proyectopdm.videodirect.R;

public class BuscarActivity extends Activity {

    final HashMap<String, String> buddies = new HashMap<String, String>();
    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    private ListView servicesListView;
    private ArrayAdapter<String> servicesAdapter;
    private ArrayList<WifiP2pDevice> devicesList = new ArrayList<WifiP2pDevice>();
    private AppReceiver receiver;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pDnsSdServiceRequest serviceRequest;
    private Handler serviceDiscoveringHandler;
    private Runnable serviceDiscoveringRunnable = new Runnable() {
        @Override
        public void run() {
            discoverServices();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        servicesListView = (ListView) findViewById(R.id.listServices);
        servicesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        servicesListView.setAdapter(servicesAdapter);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);



        servicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connect(devicesList.get(position));
            }
        });

        serviceDiscoveringHandler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new AppReceiver (manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void buscar(View v) {
        prepareServiceDiscovery();
        discoverServices();
    }

    private void discoverServices() {

        manager.removeServiceRequest(channel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                manager.addServiceRequest(channel, serviceRequest, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                serviceDiscoveringHandler.postDelayed(serviceDiscoveringRunnable,30000);
                            }

                            @Override
                            public void onFailure(int reason) {
                                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                                if (reason == WifiP2pManager.P2P_UNSUPPORTED)
                                    Log.d("ERROR", "P2P isn't supported on this device.");
                            }

                        });
                    }

                    @Override
                    public void onFailure(int reason) {

                    }
                });
            }

            @Override
            public void onFailure(int reason) {

            }
        });





    }

    private void prepareServiceDiscovery(){
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {

            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomain, Map record, WifiP2pDevice device) {
                    Log.d("DirectVideo", "DnsSdTxtRecord available -" + record.toString());

                    buddies.put(device.deviceAddress, (String) record.get("userName"));

            }
        };

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {

                if (buddies.containsKey(resourceType.deviceAddress)) {
                    resourceType.deviceName = buddies
                            .containsKey(resourceType.deviceAddress) ? buddies
                            .get(resourceType.deviceAddress) : resourceType.deviceName;

                    // Add to the custom adapter defined specifically for showing
                    // wifi devices.
                    devicesList.add(resourceType);
                    servicesAdapter.add(resourceType.deviceName);
                    servicesAdapter.notifyDataSetChanged();
                    Log.d("DirectVideo", "onBonjourServiceAvailable " + instanceName);

                }
            }
        };

        manager.setDnsSdResponseListeners(channel, servListener, txtListener);

        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
    }

    private void connect(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 15;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(BuscarActivity.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
