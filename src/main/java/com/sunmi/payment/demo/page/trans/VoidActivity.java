package com.sunmi.payment.demo.page.trans;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hjq.bar.TitleBar;
import com.sunmi.payment.demo.R;
import com.sunmi.payment.demo.page.BaseTransActivity;
import com.sunmi.payment.demo.utils.OnTitleBarListenerWrapper;

import org.json.JSONObject;

public class VoidActivity extends BaseTransActivity {

    private EditText editAmount, editTransactionId, editOriginOrderId;
    private EditText editTip;
    private EditText editTax;
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void);
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
        editTransactionId = findViewById(R.id.edit_origin_transaction_id);
        editOriginOrderId = findViewById(R.id.edit_origin_order_id);
        tvResult = findViewById(R.id.tv_result);
        findViewById(R.id.mb_ok).setOnClickListener(view -> {
            tvResult.setText("");
            Void();
        });
    }

    private void Void() {
        String amount = editAmount.getText().toString();
        String tip = editTip.getText().toString();
        String tax = editTax.getText().toString();
        String originTransactionId = editTransactionId.getText().toString();
        String originOrderId = editOriginOrderId.getText().toString();
        long amountLong = 0;
        try {
            amountLong = Long.parseLong(amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(originTransactionId) && TextUtils.isEmpty(originOrderId)) {
            showToast("originTransactionId 或 originOrderId 不能都为空");
            return;
        }

        try {
            JSONObject obj = new JSONObject();
            obj.put("action", "void");
            obj.put("orderId", System.currentTimeMillis() + "");
            if (!TextUtils.isEmpty(originTransactionId)) {
                obj.put("originTransactionId", originTransactionId);
            }
            if (!TextUtils.isEmpty(originOrderId)) {
                obj.put("originOrderId", originOrderId);
            }
            if (amountLong == 0) {
                obj.put("voidType", "full");
            } else {
                obj.put("voidType", "partial");
                obj.put("amount", amountLong);
                try {
                    obj.put("tip", Long.parseLong(tip));
                    obj.put("tax", Long.parseLong(tax));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            startTrans(obj.toString(), tvResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
