package com.example.wzhao1.myapplication.manager;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.TagTechnology;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;

import com.example.wzhao1.myapplication.util.log.DLog;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

/**
 * Created by wzhao1 on 16/5/9.
 */
public class NfcManagerImpl implements NfcManager {

    private IsoDep isoDep;
    private Bus bus;
    private final static ExecutorService NFC_SERVICE = Executors.newCachedThreadPool();
    private OnNfcSuccessListener onNfcSuccessListener;

    @Inject
    public NfcManagerImpl(Bus bus, OnNfcSuccessListener onNfcSuccessListener) {
        this.bus = bus;
        this.onNfcSuccessListener = onNfcSuccessListener;
    }

    public void setIsoDep(Parcelable p) {
        if (p != null) {
            Tag tag = (Tag) p;
            isoDep = isoDep != null ? isoDep : IsoDep.get(tag);
        }
    }

    @Override
    public void connect() throws IOException {
        if(isoDep.isConnected()) {
            isoDep.close();
        }
        isoDep.connect();
    }

    @Override
    public boolean isConnected() {
        return isoDep.isConnected();
    }

    @Override
    public void writeApdus(byte[][] data) {
        NFC_SERVICE.execute(new NfcRunnable(bus, data));
    }

    @Override
    public void close() throws IOException {
        if (isConnected()) {
            isoDep.close();
        }
    }

    class NfcRunnable implements Runnable {
        Bus bus;
        byte[][] data;

        public NfcRunnable(Bus bus, byte[][] data) {
            this.bus = bus;
            this.data = data;
        }

        @Override
        public void run() {
            try {
                connect();
                byte[] respApdu;
                for (int i = 0; i < data.length; i++) {
                    byte[] apdu = data[i];
                    Log.e(this.getClass().getSimpleName(), "transceive data: " + bytesToHexString(apdu));
                    respApdu = isoDep.transceive(apdu);
                    if (respApdu.length == 2 && respApdu[0] != 0x90 && respApdu[1] != 0x00) {
                        onNfcSuccessListener.onSuccess(new NfcResponseEvent(respApdu, false));
                        return;
                    }
                    if (i == data.length - 1) {
                        onNfcSuccessListener.onSuccess(new NfcResponseEvent(respApdu, true));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class NfcResponseEvent {
        private final String responseData;
        private final boolean isSuccess;

        public NfcResponseEvent(byte[] respByte, boolean isSuccess) {
            this.responseData = bytesToHexString(respByte);
            this.isSuccess = isSuccess;
        }

        public String getResponseData() {
            return responseData;
        }

        public boolean isSuccess() {
            return isSuccess;
        }
    }

    /* *
             * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)
             * 来转换成16进制字符串。
             * @param src byte[] data
             * @return hex string
             */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
