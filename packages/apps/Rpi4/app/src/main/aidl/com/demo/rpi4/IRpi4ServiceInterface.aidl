// IRpi4ServiceInterface.aidl
package com.demo.rpi4;

import com.demo.rpi4.IRpi4ServiceCallback;

// Declare any non-default types here with import statements

interface IRpi4ServiceInterface {
    void register(IRpi4ServiceCallback iRpi4ServiceCallback);
    void unregister();
    void headLight(boolean isTurnedOn);
    void leftIndicator(boolean isStarted);
    void rightIndicator(boolean isStarted);
    void parkLight(boolean isStarted);
    boolean getHeadLightStatus();
    boolean getLeftIndicatorStatus();
    boolean getRightIndicatorStatus();
}