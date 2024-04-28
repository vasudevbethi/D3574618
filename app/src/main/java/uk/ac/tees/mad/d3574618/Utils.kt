package uk.ac.tees.mad.d3574618

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun Context.showToast(text: String) {
    Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
}

fun formattedDate(date: Date?): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return if (date == null) return "" else sdf.format(date).toString()
}

fun Context.sendMail(to: String, subject: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        val data = Uri.parse("mailto:${to}?subject=$subject&body=")
        intent.setData(data)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        this.showToast("No email app")
    } catch (t: Throwable) {
        Log.d("Message ERROR", t.message.toString())
    }
}

fun Context.dial(phone: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
        startActivity(intent)
    } catch (t: Throwable) {
        Log.d("Dial Error ERROR", t.message.toString())
    }
}

fun handleImageSelection(uri: Uri, context: Context): ByteArray {
    val bitmap = if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images
            .Media
            .getBitmap(context.contentResolver, uri)

    } else {
        val source = ImageDecoder
            .createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
    return convertBitmapToByteArray(bitmap)
//    _uiState.update {
//        it.copy(image = imageByteArray)
//    }
}

fun handleImageCapture(bitmap: Bitmap): ByteArray {
    return convertBitmapToByteArray(bitmap)
}

fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return outputStream.toByteArray()
}