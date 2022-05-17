/*  Author: Yun Chi Leong
 *  COSC 4730 Mobile Programming Fall 2021
 *  Program 6
 *  December 5th, 2021
 *  A client application that connects to the Battle Bot server
 *  and has move and fire controls. */

package com.example.battlebotclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    //Hard coded Server port and Bot's stat
    public static final int SERVERPORT = 3012;
    public static final String BOT_SETUP = "Yun 0 0 3";
    private LinearLayout msgWindow;
    private Handler handler;
    private ClientThread clientThread;
    private Thread thread;
    private int green, red, white;
    FloatingActionButton up, down, left, right;
    FloatingActionButton fireUp, fireDown, fireLeft, fireRight;
    private EditText hostname;
    private View hostname_window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        green = ContextCompat.getColor(this, R.color.green);
        red = ContextCompat.getColor(this, R.color.red);
        white = ContextCompat.getColor(this, R.color.white);
        handler = new Handler();
        msgWindow = findViewById(R.id.msg_list);
        hostname = findViewById(R.id.hostname_et);
        hostname_window = findViewById(R.id.hostname_connect_layout);
        up = findViewById(R.id.up_button);
        down = findViewById(R.id.down_button);
        left = findViewById(R.id.left_button);
        right = findViewById(R.id.right_button);
        fireUp = findViewById(R.id.fireUp);
        fireDown = findViewById(R.id.fireDown);
        fireLeft = findViewById(R.id.fireLeft);
        fireRight = findViewById(R.id.fireRight);

        // Set Movement buttons listeners
        View.OnTouchListener movementListener = (v, event) -> {
            int move_button = v.getId();
            switch (move_button) {
                case R.id.up_button:
                    clientThread.sendMessage("move 0 -1");
                    break;
                case R.id.down_button:
                    clientThread.sendMessage("move 0 1");
                    break;
                case R.id.left_button:
                    clientThread.sendMessage("move -1 0");
                    break;
                case R.id.right_button:
                    clientThread.sendMessage("move 1 0");
                    break;
            }
            return false;
        };
        up.setOnTouchListener(movementListener);
        down.setOnTouchListener(movementListener);
        left.setOnTouchListener(movementListener);
        right.setOnTouchListener(movementListener);
    }

    // Set fire buttons listeners
    public void fireOnClick(View view) {
        int fire_button = view.getId();
        switch (fire_button) {
            case R.id.fireUp:
                clientThread.sendMessage("fire 0");
                break;
            case R.id.fireDown:
                clientThread.sendMessage("fire 180");
                break;
            case R.id.fireLeft:
                clientThread.sendMessage("fire 270");
                break;
            case R.id.fireRight:
                clientThread.sendMessage("fire 90");
                break;
        }
    }

    // Set exit button onClick
    public void ExitOnClick(View view) throws IOException {
        clientThread.reset();
    }

    // Create new textviews for message window
    public TextView createNewMessage(String message, int color) {
        if (message == null || message.trim().isEmpty()) {
            message = "<Empty message>";
        }
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextColor(color);
        tv.setTextSize(16);
        tv.setPadding(4, 2, 0, 0);
        return tv;
    }

    // Display textviews in scroll view
    public void showMessage(final String message, final int color) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                msgWindow.addView(createNewMessage(message, color));
            }
        });
    }

    // Connect client to server
    public void ConnectOnClick(View view) {
            msgWindow.removeAllViews();
            showMessage("Connecting to Server...", white);
            clientThread = new ClientThread();
            thread = new Thread(clientThread);
            thread.start();
            showMessage("Connected.", white);
            up.setEnabled(true);
            down.setEnabled(true);
            left.setEnabled(true);
            right.setEnabled(true);
            fireUp.setEnabled(true);
            fireDown.setEnabled(true);
            fireLeft.setEnabled(true);
            fireRight.setEnabled(true);
            hostname_window.setVisibility(View.GONE);
            hostname.getText().clear();
    }

    class ClientThread implements Runnable {
        private Socket socket;
        private PrintWriter output;
        private BufferedReader input;
        private boolean setup = false;

        @Override
        public void run() {
            String hostIP = hostname.getText().toString().trim();
            try {
                InetAddress serverIP = InetAddress.getByName(hostIP);
                socket = new Socket(serverIP, SERVERPORT);
                output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                try {
                    // Send initial bot setup to server
                    sendMessage(BOT_SETUP);
                    String str = input.readLine().trim();
                    if (str.equals("Info Dead") || str.equals("Info GameOver")) {
                        closeAll();
                        reset();
                    }
                } catch (Exception e) {
                    showMessage("Error happened sending/receiving", green);
                }
            }catch (Exception e) {
                showMessage("[Error] Unable to connect...", white);
            }
        }

        public void closeAll() throws IOException {
            input.close();
            output.close();
            socket.close();
        }

        // Reset views, buttons and flags
        public void reset(){
            hostname_window.setVisibility(View.VISIBLE);
            msgWindow.removeAllViews();
            up.setEnabled(false);
            down.setEnabled(false);
            left.setEnabled(false);
            right.setEnabled(false);
            fireUp.setEnabled(false);
            fireDown.setEnabled(false);
            fireLeft.setEnabled(false);
            fireRight.setEnabled(false);
            setup = false;
        }

        // Listens to server
        public boolean isResponding(){
            try {
                String str = input.readLine();
                showMessage("[Server] " + str, red);
                if(str.equals("Info Dead") || str.equals("Info GameOver")){
                    closeAll();
                    reset();
                }
                if(str.length()==0){
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }


        // Send messages to Server
        void sendMessage(final String newMsg) {
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        if (null != socket && newMsg != null) {
                            if(!setup || isResponding()) {
                                output.println(newMsg);
                                showMessage("[Client] " + newMsg, green);
                                setup = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        }
    }
