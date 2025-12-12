package com.sunmi.payment.demo.dialog

import android.content.Context
import com.android.architecture.extension.showKeyboard
import com.android.architecture.ui.page.BaseDialog
import com.android.architecture.ui.widget.view.RegexEditText
import com.sunmi.payment.demo.R

class InputIpPortDialog {

    class Builder(context: Context) : CommonDialog.Builder<Builder>(context),
        BaseDialog.OnShowListener {
        private val mInputIpView: RegexEditText by lazy { findViewById(R.id.tv_input_ip) }
        private val mInputPortView: RegexEditText by lazy { findViewById(R.id.tv_input_port) }

        private var confirmListener: ((ip: String, port: String) -> Unit)? = null

        init {
            setCustomView(R.layout.dialog_input_ip_port)
            addOnShowListener(this)

            onConfirm {
                val ip = mInputIpView.text.toString()
                val port = mInputPortView.text.toString()
                confirmListener?.invoke(ip, port)
            }
        }

        fun ip(text: String): Builder {
            mInputIpView.setText(text)
            val editable = mInputIpView.text ?: return this
            val index = editable.length
            if (index <= 0) {
                return this
            }
            mInputIpView.requestFocus()
            mInputIpView.setSelection(index)
            return this
        }

        fun port(text: String): Builder {
            mInputPortView.setText(text)
            return this
        }

        fun onConfirm2(listener: ((ip: String, port: String) -> Unit)): Builder {
            confirmListener = listener
            return this
        }

        override fun onShow(dialog: BaseDialog?) {
            postDelayed(500) {
                mInputIpView.showKeyboard()
            }
        }

    }

}