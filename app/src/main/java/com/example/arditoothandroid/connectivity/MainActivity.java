package com.example.arditoothandroid.connectivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.arditoothandroid.R;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,
    MapperClass.MapperCallback {

  @BindView(R.id.et_character_string) EditText etCharacterString;
  @BindView(R.id.tv_no_letter_found) TextView tvNoLetterFound;

  private TextToSpeech mTextToSpeech;
  private int bytesIn;
  private int rate = 0;
  private int startTime;

  BluetoothAdapter mBluetoothAdapter;
  BluetoothDevice mBtDevice;
  BluetoothSocket mBtSocket;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    mTextToSpeech = new TextToSpeech(getApplicationContext(), this);
  }

  private int getSampleRate() {
    long millis = System.currentTimeMillis();
    int stopTime = (int) millis;
    int time_diff = stopTime - this.startTime;
    if (time_diff != 0) {
      this.rate = 1000 / time_diff;
    }
    millis = System.currentTimeMillis();
    this.startTime = (int) millis;
    return this.rate;
  }

  private void speekCharacter(String speakMessage) {
    mTextToSpeech.speak(speakMessage, TextToSpeech.QUEUE_FLUSH, null);
  }

  private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();

      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        String deviceName = device.getName();
        String deviceMacAddress = device.getAddress();

        Toast.makeText(context, deviceName, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, deviceMacAddress, Toast.LENGTH_SHORT).show();
      }
    }
  };

  void connectToBluetooth() {

    // Bluetooth mac address of the target bluetooth host
    String HC05 = "98:D3:32:20:FA:17";

    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter == null) {
      Toast.makeText(this, "Does not support bluetooth", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(this, "Does support bluetooth", Toast.LENGTH_SHORT).show();
    }

    if (this.mBtSocket != null && this.mBtDevice != null) {
      try {
        this.mBtDevice = this.mBluetoothAdapter.getRemoteDevice(HC05);
        this.mBtSocket = this.mBtDevice.createRfcommSocketToServiceRecord(
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
      } catch (IOException e) {
        e.printStackTrace();
      }

      try {
        this.mBtSocket.connect();
      } catch (IOException e2) {
        e2.printStackTrace();
        try {
          this.mBtSocket.close();
          Toast.makeText(this, "Socket closed", Toast.LENGTH_SHORT).show();
        } catch (IOException e3) {
          Toast.makeText(this, "Socket cannot be closed", Toast.LENGTH_SHORT).show();
        }
      }

      if (this.mBtSocket.isConnected()) {
        Toast.makeText(this, "Bluetooth-Device connected", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this, "Bluetooth-Device failed to connect", Toast.LENGTH_SHORT).show();
      }

      Thread thread = new Thread(new MyThread());
      thread.start();
    } else {
      Toast.makeText(this, "Turn on your bluetooth connectivity", Toast.LENGTH_SHORT).show();
    }
  }

  Handler handler = new Handler(Looper.getMainLooper()) {
    public void handleMessage(Message inputMessage) {

      switch (inputMessage.what) {
        case 43:
          char[] readBuffer = (char[]) inputMessage.obj;

          int rate = getSampleRate();

          if (rate > 6) {
            Toast.makeText(MainActivity.this,
                "The Input is very fast. Please change to Receiver-View.", Toast.LENGTH_SHORT)
                .show();
          }

          String readMessage = new String(readBuffer, 0, inputMessage.arg1);
          readMessage.trim();

          etCharacterString.setText(readMessage);

          try {
            String character = MapperClass.stringMapper(readMessage);

            if (character.isEmpty()) {
              etCharacterString.setText("");
              tvNoLetterFound.setVisibility(View.VISIBLE);
            } else {
              tvNoLetterFound.setVisibility(View.GONE);
              speekCharacter("The Letter is " + String.valueOf(character));
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
          return;

        default:
          Toast.makeText(MainActivity.this, "Default ::", Toast.LENGTH_SHORT).show();
      }
    }
  };

  @OnClick(R.id.btn_connect) public void onConnectbtnClicked() {
   connectToBluetooth();
  }

  class MyThread implements Runnable {
    MyThread() {
    }

    @Override
    public void run() {
      do {
      } while (!MainActivity.this.mBtSocket.isConnected());
      new ConnectedThread(MainActivity.this.mBtSocket).start();
    }
  }

  private class ConnectedThread extends Thread {
    private final BluetoothSocket mBtSocket;
    private final InputStream mInStream;
    private final OutputStream mOutStream;

    ConnectedThread(BluetoothSocket socket) {
      this.mBtSocket = socket;
      InputStream tmpIn = null;
      OutputStream tmpOut = null;
      try {
        tmpIn = socket.getInputStream();
        tmpOut = socket.getOutputStream();
      } catch (IOException e) {
        e.printStackTrace();
      }
      this.mInStream = tmpIn;
      this.mOutStream = tmpOut;
    }

    public void run() {
      Log.i("Tag", "Connected Thread started - looking for incoming massages");

      char[] buffer = new char[1024];
      while (true) {
        try {
          buffer[bytesIn] = (char) this.mInStream.read();

          if (buffer[bytesIn] == ';') {
            handler.obtainMessage(43, bytesIn, -1, buffer).sendToTarget();
            bytesIn = 0;
            buffer = new char[1024];
          } else {
            bytesIn = bytesIn + 1;
          }
        } catch (IOException e) {
          e.printStackTrace();
          Log.e("Tag", "Exception run() Message");
          return;
        }
      }
    }

    public void write(byte[] bytes) {
      try {
        this.mOutStream.write(bytes);

        Log.i("Tag", "Message out: " + bytes);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public void cancel() {
      try {
        this.mBtSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override public void onInit(int status) {
    if (status == TextToSpeech.SUCCESS) {
      int result = mTextToSpeech.setLanguage(Locale.UK);

      if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
        Toast.makeText(this, "Something wrong with Text to speech. Please try again soon.",
            Toast.LENGTH_SHORT).show();
      }
    } else {
      Toast.makeText(this, "Text to speech is not successful.", Toast.LENGTH_SHORT).show();
    }
  }

  @Override public void mapperCallback() {

  }
}
