package com.example.arditoothandroid.connectivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class AcceptThread {

  private final BluetoothServerSocket mmServerSocket;

  private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

  public AcceptThread() {
    BluetoothServerSocket tmp = null;
    try {
      tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("MyAPP", Ardutooth.UUID);
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(TAG, "Socket's listen() method failed", e);
    }
    mmServerSocket = tmp;
  }

  public void run() {
    BluetoothSocket socket = null;
    while (true) {
      try {
        socket = mmServerSocket.accept();
      } catch (IOException e) {
        e.printStackTrace();
        Log.e(TAG, "Socket's accept() method failed", e);
        break;
      }

      if (socket != null) {
        try {
          mmServerSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
      }
    }
  }

  public void cancel() {
    try {
      mmServerSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(TAG, "Could not close the connect socket", e);
    }
  }
}
