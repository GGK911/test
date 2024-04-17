package imgeTest;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IoUtil;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import pdfTest.ImageUtil;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/15 10:58
 */
public class test {

    @Test
    @SneakyThrows
    public void scale() {
        Path imgPath = Paths.get("src/main/resources/file/image", "test.webp");
        Path outPath = Paths.get("target", "test.webp");
        byte[] img = Files.readAllBytes(imgPath);

        System.out.println(FileTypeUtil.getType(Hex.toHexString(img)));

        Iterator<ImageReader> suffixReader = ImageIO.getImageReadersBySuffix("webp");
        ImageReader imageReader = null;
        while (suffixReader.hasNext()) {
            imageReader = suffixReader.next();
        }
        assert imageReader != null;
        ImageWriter imageWriter = ImageIO.getImageWriter(imageReader);

        BufferedImage read = ImageIO.read(IoUtil.toStream(img));
        Thumbnails.Builder<BufferedImage> imageBuilder = Thumbnails.of(read).size(100, 100);
        imageBuilder.toFile(outPath.toAbsolutePath().toString());
    }

    @Test
    @SneakyThrows
    public void webToPng() {
    }
}
