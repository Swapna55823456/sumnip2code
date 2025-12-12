package com.sunmi.payment.demo.page.trans;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hjq.bar.TitleBar;
import com.sunmi.payment.demo.R;
import com.sunmi.payment.demo.page.BaseTransActivity;
import com.sunmi.payment.demo.utils.OnTitleBarListenerWrapper;

import org.json.JSONObject;

public class CaptureActivity extends BaseTransActivity {

    private EditText editAmount;
    private EditText editTip;
    private EditText editTax;
    private EditText editOriginTransactionId;
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setOnTitleBarListener(new OnTitleBarListenerWrapper() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }
        });
        editAmount = findViewById(R.id.edit_amount);
        editTip = findViewById(R.id.edit_tip);
        editTax = findViewById(R.id.edit_tax);
        editOriginTransactionId = findViewById(R.id.edit_transaction_id);
        tvResult = findViewById(R.id.tv_result);
        findViewById(R.id.mb_ok).setOnClickListener(view -> {
            tvResult.setText("");
            sale();
        });
    }

    private void sale() {
        String amount = editAmount.getText().toString();
        String tip = editTip.getText().toString();
        String tax = editTax.getText().toString();
        long amountLong;
        try {
            amountLong = Long.parseLong(amount);
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Please input correct amount");
            return;
        }
        if (amountLong <= 0) {
            showToast("Please input correct amount");
            return;
        }
        String originTransactionId = editOriginTransactionId.getText().toString();
        try {
            JSONObject obj = new JSONObject();
            obj.put("action", "capture");

            obj.put("orderId", System.currentTimeMillis() + "");
            obj.put("originTransactionId", originTransactionId);
            obj.put("amount", amountLong);
            try {
                obj.put("tip", Long.parseLong(tip));
                obj.put("tax", Long.parseLong(tax));
            } catch (Exception e) {
                e.printStackTrace();
            }
            startTrans(obj.toString(), tvResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
