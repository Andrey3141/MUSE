package com.example.muse.update

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.TextView
import com.example.muse.R

object UpdateDialog {

    fun show(context: Context, info: UpdateInfo) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_update, null)
        view.findViewById<TextView>(R.id.dialogCurrentVersion).text =
            "Текущая: ${com.example.muse.BuildConfig.VERSION_NAME}"
        view.findViewById<TextView>(R.id.dialogNewVersion).text =
            "Новая: ${info.versionName}"
        view.findViewById<TextView>(R.id.dialogReleaseNotes).text =
            info.releaseNotes.takeIf { it.isNotBlank() } ?: "Описание отсутствует"

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton("Обновить") { _, _ ->
                val target = info.apkUrl ?: info.releasePageUrl
                if (target.isNotBlank()) {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(target)))
                    } catch (_: Exception) { }
                }
            }
            .setNegativeButton("Позже", null)
            .create()

        dialog.show()
    }
}