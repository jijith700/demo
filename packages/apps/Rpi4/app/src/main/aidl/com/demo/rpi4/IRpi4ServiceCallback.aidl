// IRpi4ServiceCallback.aidl
package com.demo.rpi4;

// Declare any non-default types here with import statements

interface IRpi4ServiceCallback {
    void onChangeHeadLight(boolean status);
    void onChangeLeftIndicator(boolean status);
    void onChangeRightIndicator(boolean status);
}