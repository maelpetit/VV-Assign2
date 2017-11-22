package fr.istic.vv;

import javassist.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MutateTests {

    static ClassPool pool;
    static Loader loader;
    static MyTranslator translator;
    static JUnitCore jUnitCore;
    static Set<Class> testClasses = new HashSet<Class>();

    @BeforeClass
    public static void initClass() throws Throwable {
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
    }

    /*@Before
    public void beforeTest() throws Throwable {
        loader.run("fr.istic.vv.TargetApp", null);
    }*/

    @After
    public void afterTest(){
        Mutators.deleteTargetClasses(translator.getCtClasses());
    }

    @Test
    public void replaceReturnInDoubleMethodsTest() throws NotFoundException, CannotCompileException, IOException {
        for(CtClass ctClass : translator.getCtClasses()){
            ctClass.defrost();
            Mutators.replaceReturnInDoubleMethods(ctClass).writeFile("target/classes");
        }
        runTests();
    }

    @Test
    public void setBooleanMethodsToTrue() throws NotFoundException, CannotCompileException, IOException {
        for(CtClass ctClass : translator.getCtClasses()){
            ctClass.defrost();
            Mutators.setBooleanMethodsTo(ctClass, true).writeFile("target/classes");
        }
        runTests();
    }

    @Test
    public void setBooleanMethodsToFalse() throws NotFoundException, CannotCompileException, IOException {
        for(CtClass ctClass : translator.getCtClasses()){
            ctClass.defrost();
            Mutators.setBooleanMethodsTo(ctClass, false).writeFile("target/classes");
        }
        runTests();
    }

    private void runTests() throws NotFoundException, CannotCompileException {
        for(Class testClass : testClasses){
            System.out.println("test: "+ testClass.getName());
            Request request = Request.aClass(testClass);
            Result r = jUnitCore.run(request);
            System.out.println("Tests ran : " + r.getRunCount() + ", failed : " + r.getFailureCount());
        }
    }

    /*@Test
    public void VoidTest(){
        Mutators.deleteTargetClasses(translator.getCtClasses());
    }*/
}
