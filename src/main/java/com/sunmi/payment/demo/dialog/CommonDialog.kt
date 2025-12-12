package com.sunmi.payment.demo.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.android.architecture.extension.click
import com.android.architecture.ui.page.BaseDialog
import com.sunmi.payment.demo.R

class CommonDialog {

    @Suppress("UNCHECKED_CAST")
    open class Builder<B : Builder<B>>(context: Context) : BaseDialog.Builder<B>(context) {

        private val llContainer: LinearLayout by lazy { findViewById(R.id.ll_ui_container) }
        private val tvTitle: TextView by lazy { findViewById(R.id.tv_ui_title) }
        private val tvConfirm: TextView by lazy { findViewById(R.id.tv_ui_confirm) }
        private val tvCancel: TextView by lazy { findViewById(R.id.tv_ui_cancel) }

        private var confirmListener: (() -> Unit)? = null
        private var cancelListener: (() -> Unit)? = null
        private var autoDismiss = true

        init {
            setContentView(R.layout.dialog_common)
            tvConfirm.click {
                autoDismiss()
                confirmListener?.invoke()
            }
            tvCancel.click {
                autoDismiss()
                cancelListener?.invoke()
            }
        }

        fun setCustomView(@LayoutRes id: Int): B {
            return setCustomView(LayoutInflater.from(context).inflate(id, llContainer, false))
        }

        fun setCustomView(view: View): B {
            llContainer.addView(view, 1)
            return this as B
        }

        fun title(text: CharSequence): B {
            tvTitle.text = text
            return this as B
        }

        fun cancelText(text: String): B {
            tvCancel.text = text
            return this as B
        }

        fun confirmText(text: CharSequence): B {
            tvConfirm.text = text
            return this as B
        }

        fun autoDismiss(dismiss: Boolean) {
            autoDismiss = dismiss
        }

        protected fun autoDismiss() {
            if (autoDismiss) {
                dismiss()
            }
        }

        fun onConfirm(listener: () -> Unit): B {
            confirmListener = listener
            return this as B
        }

        fun onCancel(listener: () -> Unit): B {
            cancelListener = listener
            return this as B
        }

    }

}