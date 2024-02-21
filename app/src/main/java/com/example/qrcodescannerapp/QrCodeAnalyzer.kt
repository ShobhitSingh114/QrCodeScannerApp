package com.example.qrcodescannerapp

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.lang.Exception
import java.nio.ByteBuffer

// This consistently analyzes the camera image and scans for barcode
class QrCodeAnalyzer(
    // onQrCodeScanned =  will be called when we scan a code
    private val onQrCodeScanned: (String) -> Unit
): ImageAnalysis.Analyzer {

    // make a list  = in it specify which kind of byte formats we support
    // we just define what kind of formats we wanna support in terms of bytes
    private val supportedImageFormats = listOf(
        // with these ImageFormats we are able to scan QR codes
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888
    )
    override fun analyze(image: ImageProxy) {
    // ImageProxy = contains Info. about a specific frame of our Camera

        // check weather 'scanned image' format {i.e. ImageProxy} have our 'supportedImageFormat' or not
        if (image.format in supportedImageFormats) {

            // Get raw data of that imageProxy
            // it detects some kind of plane and it tries to find these barcodes on these planes
            val bytes = image.planes.first().buffer.toByteArray()


            // PlanarYUVLuminanceSource = process image data for barcode recognition
            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )

            // bitmap file = contains info about qrCode
            val binaryBmp = BinaryBitmap(HybridBinarizer(source))

            // Now we have barCode info but we still need to decode that info. which QrCode have
            try {
                // MultiFormatReader = decoding barcodes from various formats.
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                BarcodeFormat.QR_CODE
                            )
                        )
                    )
                }.decode(binaryBmp)
                // 'result' has decoded Qrcode info
                onQrCodeScanned(result.text)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                image.close()
            }

        }

    }

    // In summary, this extension function converts the contents of a ByteBuffer into a ByteArray,
    // making sure to start from the beginning of the buffer and include all of its elements.
    private fun ByteBuffer.toByteArray(): ByteArray {
//    rewind(): This method is used to move the position of the buffer to the beginning,
//    ensuring that you start reading from the start of the buffer.
//
//    remaining(): This method returns the number of elements remaining in the buffer.
//    In this case, it represents the total size of the buffer.
//
//    ByteArray(remaining()): This creates a new ByteArray with the size equal to
//    the remaining elements in the buffer.
//
//    also { get(it) }: The also function is used for side effects. Here,
//    it's used to execute the get(it) function, which reads the content of the
//    buffer into the newly created ByteArray.

        rewind()
        return ByteArray(remaining()).also {
            get(it)
        }
    }
}