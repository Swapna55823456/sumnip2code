package com.sunmi.payment.demo.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.android.architecture.ui.page.BaseDialog;
import com.sunmi.payment.demo.R;

import java.util.Locale;

public final class LoadingDialog {

    public static final class Builder extends BaseDialog.Builder<Builder> {

        private final TextView tvClose;
        private final TextView mMessageView;
        private String msgText;
        private CloseListener closeListener;

        public Builder(Context context) {
            super(context);
            setContentView(R.layout.dialog_loading);
            setAnimStyle(BaseDialog.ANIM_TOAST);
            setBackgroundDimEnabled(true);
            setCancelable(false);

            tvClose = findViewById(R.id.tvClose);
            mMessageView = findViewById(R.id.tv_wait_message);

            tvClose.setOnClickListener(view -> {
                if (closeListener != null) {
                    closeListener.onClose();
                    dismiss();
                }
            });
        }

        public Builder setMessage(@StringRes int id) {
            return setMessage(getString(id));
        }

        public Builder setMessage(CharSequence text) {
            msgText = text.toString();
            mMessageView.setText(text);
            mMessageView.setVisibility(text == null ? View.GONE : View.VISIBLE);
            return this;
        }

        public Builder setCloseListener(CloseListener listener) {
            closeListener = listener;
            return this;
        }

        public void updateProgress(int progress) {
            mMessageView.setText(String.format(Locale.getDefault(), "%s(%ds)", msgText, progress));
        }


    }

    public interface CloseListener {
        void onClose();
    }

}