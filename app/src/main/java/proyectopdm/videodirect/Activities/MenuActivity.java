package proyectopdm.videodirect.Activities;

import android.app.ListActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import proyectopdm.videodirect.AppReceiver;

public class MenuActivity extends ListActivity {

    String[] menu = {"Iniciar nuevo streaming", "Buscar streaming",};
    String[] activities = {"MainActivity", "BuscarActivity"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu));


    }


    @Override
    protected void onListItemClick(ListView l,View v,int position,long id) {
        super.onListItemClick(l, v, position, id);
        if (position < 2) {
            String nombreValue = activities[position];
            try {
                Class<?> clase = Class.forName("proyectopdm.videodirect.Activities." + nombreValue);
                Intent inte = new Intent(this, clase);
                this.startActivity(inte);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
