package com.sunmi.payment.demo.helper;

import android.os.Bundle;

import com.android.architecture.helper.CacheHelper;
import com.android.architecture.utils.AppUtils;
import com.hjq.toast.ToastUtils;
import com.pos.connection.bridge.binder.ECRConstant;
import com.pos.connection.bridge.binder.ECRParameters;
import com.sunmi.payment.SunmiPaymentKernel;
import com.sunmi.payment.demo.constant.Constants;
import com.sunmi.payment.utils.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * File describe:
 * Author: SuQi
 * Create date: 2024/7/20
 * Modify date: 2024/7/20
 * Version: 1
 */
public class PaymentHelper {

    private static final String TAG = PaymentHelper.class.getSimpleName();
    private static volatile boolean isConnecting = false;
    private static final List<SunmiPaymentKernel.ConnectCallback> callbackList = new ArrayList<>();
    public static final String ECR_MODE = "ecrMode";

    public static void init() {
        // 自动连接Payment
        boolean lastConnectStatus = CacheHelper.INSTANCE.getFlag(Constants.Connect.PAYMENT_CONNECT_STATUS, false);
        if (lastConnectStatus) {
            String connectType = CacheHelper.INSTANCE.getString(Constants.Connect.PAYMENT_CONNECT_TYPE);
            Bundle config = new Bundle();

            switch (connectType) {
                case Constants.ConnectType.LOCAL:
                    config.putBoolean(ECR_MODE, false);
                    config.putInt("timeout", 10);
                    connectPayment(config, null);
                    break;

                case Constants.ConnectType.USB:
                    config.putBoolean(ECR_MODE, true);
                    config.putString(ECRParameters.MODE, ECRConstant.Mode.USB);
                    config.putInt("timeout", 10);
                    connectPayment(config, null);
                    break;

                case Constants.ConnectType.WIFI:
                    config.putBoolean(ECR_MODE, true);
                    config.putString(ECRParameters.MODE, ECRConstant.Mode.WIFI);
                    config.putString(ECRParameters.WIFI_ADDRESS, CacheHelper.INSTANCE.getString(Constants.Connect.PAYMENT_CONNECT_WIFI_IP));
                    config.putInt(ECRParameters.WIFI_PORT, Integer.parseInt(CacheHelper.INSTANCE.getString(Constants.Connect.PAYMENT_CONNECT_WIFI_PORT, "3888")));
                    config.putInt("timeout", 30);
                    connectPayment(config, null);
                    break;
            }
        }
    }

    public static synchronized void connectPayment(Bundle config, SunmiPaymentKernel.ConnectCallback callback) {
        if (callback != null) {
            callbackList.add(callback);
        }
        if (isConnecting) {
            return;
        }
        isConnecting = true;
        SunmiPaymentKernel.getInstance().initPayment(AppUtils.getApp(), config, new SunmiPaymentKernel.ConnectCallback() {
            @Override
            public void onConnect() {
                Logs.e(TAG, "onConnect");
                isConnecting = false;
                CacheHelper.INSTANCE.saveBool(Constants.Connect.PAYMENT_CONNECT_STATUS, true);
                callbackOnConnect();
            }

            @Override
            public void onDisconnect(int code, String msg) {
                Logs.e(TAG, "onDisconnect: code:" + code + "; msg:" + msg);
                ToastUtils.show("initPayment onDisconnect: code:" + code + "; msg:" + msg);
                isConnecting = false;
                CacheHelper.INSTANCE.saveBool(Constants.Connect.PAYMENT_CONNECT_STATUS, false);
                callbackOnDisConnect(code, msg);
            }
        });
    }

    public static void removeCallback(SunmiPaymentKernel.ConnectCallback callback) {
        if (callback != null) {
            callbackList.remove(callback);
        }
    }

    private static void callbackOnConnect() {
        for (int i = 0; i < callbackList.size(); i++) {
            SunmiPaymentKernel.ConnectCallback connectCallback = callbackList.get(i);
            if (connectCallback != null) {
                connectCallback.onConnect();
            }
        }
    }

    private static void callbackOnDisConnect(int code, String msg) {
        for (int i = 0; i < callbackList.size(); i++) {
            SunmiPaymentKernel.ConnectCallback connectCallback = callbackList.get(i);
            if (connectCallback != null) {
                connectCallback.onDisconnect(code, msg);
            }
        }
        callbackList.clear();
    }

}
