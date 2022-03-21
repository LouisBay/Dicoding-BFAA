package com.louis.mybroadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import java.lang.Exception

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        try {
            if (bundle != null) {
                /*
                Bundle dengan key "pdus" sudah merupakan standar yang digunakan oleh system
                 */
                val pdusObj = bundle.get("pdus") as Array<*>
                for (aPdusObj in pdusObj) {
                    val currentMessage = getIncomingMessage(aPdusObj as Any, bundle)
                    val senderNum = currentMessage.displayOriginatingAddress
                    val message = currentMessage.displayMessageBody
                    Log.d(TAG, "senderNum: $senderNum; message: $message")
                    Intent(context, SmsReceiverActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra(SmsReceiverActivity.EXTRA_SMS_NO, senderNum)
                        putExtra(SmsReceiverActivity.EXTRA_SMS_MESSAGE, message).also {
                            context.startActivity(it)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Exception smsReceiver $e")
        }
    }

    private fun getIncomingMessage(aObject: Any, bundle: Bundle): SmsMessage {
        val currentSMS: SmsMessage
        val format = bundle.getString("format")
        currentSMS = if (Build.VERSION.SDK_INT >= 23) {
            SmsMessage.createFromPdu(aObject as ByteArray, format)
        } else SmsMessage.createFromPdu(aObject as ByteArray)

        return currentSMS
    }

    companion object {
        private val TAG = SmsReceiver::class.java.simpleName
    }
}