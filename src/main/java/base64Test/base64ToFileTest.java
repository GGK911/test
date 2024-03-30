package base64Test;

import cn.hutool.core.io.FileUtil;
import org.bouncycastle.util.encoders.Base64;

/**
 * @author TangHaoKai
 * @version V1.0 2024/3/22 17:01
 */
public class base64ToFileTest {
    public static void main(String[] args) {
        String imgStr = "iVBORw0KGgoAAAANSUhEUgAAASwAAAEsCAIAAAD2HxkiAAABHUlEQVR4nO3BAQ0AAADCoPdPbQ43oAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAnAyAYAAFGw74oAAAAAElFTkSuQmCC";
        byte[] decode = Base64.decode(imgStr);
        FileUtil.writeBytes(decode, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\base64Test\\test.png");

    }
}
