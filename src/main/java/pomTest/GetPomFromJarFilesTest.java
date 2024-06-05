package pomTest;

import lombok.SneakyThrows;
import org.apache.commons.collections4.map.HashedMap;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/30 11:28
 */
public class GetPomFromJarFilesTest {

    @Test
    @SneakyThrows
    public void test() {
        Element dependencys = new DOMElement("dependencys");
        File dir = new File("C:\\Users\\ggk911\\IdeaProjects\\cmsTestDemo\\src\\main\\resources\\jar");        //需生成pom.xml 文件的 lib路径
        // 遍历所有jar文件
        Map<String, Integer> nameMap = new HashedMap<>();
        for (File jarFile : dir.listFiles()) {
            String jarFileName = jarFile.getName();
            // 从文件转成可识别的对象
            JarInputStream jis = new JarInputStream(Files.newInputStream(jarFile.toPath()));
            Manifest mainmanifest = jis.getManifest();
            jis.close();
            String name = "";
            String version = "";
            // 能直接读出来可识别的对象
            if (mainmanifest != null) {
                name = mainmanifest.getMainAttributes().getValue("Bundle-Name");
                if (name == null) {
                    name = mainmanifest.getMainAttributes().getValue("Specification-Title");
                    if (name == null) {
                        name = jarFileName.substring(0, jarFileName.lastIndexOf("."));
                    }
                }
                version = mainmanifest.getMainAttributes().getValue("Bundle-Version");
                if (version == null) {
                    version = mainmanifest.getMainAttributes().getValue("Specification-Version");
                    if (version == null) {
                        version = mainmanifest.getMainAttributes().getValue("Manifest-Version");
                    }
                }
            } else {
                // 读不出来
                name = jarFileName.substring(0, jarFileName.lastIndexOf("."));

            }
            name = name.replace(" ", "-");
            if (nameMap.containsKey(name)) {
                nameMap.put(name, nameMap.get(name) + 1);
            } else {
                nameMap.put(name, 1);
            }

            System.out.println(name + nameMap.get(name) + " - " + version);

        }
    }

}
