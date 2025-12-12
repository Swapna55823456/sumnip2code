package com.sunmi.payment.demo.page.trans;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hjq.bar.TitleBar;
import com.sunmi.payment.demo.R;
import com.sunmi.payment.demo.page.BaseTransActivity;
import com.sunmi.payment.demo.utils.OnTitleBarListenerWrapper;

import org.json.JSONObject;

public class SettlementActivity extends BaseTransActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);
        tvResult = findViewById(R.id.tv_result);
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setOnTitleBarListener(new OnTitleBarListenerWrapper() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }
        });
        findViewById(R.id.mb_ok).setOnClickListener(view -> {
            tvResult.setText("");
            sale();
        });
    }

    private void sale() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("action", "settlement");

            obj.put("orderId", System.currentTimeMillis() + "");
            startTrans(obj.toString(), tvResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
