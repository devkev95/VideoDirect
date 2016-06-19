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

    Context context;

    public ConnectionListener(Context context){
        this.context = context;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

        // InetAddress from WifiP2pInfo struct.
        InetAddress groupOwnerAddress = info.groupOwnerAddress;
        if (info.groupFormed) {
            Intent intent = new Intent(context, ShowVideo.class);
            intent.putExtra("serverIp", groupOwnerAddress.getHostAddress());
            context.startActivity(intent);
        }

    }
}
