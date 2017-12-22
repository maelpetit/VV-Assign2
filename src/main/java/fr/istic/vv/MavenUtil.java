package fr.istic.vv;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Collections;

public class MavenUtil {

    public static InvocationResult execGoals(String goals, String projectDir) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( projectDir + "/pom.xml" ) );
        request.setGoals( Collections.singletonList( goals ) );

        Invoker invoker = new DefaultInvoker();
        return invoker.execute( request );
    }
}
