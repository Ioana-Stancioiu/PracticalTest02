package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {
    private ServerSocket serverSocket = null;
    private final HashMap<String, DataModel> data;
    public ServerThread(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.data = new HashMap<>();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public synchronized void setData(String clientInput, DataModel dataModel) {
        this.data.put(clientInput, dataModel);
    }

    public synchronized HashMap<String, DataModel> getData() {
        return data;
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (IOException clientProtocolException) {
            clientProtocolException.printStackTrace();
        }
    }
    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
