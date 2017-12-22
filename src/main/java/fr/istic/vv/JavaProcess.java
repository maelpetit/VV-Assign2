package fr.istic.vv;

import fr.istic.vv.log.FileLog;
import javassist.CtClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JavaProcess {
    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    private JavaProcess() {}

    public static int exec(Class klass, String targetProjectPath, String mutation) throws IOException,
            InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = klass.getCanonicalName();

        String[] COMMAND = {
                javaBin,
                "-cp",
                classpath,
                className,
                targetProjectPath
                + "%@%" +
                mutation
        };

        ProcessBuilder builder = new ProcessBuilder(COMMAND).inheritIO();
        builder.redirectErrorStream(true);
        Process process = builder.start();
        process.waitFor();
        process.destroy();
        return process.exitValue();
    }

}
