package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread {
    private final ServerThread serverThread;
    private final Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            return;
        }

        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            // Get input from client
            String url = bufferedReader.readLine();
            HashMap<String, DataModel> data = serverThread.getData();
            DataModel dataModel;

            // Check if input is in cache
            if (data.containsKey(url)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                dataModel = data.get(url);
            } else {
                // Get data from server
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();

                // Get page source code
                String pageSourceCode = null;
                if (httpEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpEntity);
                }

                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }

                // Create data model with parsed data
                dataModel = new DataModel(pageSourceCode);

                // Add data to cache
                serverThread.setData(url, dataModel);
            }

            if (dataModel == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Data Model Information is null!");
                return;
            }

            // Send data to client
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Sending the information to the client...");
            printWriter.println(dataModel.getPageBody());
            printWriter.flush();
        } catch (IOException e) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
