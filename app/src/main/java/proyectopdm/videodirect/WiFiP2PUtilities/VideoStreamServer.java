package proyectopdm.videodirect.WiFiP2PUtilities;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by kevin on 06-12-16.
 */
public class VideoStreamServer extends NanoHTTPD {

    private File file;

    public VideoStreamServer(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> headers = session.getHeaders();
        int constantLength = 307200 ;
        long fileLength=0, range;

        if (headers.containsKey("range"))
        {
            String contentRange = headers.get("range");
            range = Integer.parseInt(contentRange.substring(contentRange.indexOf("=") + 1, contentRange.indexOf("-")));

        }
        else
        {
            range = 0;

        }


        byte[] buffer = new byte[0];


        long bufLength=0;


        try {

            RandomAccessFile ff =new RandomAccessFile(file,"rw" );
            long remainingChunk = ff.length() - range; //remaining
            fileLength = ff.length();
            if (remainingChunk < constantLength){
                bufLength= remainingChunk; //means last part

            }

            else
                bufLength = constantLength;
            if (range !=0)
                ff.seek(range);

            buffer= new byte[(int)bufLength];


            ff.read(buffer);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Response response = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT,"video/3gpp",new ByteArrayInputStream(buffer),
                bufLength);

        response.addHeader("Content-Length",(fileLength)+"");
        response.addHeader("Content-Range",String.format("bytes %s-%s/%s", range,(range+bufLength), fileLength));
        Log.e("SERVER", "Inside server sent " + String.format("bytes %s-%s/%s", range, (range + bufLength), fileLength));
        return response;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
