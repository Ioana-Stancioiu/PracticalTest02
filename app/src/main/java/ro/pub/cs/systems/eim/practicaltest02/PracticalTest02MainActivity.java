package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {
    private EditText serverPortEditText;
    private EditText clientAddressEditText;
    private EditText clientPortEditText;
    private Button connectButton;

    private Button getInformationButton;
    private EditText clientInput;
    private TextView resultTextView;

    private ServerThread serverThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        clientAddressEditText = (EditText)findViewById(R.id.client_address_edit_text);
        clientPortEditText = (EditText)findViewById(R.id.client_port_edit_text);
        connectButton = (Button)findViewById(R.id.connect_button);

        getInformationButton = (Button)findViewById(R.id.get_info_button);
        clientInput = (EditText)findViewById(R.id.client_input_edit_text);
        resultTextView = (TextView)findViewById(R.id.result_text_view);

        connectButton.setOnClickListener(view -> {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Toast.makeText(getApplicationContext(), "Could not create server thread!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread.start();
        });

        getInformationButton.setOnClickListener(view -> {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String clientInputString = clientInput.getText().toString();
            if (clientInputString.isEmpty()) {
                Toast.makeText(getApplicationContext(), "URL should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            ClientThread clientThread = new ClientThread(
                    clientAddress,
                    Integer.parseInt(clientPort),
                    clientInputString,
                    resultTextView
            );
            clientThread.start();
        });
    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}