package imgeTest;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 压缩图片，其实是缩放大小
 */
public class ThumbnailsUtils {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ThumbnailsUtils.class);

    /**
     * 按文件大小压缩一个输入流返回一个输出流
     *
     * @param imageBytes  图片字节数组
     * @param confineSize 限制文件大小 kb
     * @param imageName   图片名称
     * @return
     */
    public static ByteArrayOutputStream compressToOut(byte[] imageBytes, int confineSize, String imageName) {
        try {
            byte[] bytes = compressPicForScaleToBytes(imageBytes, confineSize, imageName);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            return out;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    /**
     * 按文件大小压缩一个图片字节数组返回一个输入流
     *
     * @param imageBytes  图片字节数组
     * @param confineSize 限制文件大小 kb
     * @param imageName   图片名称
     */
    public static ByteArrayInputStream compressIn(byte[] imageBytes, int confineSize, String imageName) {

        try {
            byte[] bytes = compressPicForScaleToBytes(imageBytes, confineSize, imageName);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            return in;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 按文件大小压缩一个图片字节数组到本地文件
     *
     * @param imageBytes  图片字节数组
     * @param toFile      目标文件名称路径
     * @param confineSize 限制文件大小 kb
     * @param imageName   图片名称
     * @return
     */
    public static void compressFile(byte[] imageBytes, String toFile, int confineSize, String imageName) {
        try {
            byte[] bytes = compressPicForScaleToBytes(imageBytes, confineSize, imageName);
            OutputStream out = new FileOutputStream(toFile);
            out.write(bytes);
            out.flush();
            IOUtils.closeQuietly(out);
            out = null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 根据指定大小压缩图片
     *
     * @param imageBytes  源图片字节数组
     * @param desFileSize 指定图片大小，单位kb
     * @param imageName   影像名称
     * @return 压缩质量后的图片字节数组
     */
    public static byte[] compressPicForScaleToBytes(byte[] imageBytes, long desFileSize, String imageName) {
        if (imageBytes == null || imageBytes.length <= 0 || imageBytes.length < desFileSize * 1024) {
            return imageBytes;
        }
        long srcSize = imageBytes.length;
        double quality = getQuality(srcSize / 1024);
        try {
            while (imageBytes.length > desFileSize * 1024) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
                Thumbnails.of(inputStream).scale(quality).outputQuality(1).toOutputStream(outputStream);
                imageBytes = outputStream.toByteArray();
                // 关闭流
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(outputStream);
                inputStream = null;
                outputStream = null;
            }
        } catch (Exception e) {
            logger.error("msg=图片ys失败!", e);
        }
        return imageBytes;
    }

    /**
     * 宽 高
     *
     * @param imageBytes
     * @param desFileSize
     * @param imageName
     * @return
     */
    public static byte[] compressPicForScaleToBytesHighet(byte[] imageBytes, long desFileSize, String imageName,
                                                          int high, int width, double quality) {
        if (imageBytes == null || imageBytes.length <= 0 || imageBytes.length < desFileSize * 1024) {
            return imageBytes;
        }
        try {

            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
            Thumbnails.of(inputStream).size(width, high).outputQuality(quality).toOutputStream(outputStream);
            imageBytes = outputStream.toByteArray();
            // 关闭流
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            inputStream = null;
            outputStream = null;
        } catch (Exception e) {
            logger.error("msg=图片ys失败!", e);
        }
        return imageBytes;
    }

    /**
     * 根据指定大小压缩图片
     *
     * @param imageBytes  源图片字节数组
     * @param desFileSize 指定图片大小，单位kb
     * @param imageName   影像名称
     * @return 压缩质量后的图片字节数组
     */
    public static byte[] compressPicForScaleToBytes(byte[] imageBytes, long desFileSize, String imageName,
                                                    double quality) {
        if (imageBytes == null || imageBytes.length <= 0 || imageBytes.length < desFileSize * 1024) {
            return imageBytes;
        }
        long srcSize = imageBytes.length;
//		double quality = getQuality(srcSize / 1024);
        try {
            while (imageBytes.length > desFileSize * 1024) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
                Thumbnails.of(inputStream).scale(quality).outputQuality(quality).toOutputStream(outputStream);
                imageBytes = outputStream.toByteArray();
                // 关闭流
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(outputStream);
                inputStream = null;
                outputStream = null;
            }
        } catch (Exception e) {
            logger.error("msg=图片ys失败!", e);
        }
        return imageBytes;
    }

    /**
     * 根据指定大小压缩图片
     *
     * @param imageBytes  源图片字节数组
     * @param desFileSize 指定图片大小，单位kb
     * @param imageName   影像名称
     * @return 压缩质量后的图片字节数组
     */
    public static byte[] compressPicForScaleToBytesWithquality(byte[] imageBytes, long desFileSize, String imageName,
                                                               double quality) {
        if (imageBytes == null || imageBytes.length <= 0 || imageBytes.length < desFileSize * 1024) {
            return imageBytes;
        }
        try {
            if (imageBytes.length > desFileSize * 1024) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
                Thumbnails.of(inputStream).scale(quality).outputQuality(quality).toOutputStream(outputStream);
                imageBytes = outputStream.toByteArray();
                // 关闭流
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(outputStream);
                inputStream = null;
                outputStream = null;
            }

        } catch (Exception e) {
            logger.error("msg=图片ys失败!", e);
        }
        return imageBytes;
    }

    /**
     * 自动调节精度(经验数值)
     *
     * @param size 源图片大小 kb
     * @return 图片压缩质量比
     */
    private static double getQuality(long size) {
        if (size < 900) {
            return 0.85;
        } else if (size < 2047) {
            return 0.6;
        } else if (size < 3275) {
            return 0.44;
        }
        return 0.4;

    }

    public static void main(String args[]) {

        File file = new File("C:\\test2.jpg");
        getFileByBytes(compressPicForScaleToBytes(Base64.decodeBase64(file2Base64(file)), 30, "test"), "D:\\",
                "test3.png");

    }

    public static byte[] File2byte(File tradeFile) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(tradeFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static void getFileByBytes(byte[] bytes, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists()) {// 判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String file2Base64(File file) {
        if (file == null) {
            return null;
        }
        String base64 = null;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            base64 = Base64.encodeBase64String(buff);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }

}
