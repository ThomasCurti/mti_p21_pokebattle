package com.epita.pokebattle.methods

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import java.io.InputStream
import java.net.URL


public class DownloadImageTask(bmImage: ImageView) :
    AsyncTask<String?, Void?, Bitmap?>() {

    var bmImage: ImageView

    override fun doInBackground(vararg params: String?): Bitmap? {
        val urldisplay = params[0]
        var mIcon11: Bitmap? = null
        try {
            val input: InputStream = URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
        }
        return mIcon11
    }

    override fun onPostExecute(result: Bitmap?) {
        bmImage.setImageBitmap(result)
    }

    init {
        this.bmImage = bmImage
    }

}