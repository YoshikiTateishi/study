// CheckFX2

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

public class CheckFX2 {

    static String pathSrc = "OOP2019";
    static String testee = "CheckSIDApp.java";
    static String tester = "FXTester.java";
    static String pathWork = "OOPwork";

    public static void main(String[] args) throws Exception {
    	long startTime = System.currentTimeMillis();
    	System.out.println("計測開始");
        String[] javac = {"javac", "-encoding", "UTF-8",
            "-classpath", pathWork, "Ex001.java"};
        String[] java = {"java", "-classpath", pathWork, "Ex001"};
        // 作業域クリア
        File dirWork = new File(pathWork);
        File[] files = dirWork.listFiles();
        for (File file : files) {
            if (!tester.equals(file.getName())) {
                file.delete();
            }
        }
        // ソースファイル取得
        File dirSrc = new File(pathSrc);
        File[] subdirs = dirSrc.listFiles();
        for (File subdir : subdirs) {
            File srcFile = new File(subdir.getPath());
            String src = subdir.getPath();
            System.out.print("+ " + subdir.getName());
            if (!src.endsWith("_Q1.java")) {
            	System.out.println(" --- Skip! NOT java file");
            	continue;
            }
            
            // ソースファイルコピー
            File testerProg = new File(pathWork + "\\" + tester);
            File testeeFile = new File(pathWork + "\\" + testee);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(srcFile), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(testeeFile), "UTF-8"));
            String data = null;
            int count = 0;
            while ((data = in.readLine()) != null) {
                if (data.startsWith("\ufeff")) { // BOM除去
                    data = data.substring(1);
                }
                if (data.trim().startsWith("package")) { // package宣言削除
                    out.write("//" + data + "\n");
                } else {
                    out.write(data + "\n");
                }
                count++;
            }
            out.close();
            System.out.println(" --- " + count + " lines");
            // ソースファイルのコンパイル
            javac[5] = testeeFile.getPath();
            ProcessBuilder pb1 = new ProcessBuilder();
            pb1.command(javac);
            Process p1 = pb1.start();
            int ret1 = p1.waitFor();
            if (ret1 != 0) {
                System.out.printf("  - %s Compile NG\n", testee);
                continue;
            } else {
                System.out.printf("  - %s Compile OK\n", testee);
            }
            // テストファイルのコンパイル
            javac[5] = testerProg.getPath();
            ProcessBuilder pb0 = new ProcessBuilder();
            pb0.command(javac);
            Process p0 = pb0.start();
            int ret0 = p0.waitFor();
            if (ret0 != 0) {
                System.out.printf("  - %s Compile NG\n", tester);
                continue;
            } else {
                System.out.printf("  - %s Compile OK\n", tester);
            }
            // 実行
            java[2] = pathWork;
            java[3] = tester.substring(0, tester.length() - 5);
            ProcessBuilder pb2 = new ProcessBuilder();
            pb2.redirectErrorStream(true);
            pb2.command(java);
            Process p2 = pb2.start();
            boolean ontime = p2.waitFor(5, TimeUnit.SECONDS);
            if (!ontime) {
                System.out.println("  - Timeout in thread \"main\"");
                p2.destroy();
            }
            // 結果表示
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    p2.getInputStream(), "MS932"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("【実行対象:")) {
                    System.out.println("  " + line);
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("処理時間：" + (endTime-startTime) + "ms");
    }
}
