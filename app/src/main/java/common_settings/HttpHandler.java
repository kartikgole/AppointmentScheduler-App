package common_settings;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.studio.android.utaappointmentscheduler.R;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
/**
 * Created by Deathlord on 11/24/2017.
 */

public class HttpHandler {
    private static final String TAG = HttpHandler.class.getSimpleName();
    private Resources resources;
    public HttpHandler(Resources resources) {
        this.resources = resources;
    }

    public String getJsonCall(String reqUrl,JSONObject jsonObject)  {
        String response = null;
        try {
            String ipAddress = resources.getString(R.string.server_ip);
            URL url = new URL("http://"+ipAddress+"/"+reqUrl);

           // Log.e("x",url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //Log.e("x","1");
            conn.setRequestMethod("POST");
            //Log.e("x","2");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            //Log.e("x","3");
            conn.setDoOutput(true);
            //Log.e("x","4");

            //Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            DataOutputStream writer = new DataOutputStream(conn.getOutputStream());

            //Log.e("x",jsonObject.toString());
            writer.writeBytes(jsonObject.toString());

            writer.close();

            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e);
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e);
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e);
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }


}
