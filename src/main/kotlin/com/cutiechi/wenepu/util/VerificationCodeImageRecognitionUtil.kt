package com.cutiechi.wenepu.util

import java.awt.Color
import java.awt.image.BufferedImage
import java.util.ArrayList
import java.util.HashMap
import javax.imageio.ImageIO

object VerificationCodeImageRecognitionUtil {

    private val TARGET_COLOR = Color.BLACK.rgb
    private val USELESS_COLOR = Color.WHITE.rgb
    private const val UNIT_WIDTH = 9
    private const val UNIT_HEIGHT = 12
    private var referenceImageMap: MutableMap<BufferedImage, Char>? = null

    private fun isTarget(colorInt: Int): Boolean {
        val color = Color(colorInt)
        val hsb = FloatArray(3)
        Color.RGBtoHSB(color.red, color.green, color.blue, hsb)
        return hsb[2] < 0.5f
    }

    private fun deNoise(verificationCodeImage: BufferedImage): BufferedImage {
        val width = verificationCodeImage.width
        val height = verificationCodeImage.height
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (isTarget(verificationCodeImage.getRGB(x, y))) {
                    verificationCodeImage.setRGB(x, y, TARGET_COLOR)
                } else {
                    verificationCodeImage.setRGB(x, y, USELESS_COLOR)
                }
            }
        }
        return verificationCodeImage
    }

    private fun split(verificationCodeImage: BufferedImage): ArrayList<BufferedImage> {
        val unitVerificationCodeImageList = ArrayList<BufferedImage>()
        unitVerificationCodeImageList.add(verificationCodeImage.getSubimage(3, 4, UNIT_WIDTH, UNIT_HEIGHT))
        unitVerificationCodeImageList.add(verificationCodeImage.getSubimage(13, 4, UNIT_WIDTH, UNIT_HEIGHT))
        unitVerificationCodeImageList.add(verificationCodeImage.getSubimage(23, 4, UNIT_WIDTH, UNIT_HEIGHT))
        unitVerificationCodeImageList.add(verificationCodeImage.getSubimage(33, 4, UNIT_WIDTH, UNIT_HEIGHT))
        return unitVerificationCodeImageList
    }

    private fun loadReferenceImageMap(): MutableMap<BufferedImage, Char>? {
        if (referenceImageMap == null) {
            referenceImageMap = HashMap<BufferedImage, Char>()
            val referenceImageNameList = ArrayList<String>()
            referenceImageNameList.add("1.png")
            referenceImageNameList.add("2.png")
            referenceImageNameList.add("3.png")
            referenceImageNameList.add("b.png")
            referenceImageNameList.add("c.png")
            referenceImageNameList.add("n.png")
            referenceImageNameList.add("m.png")
            referenceImageNameList.add("v.png")
            referenceImageNameList.add("x.png")
            referenceImageNameList.add("z.png")
            for (referenceImageName in referenceImageNameList) {
                (referenceImageMap as HashMap<BufferedImage, Char>)[ImageIO.read(this.javaClass.classLoader
                        .getResourceAsStream("images/$referenceImageName"))] = referenceImageName[0]
            }
        }
        return referenceImageMap
    }

    private fun unitRecognition(unitVerificationCodeImage: BufferedImage, referenceImageMap: MutableMap<BufferedImage, Char>): Char {
        var result = ' '
        val width = unitVerificationCodeImage.width
        val height = unitVerificationCodeImage.height
        var minDifferencePixel = width * height
        for (referenceImage in referenceImageMap.keys) {
            var differencePixel = 0
            for (x in 0 until width) {
                for (y in 0 until height) {
                    differencePixel += if (unitVerificationCodeImage.getRGB(x, y) != referenceImage.getRGB(x, y)) 1 else 0
                    if (differencePixel >= minDifferencePixel) {
                        break
                    }
                }
            }
            if (differencePixel < minDifferencePixel) {
                minDifferencePixel = differencePixel
                result = referenceImageMap.get(referenceImage)!!
            }
        }
        return result
    }

    fun recognition(image: BufferedImage): String {
        var verificationCodeImage = image
        var verificationCode = ""
        verificationCodeImage = deNoise(verificationCodeImage)
        val unitVerificationCodeImageList = split(verificationCodeImage)
        val referenceImageMap = loadReferenceImageMap()
        for (unitVerificationCodeImage in unitVerificationCodeImageList) {
            verificationCode += unitRecognition(unitVerificationCodeImage, referenceImageMap!!)
        }
        return verificationCode
    }
}
