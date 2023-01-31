package com.niyaj.popos.features.reminder.domain.repository

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class AttendanceReceiver: BroadcastReceiver() {
    override fun onReceive(context : Context?, intent : Intent?) {
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return

        Timber.d("onReceive message $message")

        val alertDialog = AlertDialog.Builder(context)

        alertDialog.setTitle(message).setMessage("Show Mark Attendance").show()
    }
}