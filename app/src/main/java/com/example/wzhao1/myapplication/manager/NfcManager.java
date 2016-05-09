package com.example.wzhao1.myapplication.manager;

import android.nfc.tech.TagTechnology;
import android.os.Parcelable;

import java.io.IOException;

/**
 * Created by wzhao1 on 16/5/9.
 */
public interface NfcManager {

    void connect() throws IOException;

    boolean isConnected();

    void writeApdus(byte[][] data);

    void close() throws IOException;

    void setIsoDep(Parcelable parcelableExtra);

}
