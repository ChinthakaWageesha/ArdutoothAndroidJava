package com.example.arditoothandroid.connectivity;

public class MapperClass {

  static String message = "";
  static String expression = "";
  MapperCallback mapperCallback;

  public void callback(MapperCallback callback) {
    this.mapperCallback = callback;
  }

  static String stringMapper(String string) {
    switch (string) {
      //todo your string mapping function here
      //eg.

      // if the string coming from the device is 1000000000T it' letter A
      case "100000000000T":
        return "A";
      case "101000000000T":
        return "B";
      case "110000000000T":
        return "C";
      default:
        return "";
    }
  }

  public interface MapperCallback {
    void mapperCallback();
  }
}
