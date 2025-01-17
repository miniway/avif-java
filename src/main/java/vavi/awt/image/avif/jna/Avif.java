// Copyright 2022 Google LLC. All rights reserved.
// SPDX-License-Identifier: BSD-2-Clause

package vavi.awt.image.avif.jna;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import vavi.awt.image.jna.avif.AvifLibrary;
import vavi.awt.image.jna.avif.avifDecoder;
import vavi.awt.image.jna.avif.avifEncoder;
import vavi.awt.image.jna.avif.avifImage;
import vavi.awt.image.jna.avif.avifRGBImage;
import vavi.awt.image.jna.avif.avifROData;
import vavi.awt.image.jna.avif.avifRWData;
import com.ochafik.lang.jnaerator.runtime.NativeSize;

/**
 * An AVIF Decoder.
 *
 * @see "AVIF Specification: https://aomediacodec.github.io/av1-avif/."
 */
public class Avif {

    private static boolean available = false;
    private static Logger LOGGER = Logger.getLogger(Avif.class.getName());
    private static final Avif avif = new Avif();

    // This is a utility class and cannot be instantiated.
    private Avif() {
        try {
            String version = AvifLibrary.INSTANCE.avifVersion();
            if (!version.startsWith("1.0.3")) {
                LOGGER.log(Level.SEVERE, "wrong version: " + version);
            }
            available = true;
        }
        catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
        }
    }

    public static boolean available() {
        return available;
    }

    public static Avif getInstance() {
        return avif;
    }

    /**
     * Returns true if the bytes in the buffer seem like an AVIF image.
     *
     * @param encoded The encoded image. buffer.position() must be 0.
     * @return true if the bytes seem like an AVIF image, false otherwise.
     */
    public static boolean isAvifImage(ByteBuffer encoded, int length) {
        avifROData data = new avifROData();
        data.data = Native.getDirectBufferPointer(encoded);
        data.size.setValue(length);
        return AvifLibrary.INSTANCE.avifPeekCompatibleFileType(data) == AvifLibrary.AVIF_TRUE;
    }

    /**
     * Parses the AVIF header and populates the Info.
     *
     * @param encoded The encoded AVIF image. encoded.position() must be 0.
     * @param length  Length of the encoded buffer.
     * @return true on success and false on failure.
     */
    public BufferedImage getCompatibleImage(ByteBuffer encoded, int length) {
        Pointer buffer = Native.getDirectBufferPointer(encoded);
        avifDecoder decoder = createDecoderAndParse(buffer, length, Runtime.getRuntime().availableProcessors());
        BufferedImage image = new BufferedImage(decoder.image.width, decoder.image.height, BufferedImage.TYPE_4BYTE_ABGR);
LOGGER.log(Level.FINER, "image depth: " + decoder.image.depth);
        return image;
    }

    private avifDecoder createDecoderAndParse(Pointer buffer, int length, int threads) {
        avifDecoder decoder = AvifLibrary.INSTANCE.avifDecoderCreate();
        if (decoder == null) {
            throw new IllegalStateException("Failed to create AVIF Decoder.");
        }
        decoder.maxThreads = threads;
        decoder.ignoreXMP = AvifLibrary.AVIF_TRUE;
        decoder.ignoreExif = AvifLibrary.AVIF_TRUE;

        // Turn off 'clap' (clean aperture) property validation. The JNI wrapper
        // ignores the 'clap' property.
        decoder.strictFlags &= ~AvifLibrary.avifStrictFlag.AVIF_STRICT_CLAP_VALID;
        // Allow 'pixi' (pixel information) property to be missing. Older versions of
        // libheif did not add the 'pixi' item property to AV1 image items (See
        // crbug.com/1198455).
        decoder.strictFlags &= ~AvifLibrary.avifStrictFlag.AVIF_STRICT_PIXI_REQUIRED;

        int res = AvifLibrary.INSTANCE.avifDecoderSetIOMemory(decoder, buffer, new NativeSize(length));
        if (res != AvifLibrary.avifResult.AVIF_RESULT_OK) {
            throw new IllegalStateException("Failed to set AVIF IO to a memory reader.");
        }
        res = AvifLibrary.INSTANCE.avifDecoderParse(decoder);
        if (res != AvifLibrary.avifResult.AVIF_RESULT_OK) {
            throw new IllegalStateException(String.format("Failed to parse AVIF image: %s.", AvifLibrary.INSTANCE.avifResultToString(res)));
        }
        return decoder;
    }

    /**
     * Decodes the AVIF image into the bitmap.
     *
     * @param encoded The encoded AVIF image. encoded.position() must be 0.
     * @param length  Length of the encoded buffer.
     * @param bitmap  The decoded pixels will be copied into the bitmap.
     * @return the decoded image.
     */
    public BufferedImage decode(ByteBuffer encoded, int length, BufferedImage bitmap) {
        Pointer buffer = Native.getDirectBufferPointer(encoded);
        avifDecoder decoder = createDecoderAndParse(buffer, length, Runtime.getRuntime().availableProcessors());
        int res = AvifLibrary.INSTANCE.avifDecoderNextImage(decoder);
        if (res != AvifLibrary.avifResult.AVIF_RESULT_OK) {
            throw new IllegalStateException(String.format("Failed to decode AVIF image. Status: %d", res));
        }
        // Ensure that the bitmap is large enough to store the decoded image.
        if (bitmap.getWidth() < decoder.image.width ||
                bitmap.getHeight() < decoder.image.height) {
            throw new IllegalStateException(String.format(
                    "Bitmap is not large enough to fit the image. Bitmap %dx%d Image %dx%d.",
                    bitmap.getWidth(), bitmap.getHeight(), decoder.image.width,
                    decoder.image.height));
        }
        // Ensure that the bitmap format is RGBA_8888, RGB_565 or RGBA_F16.
        if (bitmap.getType() != BufferedImage.TYPE_4BYTE_ABGR &&
                bitmap.getType() != BufferedImage.TYPE_USHORT_565_RGB) {
            throw new IllegalStateException(String.format("Bitmap format (%d) is not supported.", bitmap.getType()));
        }
        avifRGBImage rgb_image = new avifRGBImage();
        AvifLibrary.INSTANCE.avifRGBImageSetDefaults(rgb_image, decoder.image);
        int bytes;
        if (bitmap.getType() == BufferedImage.TYPE_USHORT_565_RGB) {
            rgb_image.format = AvifLibrary.avifRGBFormat.AVIF_RGB_FORMAT_RGB;
            rgb_image.depth = 8;
            bytes = 2;
        } else {
            rgb_image.depth = 8;
            bytes = 4;
        }
        ByteBuffer nativeBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * bytes);
        rgb_image.pixels = Native.getDirectBufferPointer(nativeBuffer);
        rgb_image.rowBytes = bitmap.getWidth() * bytes;
        res = AvifLibrary.INSTANCE.avifImageYUVToRGB(decoder.image, rgb_image);
        if (res != AvifLibrary.avifResult.AVIF_RESULT_OK) {
            throw new IllegalStateException(String.format("Failed to convert YUV Pixels to RGB. Status: %d", res));
        }
        // cause nativeBuffer doesn't have array()
        ByteBuffer localBuffer = ByteBuffer.allocate(nativeBuffer.capacity());
        localBuffer.put(nativeBuffer);
        bitmap.getRaster().setDataElements(0, 0, bitmap.getWidth(), bitmap.getHeight(), localBuffer.array());
        AvifLibrary.INSTANCE.avifDecoderDestroy(decoder);
        return bitmap;
    }

    /** Encodes the java image into the AVIF image. */
    public ByteBuffer encode(BufferedImage bitmap, int quality) {
        LOGGER.log(Level.FINE, "depth: " + bitmap.getColorModel().getPixelSize() / bitmap.getColorModel().getNumComponents());
        avifImage image = AvifLibrary.INSTANCE.avifImageCreate(bitmap.getWidth(), bitmap.getHeight(), bitmap.getColorModel().getPixelSize() / bitmap.getColorModel().getNumComponents(), AvifLibrary.avifPixelFormat.AVIF_PIXEL_FORMAT_YUV444); // these values dictate what goes into the final AVIF
        if (image == null) {
            throw new OutOfMemoryError("avifImageCreate");
        }
        // Configure image here: (see avif/avif.h)
        // * colorPrimaries
        // * transferCharacteristics
        // * matrixCoefficients
        // * avifImageSetProfileICC()
        // * avifImageSetMetadataExif()
        // * avifImageSetMetadataXMP()
        // * yuvRange
        // * alphaPremultiplied
        // * transforms (transformFlags, pasp, clap, irot, imir)

        // If you have RGB(A) data you want to encode, use this path
        LOGGER.log(Level.FINE, "Encoding from converted RGBA");

        if (bitmap.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
            throw new IllegalStateException(String.format("Bitmap format (%d) is not supported.", bitmap.getType()));
        }
        avifRGBImage rgb = new avifRGBImage();
        AvifLibrary.INSTANCE.avifRGBImageSetDefaults(rgb, image);
        rgb.format = AvifLibrary.avifRGBFormat.AVIF_RGB_FORMAT_ABGR;
        // Override RGB(A)->YUV(A) defaults here:
        //   depth, format, chromaDownsampling, avoidLibYUV, ignoreAlpha, alphaPremultiplied, etc.

        AvifLibrary.INSTANCE.avifRGBImageAllocatePixels(rgb);
        ByteBuffer nativeBuffer = rgb.pixels.getByteBuffer(0, (long) rgb.rowBytes * rgb.height);
//        ByteBuffer nativeBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * bytes);
//        rgb.pixels = Native.getDirectBufferPointer(nativeBuffer);
//        rgb.rowBytes = bitmap.getWidth() * bytes;
        //LOGGER.log(Level.FINE, StringUtil.paramString(rgb));

        nativeBuffer.put(((DataBufferByte) bitmap.getRaster().getDataBuffer()).getData());

        int convertResult = AvifLibrary.INSTANCE.avifImageRGBToYUV(image, rgb);
        if (convertResult != AvifLibrary.avifResult.AVIF_RESULT_OK) {
            throw new IllegalStateException(String.format("Failed to convert to YUV(A): %s", AvifLibrary.INSTANCE.avifResultToString(convertResult)));
        }

        avifEncoder encoder = AvifLibrary.INSTANCE.avifEncoderCreate();
        if (encoder == null) {
            throw new OutOfMemoryError("avifEncoderCreate");
        }
        // Configure your encoder here (see avif/avif.h):
        // * maxThreads
        // * quality
        // * qualityAlpha
        // * tileRowsLog2
        // * tileColsLog2
        // * speed
        // * keyframeInterval
        // * timescale
        encoder.maxThreads = Runtime.getRuntime().availableProcessors();
//        encoder.quality = quality;
//        encoder.qualityAlpha = AvifLibrary.AVIF_QUALITY_LOSSLESS;

        // Call avifEncoderAddImage() for each image in your sequence
        // Only set AVIF_ADD_IMAGE_FLAG_SINGLE if you're not encoding a sequence
        // Use avifEncoderAddImageGrid() instead with an array of avifImage* to make a grid image
        int addImageResult = AvifLibrary.INSTANCE.avifEncoderAddImage(encoder, image, 1, AvifLibrary.avifAddImageFlag.AVIF_ADD_IMAGE_FLAG_SINGLE);
        if (addImageResult != AvifLibrary.avifResult.AVIF_RESULT_OK) {
            throw new IllegalStateException(String.format("Failed to add image to encoder: %s", AvifLibrary.INSTANCE.avifResultToString(addImageResult)));
        }

        avifRWData avifOutput = new avifRWData();
        int finishResult = AvifLibrary.INSTANCE.avifEncoderFinish(encoder, avifOutput);
        if (finishResult != AvifLibrary.avifResult.AVIF_RESULT_OK) {
            throw new IllegalStateException(String.format("Failed to finish encode: %s", AvifLibrary.INSTANCE.avifResultToString(finishResult)));
        }

        LOGGER.log(Level.FINE, "Encode success: %d total bytes", avifOutput.size.longValue());

        AvifLibrary.INSTANCE.avifRGBImageFreePixels(rgb);
        AvifLibrary.INSTANCE.avifEncoderDestroy(encoder);

        return avifOutput.data.getByteBuffer(0, avifOutput.size.longValue());
    }
}
