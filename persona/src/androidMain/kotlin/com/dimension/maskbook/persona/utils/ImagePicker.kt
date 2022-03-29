/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.persona.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.dhaval2404.imagepicker.ImagePickerActivity
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.github.dhaval2404.imagepicker.listener.DismissListener
import java.io.File

open class ImagePicker {
    companion object {

        private const val EXTRA_IMAGE_PROVIDER = "extra.image_provider"
        private const val EXTRA_IMAGE_MAX_SIZE = "extra.image_max_size"
        private const val EXTRA_CROP = "extra.crop"
        private const val EXTRA_CROP_X = "extra.crop_x"
        private const val EXTRA_CROP_Y = "extra.crop_y"
        private const val EXTRA_MAX_WIDTH = "extra.max_width"
        private const val EXTRA_MAX_HEIGHT = "extra.max_height"
        private const val EXTRA_SAVE_DIRECTORY = "extra.save_directory"
        private const val EXTRA_ERROR = "extra.error"
        private const val EXTRA_MIME_TYPES = "extra.mime_types"

        /**
         * Use this to use ImagePicker in Activity Class
         *
         * @param context Context Instance
         */
        @JvmStatic
        fun with(context: Context): Builder {
            return Builder(context)
        }

        /**
         * Get error message from intent
         */
        @JvmStatic
        fun getError(data: Intent?): String {
            val error = data?.getStringExtra(EXTRA_ERROR)
            if (error != null) {
                return error
            } else {
                return "Unknown Error!"
            }
        }
    }

    class Builder(private val context: Context) {

        // Image Provider
        private var imageProvider = ImageProvider.GALLERY

        // Mime types restrictions for gallery. by default all mime types are valid
        private var mimeTypes: Array<String> = emptyArray()

        /*
         * Crop Parameters
         */
        private var cropX: Float = 0f
        private var cropY: Float = 0f
        private var crop: Boolean = false

        /*
         * Resize Parameters
         */
        private var maxWidth: Int = 0
        private var maxHeight: Int = 0

        /*
         * Max File Size
         */
        private var maxSize: Long = 0

        private var imageProviderInterceptor: ((ImageProvider) -> Unit)? = null

        /**
         * Dialog dismiss event listener
         */
        private var dismissListener: DismissListener? = null

        /**
         * File Directory
         *
         * Camera, Crop, Compress Image Will be store in this directory.
         *
         * If null, Image will be stored in "{fileDir}/Images"
         */
        private var saveDir: String? = null

        /**
         * Specify Image Provider (Camera, Gallery or Both)
         */
        fun provider(imageProvider: ImageProvider): Builder {
            this.imageProvider = imageProvider
            return this
        }

        /**
         * Only Capture image using Camera.
         */
        // @Deprecated("Please use provider(ImageProvider.CAMERA) instead")
        fun cameraOnly(): Builder {
            this.imageProvider = ImageProvider.CAMERA
            return this
        }

        /**
         * Only Pick image from gallery.
         */
        // @Deprecated("Please use provider(ImageProvider.GALLERY) instead")
        fun galleryOnly(): Builder {
            this.imageProvider = ImageProvider.GALLERY
            return this
        }

        /**
         * Restrict mime types during gallery fetching, for instance if you do not want GIF images,
         * you can use arrayOf("image/png","image/jpeg","image/jpg")
         * by default array is empty, which indicates no additional restrictions, just images
         * @param mimeTypes
         */
        fun galleryMimeTypes(mimeTypes: Array<String>): Builder {
            this.mimeTypes = mimeTypes
            return this
        }

        /**
         * Set an aspect ratio for crop bounds.
         * User won't see the menu with other ratios options.
         *
         * @param x aspect ratio X
         * @param y aspect ratio Y
         */
        fun crop(x: Float, y: Float): Builder {
            cropX = x
            cropY = y
            return crop()
        }

        /**
         * Crop an image and let user set the aspect ratio.
         */
        fun crop(): Builder {
            this.crop = true
            return this
        }

        /**
         * Crop Square Image, Useful for Profile Image.
         *
         */
        fun cropSquare(): Builder {
            return crop(1f, 1f)
        }

        /**
         * Max Width and Height of final image
         */
        fun maxResultSize(width: Int, height: Int): Builder {
            this.maxWidth = width
            this.maxHeight = height
            return this
        }

        /**
         * Compress Image so that max image size can be below specified size
         *
         * @param maxSize Size in KB
         */
        fun compress(maxSize: Int): Builder {
            this.maxSize = maxSize * 1024L
            return this
        }

        /**
         * Provide Directory to store Captured/Modified images
         *
         * @param path Folder Directory
         */
        fun saveDir(path: String): Builder {
            this.saveDir = path
            return this
        }

        /**
         * Provide Directory to store Captured/Modified images
         *
         * @param file Folder Directory
         */
        fun saveDir(file: File): Builder {
            this.saveDir = file.absolutePath
            return this
        }

        /**
         * Intercept Selected ImageProvider,  Useful for Analytics
         *
         * @param interceptor ImageProvider Interceptor
         */
        fun setImageProviderInterceptor(interceptor: (ImageProvider) -> Unit): Builder {
            this.imageProviderInterceptor = interceptor
            return this
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         */
        fun setDismissListener(listener: DismissListener): Builder {
            this.dismissListener = listener
            return this
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         */
        fun setDismissListener(listener: (() -> Unit)): Builder {
            this.dismissListener = object : DismissListener {
                override fun onDismiss() {
                    listener.invoke()
                }
            }
            return this
        }

        /**
         * Prefer {@see createIntentFromDialog} over this method, as
         *
         *  Only used with ImageProvider.GALLERY or ImageProvider.CAMERA
         */
        fun createIntent(): Intent {
            val intent = Intent(context, ImagePickerActivity::class.java)
            intent.putExtras(getBundle())
            return intent
        }

        /**
         * Get Bundle for ImagePickerActivity
         */
        private fun getBundle(): Bundle {
            return Bundle().apply {
                putSerializable(EXTRA_IMAGE_PROVIDER, imageProvider)
                putStringArray(EXTRA_MIME_TYPES, mimeTypes)

                putBoolean(EXTRA_CROP, crop)
                putFloat(EXTRA_CROP_X, cropX)
                putFloat(EXTRA_CROP_Y, cropY)

                putInt(EXTRA_MAX_WIDTH, maxWidth)
                putInt(EXTRA_MAX_HEIGHT, maxHeight)

                putLong(EXTRA_IMAGE_MAX_SIZE, maxSize)

                putString(EXTRA_SAVE_DIRECTORY, saveDir)
            }
        }
    }
}
