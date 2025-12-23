package com.sunmi.payment.demo.page.trans;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sunmi.payment.demo.R;
import com.sunmi.payment.demo.page.BaseTransActivity;

import org.json.JSONObject;

public class VoidActivity extends BaseTransActivity {

    private static final String TAG = "SUNMI_VOID";

    private EditText editAmount, editTransactionId, editOriginOrderId;
    private TextView tvResult;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void);

        editAmount = findViewById(R.id.edit_amount);
        editTransactionId = findViewById(R.id.edit_origin_transaction_id);
        editOriginOrderId = findViewById(R.id.edit_origin_order_id);
        tvResult = findViewById(R.id.tv_result);

        tvResult.setText("");

        // -------------------------------
        // Receive values from Flutter
        // -------------------------------
        Intent intent = getIntent();
        if (intent != null) {
            editAmount.setText(intent.getStringExtra("amount"));
            editTransactionId.setText(intent.getStringExtra("originTransactionId"));
            editOriginOrderId.setText(intent.getStringExtra("originOrderId"));
        }

        // Auto start VOID (for visibility)
        handler.postDelayed(this::startVoid, 300);
    }

    private void startVoid() {

        String originTxnId = editTransactionId.getText().toString().trim();
        String originOrderId = editOriginOrderId.getText().toString().trim();

        if (TextUtils.isEmpty(originTxnId) || TextUtils.isEmpty(originOrderId)) {
            sendError("Missing original transaction/order ID");
            return;
        }

        try {
            JSONObject request = new JSONObject();
            request.put("action", "void");

            // ✅ MUST BE A NEW UNIQUE VOID ID
            request.put("orderId", "VOID_" + System.currentTimeMillis());

            // ✅ MUST BE ORIGINAL SALE REFERENCES
            request.put("originOrderId", originOrderId);
            request.put("originTransactionId", originTxnId);

            request.put("voidType", "full");

            Log.e(TAG, "VOID REQUEST → " + request.toString());

            startTrans(request.toString(), tvResult);
            handler.postDelayed(this::checkAndSendResult, 500);

        } catch (Exception e) {
            sendError(e.getMessage());
        }
    }




    private void checkAndSendResult() {

        String resultJson = tvResult.getText().toString().trim();

        if (TextUtils.isEmpty(resultJson) || !resultJson.contains("{")) {
            handler.postDelayed(this::checkAndSendResult, 500);
            return;
        }

        try {
            JSONObject sunmi = new JSONObject(resultJson);

            JSONObject response = new JSONObject();
            response.put(
                    "status",
                    "00".equals(sunmi.optString("resultCode")) ? "SUCCESS" : "FAILED"
            );
            response.put("message", sunmi.optString("resultMsg"));
            response.put("amount", sunmi.optString("processedAmount"));
            response.put("fullResponse", sunmi.toString());

            Log.e(TAG, "VOID RESPONSE → " + response.toString());

            Intent intent = new Intent();
            intent.putExtra("paymentResult", response.toString());
            setResult(RESULT_OK, intent);
            finish();

        } catch (Exception e) {
            sendError(e.getMessage());
        }
    }

    private void sendError(String msg) {
        try {
            JSONObject err = new JSONObject();
            err.put("status", "FAILED");
            err.put("message", msg);

            Intent intent = new Intent();
            intent.putExtra("paymentResult", err.toString());
            setResult(RESULT_OK, intent);

            Log.e(TAG, "VOID ERROR → " + msg);

        } catch (Exception ignored) {}

        finish();
    }
}
