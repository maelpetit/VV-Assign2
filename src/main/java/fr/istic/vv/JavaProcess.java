package fr.istic.vv;

import fr.istic.vv.log.FileLog;
import javassist.CtClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JavaProcess {
    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    private JavaProcess() {}

    public static int exec(Class klass, String targetProjectPath) throws IOException,
            InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = klass.getCanonicalName();
        String args = targetProjectPath;

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className, args);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            //FileLog.log(line);
            if(line.contains("[INFO]") || line.contains("[ERROR]") || line.contains("	at")){

            }
            else {
                logger.info(line);
            }
        }
        //FileLog.writeLog("JavaProcess");

        process.waitFor();
        return process.exitValue();
    }

}
