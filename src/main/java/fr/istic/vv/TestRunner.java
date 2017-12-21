package fr.istic.vv;

import javassist.*;
import org.apache.maven.shared.invoker.*;
import org.junit.runner.JUnitCore;

import java.io.File;
import java.net.URL;
import java.util.*;

public class TestRunner {

    private ClassPool pool;
    private Loader loader;
    //private MyTranslator translator;
    private JUnitCore jUnitCore;
    private Set<Class> testClasses = new HashSet<Class>();
    private String projectDir;
    private URL[] urls;
    private boolean allTestPassed;

    private TestRunner(String projectPath){
        projectDir = projectPath;
        pool = ClassPool.getDefault();
        loader = new Loader(pool);
        File classDir = new File(projectPath + "/target/classes");
        File testDir = new File(projectPath + "/target/test-classes");
        try {
            pool.appendClassPath(classDir.getPath());
            pool.appendClassPath(testDir.getPath());

            jUnitCore = new JUnitCore();

            String[] _testClasses = findTestClasses(testDir, "").toArray(new String[0]);

            for(CtClass ctClass : pool.get(_testClasses)){
                try {
                    testClasses.add(ctClass.toClass());
                }catch(CannotCompileException e){
                    //e.printStackTrace();
                }
            }

        } catch (NotFoundException e) {
            e.printStackTrace();
        }


    }

    private void runTests() throws NotFoundException, CannotCompileException, MavenInvocationException {

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( projectDir + "/pom.xml" ) );
        request.setGoals( Collections.singletonList( "surefire:test" ) );

        Invoker invoker = new DefaultInvoker();
        InvocationResult result = invoker.execute( request );
        allTestPassed = result.getExitCode() == 0;
    }

    private List<String> findTestClasses(File testDir, String pkg){
        List<String> res = new ArrayList<>();
        for(File file : testDir.listFiles()){
            if(file.isFile() && file.getName().endsWith(".class")){
                String fileName = file.getName();
                res.add(pkg + fileName.substring(0, fileName.length() - 6));
            }else if(file.isDirectory()){
                res.addAll(findTestClasses(file, pkg + file.getName() + "."));
            }
        }
        return res;
    }

    public static void main(String[] args) throws Throwable {
        String projectDir = "/home/mael/M2/VetV/commons-cli";//"/home/mael/M2/VetV/TargetProject";
        System.setProperty("maven.home", "/home/mael/Applications/apache-maven-3.5.0");
        if(args.length > 0){
            projectDir = args[0];
        }

        //FileLog
//        FileLog.writeLog("FileLog");

        TestRunner testRunner = new TestRunner(projectDir);
        testRunner.runTests();


//        FileLog.writeLog("TestRunner");
    }



}
