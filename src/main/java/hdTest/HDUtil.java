package hdTest;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TangHaoKai
 * @version V1.0 2024/5/24 9:55
 */
public class HDUtil {

    public HDUtil() {
    }

    public static String getOsName() {
        String os = "";
        os = System.getProperty("os.name");
        return os;
    }

    public static String getHDSerialNo() {
        String sn = "";
        String os = getOsName();
        if (os.startsWith("Linux")) {
            String command = "dmidecode -s system-serial-number";
            InputStream is = null;

            try {
                Process p = Runtime.getRuntime().exec(command);
                is = p.getInputStream();
                byte[] bytes = new byte[64];

                for (int byteCount = 0; (byteCount = is.read(bytes)) != -1; sn = sn + new String(bytes, 0, byteCount)) {
                }

                System.out.println("sn:" + sn);
            } catch (IOException var36) {
                var36.printStackTrace();
            } finally {
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException var31) {
                        var31.printStackTrace();
                    }
                }

            }
        } else if (os.startsWith("Windows")) {
            InputStream is = null;

            try {
                File file = File.createTempFile("realhowto", ".vbs");
                file.deleteOnExit();
                FileWriter fw = new FileWriter(file);
                String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\nSet colItems = objWMIService.ExecQuery _ \n   (\"Select * from Win32_BaseBoard\") \nFor Each objItem in colItems \n    Wscript.Echo objItem.SerialNumber \n    exit for  ' do the first cpu only! \nNext \n";
                fw.write(vbs);
                fw.close();
                Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
                is = p.getInputStream();
                byte[] bytes = new byte[64];

                for (int byteCount = 0; (byteCount = is.read(bytes)) != -1; sn = sn + new String(bytes, 0, byteCount)) {
                }
            } catch (Exception var34) {
                var34.printStackTrace();
            } finally {
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException var32) {
                        var32.printStackTrace();
                    }
                }

            }
        } else {
            sn = "unknown";
        }

        sn = sn.trim();
        if ("NONE".equalsIgnoreCase(sn)) {
            try {
                sn = getCPU();
            } catch (IOException var33) {
                var33.printStackTrace();
                sn = "unknown";
            }
        }

        return sn;
    }

    public static String getCPU() throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"wmic", "cpu", "get", "ProcessorId"});
        process.getOutputStream().close();
        Scanner sc = new Scanner(process.getInputStream());
        String serial = sc.next();
        System.out.println("CPU SerialNo: " + serial);
        return serial;
    }

    public static String getLocalMac() {
        String mac = null;
        InputStream is = null;

        try {
            String os = getOsName();
            byte[] macByte;
            if (os.startsWith("Linux")) {
                Process p = (new ProcessBuilder(new String[]{"ifconfig"})).start();
                is = p.getInputStream();
                macByte = new byte[64];
                int byteCount = 0;
                String line = "";

                while ((byteCount = is.read(macByte)) != -1) {
                    line = line + new String(macByte, 0, byteCount);
                    Pattern pat = Pattern.compile("\\b\\w+:\\w+:\\w+:\\w+:\\w+:\\w+\\b");
                    Matcher mat = pat.matcher(line);
                    if (mat.find()) {
                        mac = mat.group(0);
                    }
                }
            } else if (os.startsWith("Windows")) {
                InetAddress ia = InetAddress.getLocalHost();
                macByte = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
                StringBuffer sb;
                if (macByte == null) {
                    System.out.println("mac is null");
                    sb = null;
                    return sb.toString();
                }

                sb = new StringBuffer("");
                int i = 0;

                while (true) {
                    if (i >= macByte.length) {
                        mac = sb.toString().toUpperCase();
                        break;
                    }

                    if (i != 0) {
                        sb.append("-");
                    }

                    int temp = macByte[i] & 255;
                    String str = Integer.toHexString(temp);
                    if (str.length() == 1) {
                        sb.append("0" + str);
                    } else {
                        sb.append(str);
                    }

                    ++i;
                }
            }

            System.out.println("本机MAC地址:" + mac);
            String var21 = mac;
            return var21;
        } catch (Exception var18) {
            var18.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException var17) {
                    var17.printStackTrace();
                }
            }

        }

        return null;
    }

    public static String getDeviceSN() {
        String result = "";
        InputStream is = null;

        try {
            String os = getOsName();
            if (os.startsWith("Windows")) {
                File file = File.createTempFile("realhowto", ".vbs");
                file.deleteOnExit();
                FileWriter fw = new FileWriter(file);
                String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\nSet colItems = objWMIService.ExecQuery(\"Select * from Win32_PhysicalMedia where SerialNumber != null\") \nFor Each objItem in colItems \nIf objItem.SerialNumber  <> \"\" then \nWscript.Echo objItem.SerialNumber \nExit For \nEnd If \nNext \n";
                fw.write(vbs);
                fw.close();
                Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
                is = p.getInputStream();
                byte[] bytes = new byte[64];

                for (int byteCount = 0; (byteCount = is.read(bytes)) != -1; result = result + new String(bytes, 0, byteCount)) {
                }
            }
        } catch (Exception var17) {
            var17.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException var16) {
                    var16.printStackTrace();
                }
            }

        }

        System.out.println("本机硬盘SN:" + result.trim());
        return result.trim();
    }

    @SneakyThrows
    public static void main(String[] args) {
        getDeviceSN();
        getLocalMac();
        getOsName();
        getCPU();
        getHDSerialNo();
    }
}

