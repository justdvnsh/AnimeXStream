package net.xblacky.animexstream.utils.service

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.exoplayer2.offline.Download
import net.xblacky.animexstream.utils.Utils
import java.lang.Exception
import java.util.*


object DownloadingService {

    private var downloadId: Long = 0L

    // using broadcast method
    val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Fetching the download id received with the broadcast
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadId == id) {
//               Toast.makeText(require)
            }
        }
    }

    fun downloadAnime(context: Context, url: String) {
        var fileName = url.substring(url.lastIndexOf("/") + 1)
        fileName = fileName.substring(0, 1).toUpperCase(Locale.getDefault()) + fileName.substring(1)
//        val file = Utils.createVideoFile(fileName, context)
        Log.d("MYSELF -> ", url)
        Log.d("MYSELF -> ", fileName)
        try {
            val request = DownloadManager.Request(Uri.parse(url))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setTitle(fileName)
                    .setDescription("Downloading")
                    .setAllowedOverRoaming(true)
                    .setAllowedOverMetered(true)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadId = downloadManager.enqueue(request)
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }

//        var finishDownload = false
//        var progress: Int = 0
//
//        while(!finishDownload) {
//            var cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
//            if (cursor.moveToFirst()) {
//                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
//                when(status) {
//                    DownloadManager.STATUS_FAILED -> {
//                        finishDownload = true
//                        Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show()
//                        break
//                    }
//
//                    DownloadManager.STATUS_PAUSED -> {
//                        Toast.makeText(context, "Downloading Paused", Toast.LENGTH_SHORT).show()
//                        break
//                    }
//
//                    DownloadManager.STATUS_PENDING -> {}
//
//                    DownloadManager.STATUS_RUNNING -> {
//                        var total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
//                        if (total >= 0) {
//                            var downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
//                            progress = ((downloaded * 100L) / total).toInt()
//                        }
//                        break
//                    }
//
//                    DownloadManager.STATUS_SUCCESSFUL -> {
//                        progress = 100
//                        finishDownload = true
//                        Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
//                        break;
//                    }
//                }
//            }
//        }
    }
}