package proyectopdm.videodirect.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import proyectopdm.videodirect.AppReceiver;
import proyectopdm.videodirect.R;

public class BuscarActivity extends Activity {

    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    private ListView servicesListView;
    private ArrayAdapter<String> servicesAdapter;
    private ArrayList<WifiP2pDevice> devicesList = new ArrayList<WifiP2pDevice>();
    private AppReceiver receiver;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            // Out with the old, in with the new.
            devicesList.clear();
            devicesList.addAll(peerList.getDeviceList());
            servicesAdapter.clear();

            for(WifiP2pDevice device:devicesList){
                servicesAdapter.add(device.deviceName);
            }

            // If an AdapterView is backed by this data, notify it
            // of the change.  For instance, if you have a ListView of available
            // peers, trigger an update.
            servicesAdapter.notifyDataSetChanged();
            if (devicesList.size() == 0) {
                Log.d("Warning", "No devices found");
                return;
            }
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new AppReceiver (manager, channel, this, peerListListener);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void buscar(View v) {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(BuscarActivity.this, "Fallo la busqueda. Reintentar", Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(BuscarActivity.this, "Fallo la conexión. Reintentar", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
