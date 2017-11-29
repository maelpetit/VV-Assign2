package fr.istic.vv;

import javassist.*;
import javassist.bytecode.BadBytecode;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class MutateTests {

    static ClassPool pool;
    static Loader loader;
    static MyTranslator translator;
    static JUnitCore jUnitCore;
    static Set<Class> testClasses = new HashSet<Class>();
    static String targetDir = "target/classes";
    static URL[] urls;

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

        urls = new URL[translator.getCtClasses().size()];
        int i = 0;
        for(CtClass classToReload : translator.getCtClasses()){
            urls[i++] = classToReload.getURL();
            System.out.println(urls[i-1]);
        }

        //TODO copy .class files in temp folder to restore them after each test
    }

    @Test
    public void run() throws NotFoundException, CannotCompileException, IOException, InterruptedException {
        JavaProcess.exec(testClasses.iterator().next());
    }

    //@After
    public void afterTest() throws Throwable {
        Mutators.deleteTargetClasses(translator.getCtClasses());
        Mutators.restoreClasses(translator.getCtClasses(), targetDir);
        /*URLClassLoader cl = new URLClassLoader(urls, MutateTests.class.getClassLoader()) ;
        for(CtClass ctClass : translator.getCtClasses()){
            Class myClass = cl.loadClass(ctClass.getName());
        }*/
    }

    @Test
    public void replaceReturnInDoubleMethodsTest() throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException {
        System.out.println("MutateTests.replaceReturnInDoubleMethodsTest");
        for(CtClass ctClass : translator.getCtClasses()){
            ctClass.defrost();
            Mutators.replaceReturnInDoubleMethods(ctClass).writeFile(targetDir);
        }
    }

    @Test
    public void setBooleanMethodsToTrue() throws NotFoundException, CannotCompileException, IOException {
        System.out.println("MutateTests.setBooleanMethodsToTrue");
        for(CtClass ctClass : translator.getCtClasses()){
            ctClass.defrost();
            Mutators.setBooleanMethodsTo(ctClass, true).writeFile(targetDir);
        }
    }

    @Test
    public void setBooleanMethodsToFalse() throws NotFoundException, CannotCompileException, IOException {
        System.out.println("MutateTests.setBooleanMethodsToFalse");
        for(CtClass ctClass : translator.getCtClasses()){
            ctClass.defrost();
            Mutators.setBooleanMethodsTo(ctClass, false).writeFile(targetDir);
        }
    }

    @Test
    public void arithmeticMutationsTest() throws BadBytecode, CannotCompileException, IOException, NotFoundException {
        System.out.println("MutateTests.arithmeticMutationsTest");
        for(CtClass ctClass : translator.getCtClasses()){
            ctClass.defrost();
            Mutators.arithmeticMutations(ctClass).writeFile(targetDir);
        }
    }
}
