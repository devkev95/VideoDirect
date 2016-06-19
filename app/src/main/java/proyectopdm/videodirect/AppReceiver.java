package proyectopdm.videodirect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.net.InetAddress;

import proyectopdm.videodirect.Activities.ShowVideo;
import proyectopdm.videodirect.WiFiP2PUtilities.ConnectionListener;

/**
 * Created by kevin on 06-09-16.
 */
public class AppReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Activity activity;
    private WifiP2pManager.PeerListListener peerListListener;
    ConnectionListener connectionListener;

    public AppReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, Activity activity,
                       WifiP2pManager.PeerListListener peerListListener) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        this.peerListListener = peerListListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

            } else {

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            if (manager != null) {
                manager.requestPeers(channel, peerListListener);
            }
            Log.d("VideoDirect", "P2P peers changed");



        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo =  intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                connectionListener = new ConnectionListener();
                manager.requestConnectionInfo(channel, connectionListener);
                InetAddress groupOwnerAddress = connectionListener.getGroupOwnerAddress();
                if(groupOwnerAddress != null) {
                    Intent showBroadcasting = new Intent();
                    showBroadcasting.setClassName("proyectopdm.videodirect.Activities","proyectopdm.videodirect." +
                            "Activities.ShowVideo");
                    showBroadcasting.putExtra("serverIp", groupOwnerAddress.getHostAddress());
                    showBroadcasting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(showBroadcasting);
                }
            }


        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        }

    }
}
