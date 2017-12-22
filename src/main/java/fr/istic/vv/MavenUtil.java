package fr.istic.vv;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class MavenUtil {

    public static int execGoals(String goals, String projectDir, boolean timeOut) {
        if(timeOut) {
            String[] _command = {
                    "mvn", "-f", projectDir + "/pom.xml"
            };
            String[] _goals = goals.split(" ");
            String[] command = new String[_command.length + _goals.length];
            int i = 0;
            for (String c : _command) {
                command[i++] = c;
            }
            for (String g : _goals) {
                command[i++] = g;
            }

            ProcessBuilder builder = new ProcessBuilder(command).inheritIO();
            Process process = null;
            try {
                process = builder.start();
                process.waitFor(30, TimeUnit.SECONDS);
                process.destroy();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            try {
                return process.exitValue();
            } catch (IllegalThreadStateException e) {
                return -1;
            }
        }else{
            InvocationRequest request = new DefaultInvocationRequest();
            request.setPomFile( new File( projectDir + "/pom.xml" ) );
            request.setGoals( Collections.singletonList( goals ) );

            Invoker invoker = new DefaultInvoker();
            try {
                return invoker.execute(request).getExitCode();
            } catch (MavenInvocationException e) {
                return 1;
            }
        }
    }
}
