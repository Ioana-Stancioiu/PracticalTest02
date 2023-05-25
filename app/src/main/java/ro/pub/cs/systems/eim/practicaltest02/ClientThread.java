package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private final String address;
    private final int port;
    private Socket socket;

    private String clientInput;
    private TextView resultTextView;
    private ImageView resultImageView;

    public ClientThread(String address, int port, String clientInput, TextView resultTextView) {
        this.address = address;
        this.port = port;
        this.clientInput = clientInput;
        this.resultTextView = resultTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            Log.i(Constants.TAG, "[CLIENT THREAD] Connection opened with " + socket.getInetAddress() + ":" + port);

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            // Send data to server
            printWriter.println(clientInput);
            printWriter.flush();

            // Take result from server
            String result = bufferedReader.readLine();
            Log.i(Constants.TAG, "[CLIENT THREAD] Getting the information from the server: " + result);
            final String finalResult = result;

            // Display result in UI
            resultTextView.post(() -> resultTextView.setText(finalResult));
        } catch (IOException e) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + e.getMessage());
        }  finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    ioException.printStackTrace();
                }
            }
        }
    }
}
