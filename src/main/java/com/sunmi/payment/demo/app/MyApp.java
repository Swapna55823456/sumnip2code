package com.sunmi.payment.demo.app;

import android.app.Application;

import com.hjq.toast.ToastUtils;
import com.sunmi.payment.demo.helper.PaymentHelper;

public class MyApp extends Application {

    public static MyApp myApp;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        ToastUtils.init(this);
        PaymentHelper.init();
    }

}
