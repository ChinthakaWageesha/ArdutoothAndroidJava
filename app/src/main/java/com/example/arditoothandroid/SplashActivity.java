package com.example.arditoothandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.example.arditoothandroid.connectivity.MainActivity;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadMain();
  }

  private void loadMain() {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override public void run() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
      }
    }, 3000);
  }
}
