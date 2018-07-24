package com.google.codelabs.mdc.kotlin.shrine.network

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache

import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.android.volley.toolbox.Volley
import com.google.codelabs.mdc.kotlin.shrine.application.ShrineApplication

/**
 * Class that handles image requests using Volley.
 */
object ImageRequester {
    private val requestQueue: RequestQueue
    private val imageLoader: ImageLoader
    private val maxByteSize: Int

    init {
        val context = ShrineApplication.instance
        this.requestQueue = Volley.newRequestQueue(context)
        this.requestQueue.start()
        this.maxByteSize = calculateMaxByteSize(context)
        this.imageLoader = ImageLoader(
                requestQueue,
                object : ImageLoader.ImageCache {
                    private val lruCache = object : LruCache<String, Bitmap>(maxByteSize) {
                        override fun sizeOf(url: String, bitmap: Bitmap): Int {
                            return bitmap.byteCount
                        }
                    }

                    @Synchronized
                    override fun getBitmap(url: String): Bitmap? {
                        return lruCache.get(url)
                    }

                    @Synchronized
                    override fun putBitmap(url: String, bitmap: Bitmap) {
                        lruCache.put(url, bitmap)
                    }
                })
    }

    /**
     * Sets the image on the given [NetworkImageView] to the image at the given URL
     *
     * @param networkImageView [NetworkImageView] to set image on
     * @param url              URL of the image
     */
    fun setImageFromUrl(networkImageView: NetworkImageView, url: String) {
        networkImageView.setImageUrl(url, imageLoader)
    }

    private fun calculateMaxByteSize(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenBytes = displayMetrics.widthPixels * displayMetrics.heightPixels * 4
        return screenBytes * 3
    }
}