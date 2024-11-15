package fileTest;

import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 转文件
 *
 * @author TangHaoKai
 * @version V1.0 2024/6/13 17:13
 */
public class test {

    private static byte[] file;

    static {
        Path path = Paths.get("src/main/java/fileTest", "hex");
        try {
            file = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @SneakyThrows
    public void hexToFile() {
        Path path = Paths.get("src/main/java/fileTest/test.png");
        FileUtil.writeBytes(Hex.decode(file), path.toAbsolutePath().toString());
    }

    @Test
    @SneakyThrows
    public void strToFile() {
        Path path = Paths.get("src/main/java/fileTest/test.png");
        String hexStr = "89504e470d0a1a0a0000000d494844520000012c0000012c0806000000797d8e7500000a054944415478daeddd5b8ea43a104551e63f69f7044aadca4a6cc789585bf2dfbd2dc0662550f9789ee75986611821c341300c0358866118c0320c0358866118c0320cc300966118c0320cc348054b924e052c49c0922460490216b024014b9280250958c092042c4902962460014b12b02409589280052c49c0922460490216b024014b9280250958c092042c49029624604912b02409585285134ec092224f38014b8a3be1042c29ea84734e014b8a02cbb9052c290e2ce718b0a438b09c6bc09280052c254dbc72c012b04cba431101968065c2cd6104580296c9368f1160095826da5c020b587292c85c004b879f99985360014b3158995760012bf4a49d3e042c60410b5a4e38c71e58d08216b080d5e8a0c2075ac00256af1d968005acaa3b2d010b589280052c09588e1fb02460014b524fb056c1e3072c0958ffdd0f6049c08ada076049c08ada7e6049c08ada766049c0b2ddc09280f5f636efde766049c07a15ab9ddb0f2c0958af63b56b1f80a5d627bdd1ebab89802560193168014bd03262d00296a065c4a0052cb5462b1959014b5f2c10010b588a5a1c0216b014b53074062cc71a587ae9a412b08025b72c8eaf630c2ced3899cc3fb080a518acac012f0ac0521456d601b080a528acac85bdc75a8160f958c3bc4fe603cb710596012d60010b5a86136ceffa55e0332c18000b588e61d443f7ca136b310a58c0faef86775894829575d218ac4e8b52d686b5022c60a91d58cb7104d6cd45296bc39a0196ab2bb5056b398ec07275a514ac80052c57572ab33e1eeb0758aeae94b03e3ef97f1c4b60c14a25c08216b080a588db4160010b566a09d6723c81b50b2b6059279fae0d6b0a58d15757e09b7175e52a0b586db0f28231072c5759c08a04cb1cccbb1d749505ac565757d0ea7f75052d60955a88d0b24e8005ac5657579e65ccbd1d8416b022b18296ab706f9f0156c4ad20b4ac155759c08ac4cac2b55626cf3db09a83052d6001ebf15789b41f9655afb53275de81158a15b4803571de81d56051406be68bdbc479075683c5e01b23e6ce01b080150980afba99b95ea6cd37b01a5dad406be6319f34dfc06a34f9be5470ee0bdc94f90656b34987d6ccabf129f30dac86130ead99b7df13e61b584d271a5a33d74cf7f90656d309f633e87351e83cdfe3c19afc6a04ad1eb78289db082c58bd0a16b4b221e83adf63c1f26760687507a0e39c8f046bda490bacb9572bdde67d1458534f585759b3d74ca7791f03d69413756d18b0ea7d9277faf859fc624e9ba45570c06aee9b89d36e71c77d44653582065ab07a7b8d026bc0150ab060d5edfc0016b0b663032ccf399f27fb85ace52da12b9f3ecff4aaae998938277c561258cd6ec58035f3aaeae47902acc668394961d5f93c015628584e54b780d3cf15601544cb95857d0717b04a4ec294c5e9aa0a5ac00a9b80c90b1354d0aa72fe8cf82c21a0fe7e5cdcfe41abd28b3cb0062f5658812a0dfdf1dfd66041cefd6246f39ff7f8602c58faf9d8804a951f1f8cfb023fcdbe6551f615b99ff912a81473b7022c799ea2983b1660c9b314b5997f8b43110b56c002964a2f58010b5892802549c092042c6049029624014b12b08025095892042c49c0029624604912b024010b589280a53b932f73072c454dbab2e71058c09a37d96a3197c0b2c85b4f72f27cba359a756e020b522dc03afd439e5e8480a54b48a5ffd6e4a9edaf749c26bc18010b50ed16f7a9edaf769cd650b48005a8e8b93db50fd58e932b2c608d072a717e4fec43c563e5195623b0ba3c8b01d6fd13b7eaf19afaebd7adc1aaba8d2b6c80bedef1025663b06e6d2b945c99020b58dfefd420943ceb0316b0c2c15a8d50926758c01a0096db3560a562052c600109585fada30758c04a07ebafdbab2cb01e6001ab2a58a7b757774ede052c605545abeb1502b0f68365cd000b58e00256b1ed4bf9960a6015bf85d5fe678a158ffdc96d5cc002d61b600958b73ef80d2c60c12a08ac670058897f6c00966758a3c0f2879adac70158b61558c08a790105966dfdf53602abce71a8f271a46a9fa104966d6df346e1dd60ade10358c02af9cca02b58a7fe0d68016b045895b60b58d0bab1368005ace8673927c13af12c0858c00256e363f7d6f13bfd00bbeb55b95bc2e6275dea8f1b74037ff7fb99a63c4600d6c113cfc8fc985065b0a63df70416b0a0b5f9e4edfc9d6fc00296f1d47d9f18b080052ca3f5f79b775edfc07a3c749fb89d4fe3931758c032a1c08a98ebd4e30d2c60010b58c00296090516b08005ac5160e9fd63987cbc81052cdb092c5758c032a1165ee62da1f5fdfb6d0116b08075e8187ae3e83bdb01ac0213ba7b3fab7c65c764b012d779d5af7a015611b0ba7eeabec3955cc5efc3ea0e96efc30a5ac45d16de02d6c7ff0eb0ea4209ac03fb7e6be1757a6676f24728a68375fb7d83c0babc806f4c7eb707fda77ff9792a58153e080fac8b0be3c6c477fc95693f555f6fdddc421358c638b0cc7bdd35042c0b371ead6abf826c000b58c0029635042c8b1658e61f581eba871f97c90fdddf04abcb43f794b906d660b01e6095451e58c00256e842af0496790416b02cf4ebfb042c60010b58c0328fc00216b06e81e51916b08065a197d82760010b58c06a07d6fae1bf378fc00296851e03d6ad0f237ffbef03abd0020616b04e8395f24780d4ef010316b0ecd326b04ebc03ffed7d0556f1050c2cfbb41bac9d5f7fb30d06606583e583aac0fa16ac4a2fa455d70ab080052c60bdb2ee81052c600d03eb0906cbef12020b589e61010b581eba03ebcc739ddbdf86ea961058c00adda7935f973c0d2c0fdd0fbe0a026b1e589fecef0296bf12020b5895c0daf5fc0f58c03a3a91c00216b080052c605dd9a7d37f5d0516b08005acaff70958c00216b0a2f6e9c47bd780052c6001ebb57ddafd465b6001ebe824020b5827de1251711e81052c6015dca79d1f610216b08005aceb60dd3edec002d6d6671c3efc9c0f56a5931e5843c07a8005ac0ffeffaa57b4c00a076b010b5805f70958c06ab7309cdcc00216b0227e9dc4c90d2c600d036b010b58c00216b080659f8005ac90db4160d92760012b6ab102cb3e010b58c07272030b58c09a3069f60758c00a046b010b58c0025627b09ce0c00216b080052cfb042c6055c20a58f60958c00296937bc43e2d6001eb397c107d5b03b04eac1b60010b58c0bab24fd60ab0ae60052c605933c06a099693db3e4d450b5845b002967daafad0dd0b4103b09ce0c09afcbc155841cf059ce0c04a7d0b8ee30a2c27b8fd89f85486b5d210ac9bdb002c60eddad60ec7f60156adcbe96927785a690fb6938e73e4ed756530a6becabcbd3fc9759b1f6b2518aceab7a3e98bb0eb4905aabe6b25162ccf1dfebe3fae0294ba56ca8255f1af127215206079f594940f96575049516055bd9796042c49029624604912b024095892802549c092246049029624014b12b08025095892042c49c0029624604912b024010b5892802549c09204acdffc07866118558683601806b00cc33080651806b00cc33080651886012cc330806518865171fc03d2432bb82a6b35ab0000000049454e44ae426082";
        String save = path.toAbsolutePath().toString();
        System.out.println(save);
        FileUtil.writeBytes(Hex.decode(hexStr), save);
    }

    @Test
    @SneakyThrows
    public void strToFile2() {
        Path path = Paths.get("src/main/java/fileTest/test.p7b");
        String str = "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";
        String save = path.toAbsolutePath().toString();
        System.out.println(save);
        FileUtil.writeBytes(str.getBytes(StandardCharsets.UTF_8), save);
    }

    @Test
    @SneakyThrows
    public void updateDirTest() {
        // 修改文件的父目录路径
        Path path = Paths.get("src/main/java/fileTest");
        File root = path.toFile();

        File dirFile = new File(root, "dir");
        File dirFile1 = new File(root, "dir1");

        // renameTo方法
        if (dirFile.renameTo(dirFile1)) {
            System.out.println("成功");
        }

    }

}
