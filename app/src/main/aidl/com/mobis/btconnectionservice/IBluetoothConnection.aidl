package com.mobis.btconnectionservice;

/**
 * @brief This interface provides a callback for SystemService Started
 */
interface IBluetoothConnection {
    //void onSystemServiceReady();
    void onClicked(int btn);
    int getPasskey(String bdAddr);
}