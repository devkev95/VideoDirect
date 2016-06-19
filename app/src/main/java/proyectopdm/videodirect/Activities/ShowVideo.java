package proyectopdm.videodirect.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import proyectopdm.videodirect.R;

public class ShowVideo extends Activity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);

        Intent intent = getIntent();

        videoView = (VideoView) findViewById(R.id.videoView);

        videoView.setVideoURI(Uri.parse(intent.getStringExtra("serverIp")+":8080"));
        videoView.requestFocus();


        videoView.start();
    }
}
