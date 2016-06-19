package proyectopdm.videodirect.WiFiP2PUtilities;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import java.net.InetAddress;

import proyectopdm.videodirect.Activities.ShowVideo;

/**
 * Created by kevin on 06-11-16.
 */
public class ConnectionListener implements WifiP2pManager.ConnectionInfoListener{

    private InetAddress groupOwnerAddress;

    public ConnectionListener(){

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

        // InetAddress from WifiP2pInfo struct.
        groupOwnerAddress = null;

        if (info.groupFormed) {
            groupOwnerAddress = info.groupOwnerAddress;
        }

    }

    public InetAddress getGroupOwnerAddress() {
        return groupOwnerAddress;
    }
}
