package com.sunmi.payment.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.sunmi.payment.demo.page.BaseActivity;
import com.sunmi.payment.demo.page.manager.ConnectActivity;
import com.sunmi.payment.demo.page.trans.AuthActivity;
import com.sunmi.payment.demo.page.trans.CaptureActivity;
import com.sunmi.payment.demo.page.trans.RefundActivity;
import com.sunmi.payment.demo.page.trans.SaleActivity;
import com.sunmi.payment.demo.page.trans.SettlementActivity;
import com.sunmi.payment.demo.page.trans.TransactionInquiryActivity;
import com.sunmi.payment.demo.page.trans.TransactionListInquiryActivity;
import com.sunmi.payment.demo.page.trans.VoidActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.sale).setOnClickListener(view -> {
            openActivity(SaleActivity.class);
        });
        findViewById(R.id._void).setOnClickListener(view -> {
            openActivity(VoidActivity.class);
        });
        findViewById(R.id.refund).setOnClickListener(view -> {
            openActivity(RefundActivity.class);
        });
        findViewById(R.id.transQuery).setOnClickListener(view -> {
            openActivity(TransactionInquiryActivity.class);
        });
        findViewById(R.id.auth).setOnClickListener(view -> {
            openActivity(AuthActivity.class);
        });
        findViewById(R.id.capture).setOnClickListener(view -> {
            openActivity(CaptureActivity.class);
        });
        findViewById(R.id.transListQuery).setOnClickListener(view -> {
            openActivity(TransactionListInquiryActivity.class);
        });
        findViewById(R.id.settlement).setOnClickListener(view -> {
            openActivity(SettlementActivity.class);
        });
        findViewById(R.id.connectManage).setOnClickListener(view -> {
            openActivity(ConnectActivity.class);
        });
    }

}
