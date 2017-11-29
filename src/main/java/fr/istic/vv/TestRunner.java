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
    static ClassPool pool;
    static Loader loader;
    static MyTranslator translator;
    static JUnitCore jUnitCore;
    static Set<Class> testClasses = new HashSet<Class>();
    static String targetDir = "target/classes";
    static URL[] urls;

    public static void main(String[] args) throws Throwable {

        pool = ClassPool.getDefault();
        loader = new Loader(pool);
        translator = new MyTranslator();
        File classDir = new File("TargetProject/target/classes");
        File testDir = new File("TargetProject/target/test-classes");
        loader.addTranslator(pool, translator);
        pool.appendClassPath(classDir.getPath());
        pool.appendClassPath(testDir.getPath());
        loader.run("fr.istic.vv.TargetApp", null);

        jUnitCore = new JUnitCore();
        String[] _testClasses = {"fr.istic.vv.AdditionTest",
                "fr.istic.vv.MultiplicationTest",
                "fr.istic.vv.DivisionTest",
                "fr.istic.vv.SubtractionTest"};
        for(CtClass ctClass : pool.get(_testClasses)){
            testClasses.add(ctClass.toClass());
        }

        URL[] urls = new URL[translator.getCtClasses().size()];
        int i = 0;
        for(CtClass classToReload : translator.getCtClasses()){
            urls[i++] = classToReload.getURL();
        }

        runTests(testClasses, jUnitCore);
    }

    private static void runTests(Set<Class> testClasses, JUnitCore jUnitCore) throws NotFoundException, CannotCompileException {
        for(Class testClass : testClasses){
            System.out.println("test: "+ testClass.getName());
            Request request = Request.aClass(testClass);
            Result r = jUnitCore.run(request);
            System.out.println("Tests ran : " + r.getRunCount() + ", failed : " + r.getFailureCount());
        }
    }

}
