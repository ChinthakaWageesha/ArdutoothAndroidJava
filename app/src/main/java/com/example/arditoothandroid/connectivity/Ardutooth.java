package com.example.arditoothandroid.connectivity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;

public class Ardutooth {

  static final String TAG = "Ardutooth";
  static final java.util.UUID UUID =
      java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  private static Activity mActivity;
  private static BluetoothHandler mBtHandler;
  private static Ardutooth instance = null;

  public static Ardutooth getInstance(Activity activity) {
    mActivity = activity;
    if (instance == null) {
      instance = new Ardutooth();
    }
    return instance;
  }

  private Ardutooth() {
    mBtHandler = BluetoothHandler.getInstance(mActivity);
    isConnected();
  }

  private boolean isConnected() {
    try {
      mBtHandler.retrieveConnection();
    } catch (Exception e) {
      Log.d(Ardutooth.TAG, "An error occurred while retrieving connection", e);
    }
    return mBtHandler.connected;
  }

  public BluetoothSocket getSocket() {
    return mBtHandler.getSocket();
  }

  public BluetoothDevice getDeviceConnected() {
    return mBtHandler.getDeviceConnected();
  }

  public void setConnection() {
    mBtHandler.createConnection();
  }

  public void disconnect() {
    mBtHandler.closeConnection();
  }

  public void sendInt(int value) {
    if (mBtHandler.getSocket() != null) {
      try {
        mBtHandler.getOutputStream().write(String.valueOf(value).concat("\n").getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void sendShort(short value) {
    if (mBtHandler.getSocket() != null) {
      try {
        mBtHandler.getOutputStream().write(String.valueOf(value).concat("\n").getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void sendLong(long value) {
    if (mBtHandler.getSocket() != null) {
      try {
        mBtHandler.getOutputStream().write(String.valueOf(value).concat("\n").getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void sendFloat(float value) {
    if (mBtHandler.getSocket() != null) {
      try {
        mBtHandler.getOutputStream().write(String.valueOf(value).concat("\n").getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void sendDouble(double value) {
    if (mBtHandler.getSocket() != null) {
      try {
        mBtHandler.getOutputStream().write(String.valueOf(value).concat("\n").getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void sendChar(char value) {
    if (mBtHandler.getSocket() != null) {
      try {
        mBtHandler.getOutputStream().write(String.valueOf(value).concat("\n").getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void sendString(String value) {
    if (mBtHandler.getSocket() != null) {
      try {
        mBtHandler.getOutputStream().write(String.valueOf(value).concat("\n").getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void sendBoolean(boolean value) {
    if (mBtHandler.getSocket() != null) {
      try {
        mBtHandler.getOutputStream().write(String.valueOf(value).concat("\n").getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
