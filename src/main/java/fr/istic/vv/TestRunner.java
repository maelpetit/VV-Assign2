package fr.istic.vv;

import javassist.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class TestRunner {

    private ClassPool pool;
    private Loader loader;
    private MyTranslator translator;
    private JUnitCore jUnitCore;
    private Set<Class> testClasses = new HashSet<Class>();
    private URL[] urls;

    private TestRunner(String projectPath){
        pool = ClassPool.getDefault();
        loader = new Loader(pool);
        translator = new MyTranslator();
        File classDir = new File(projectPath + "/target/classes");
        File testDir = new File(projectPath + "/target/test-classes");
        try {
            loader.addTranslator(pool, translator);
            pool.appendClassPath(classDir.getPath());
            pool.appendClassPath(testDir.getPath());

            jUnitCore = new JUnitCore();
            String[] _testClasses = testDir.list();
            for(CtClass ctClass : pool.get(_testClasses)){
                testClasses.add(ctClass.toClass());
            }

            urls = new URL[translator.getCtClasses().size()];
            int i = 0;
            for(CtClass classToReload : translator.getCtClasses()){
                urls[i++] = classToReload.getURL();
            }

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }


    }

    private void runTests() throws NotFoundException, CannotCompileException {
        for(Class testClass : testClasses){
            System.out.println("test: "+ testClass.getName());
            Request request = Request.aClass(testClass);
            Result r = jUnitCore.run(request);
            System.out.println("Tests ran : " + r.getRunCount() + ", failed : " + r.getFailureCount());
        }
    }

    private void findTestClasses(String testDir){

    }

    public static void main(String[] args) throws Throwable {
        String projectDir ="TargetProject";
        if(args.length > 2){
            projectDir = args[0];
        }

        TestRunner testRunner = new TestRunner(projectDir);
        testRunner.runTests();
    }



}