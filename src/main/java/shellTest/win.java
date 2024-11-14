package shellTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class win {
    public static void main(String[] args) {


        callShellByExec("wkhtmltopdf https://www.cnblogs.com/huio/p/17105711.html D:密评之——密钥管理2.pdf");
    }

    /**
     * 使用 exec 调用 shell 命令
     */
    public static void callShellByExec(String shellString) {
        Process process = null;
        try {
            // 使用 ProcessBuilder 运行命令，并合并错误流到标准输出
            ProcessBuilder processBuilder = new ProcessBuilder(shellString.split(" "));
            processBuilder.redirectErrorStream(true); // 将错误流合并到标准输出

            process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Output: " + line);
                }
            }

            int exitValue = process.waitFor();
            if (exitValue != 0) {
                System.out.println("call shell failed. error code is :" + exitValue);
            }

        } catch (Throwable e) {
            System.err.println("call shell failed. " + e.getMessage());
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
