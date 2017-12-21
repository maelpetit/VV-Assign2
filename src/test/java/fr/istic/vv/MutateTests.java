package fr.istic.vv;

import fr.istic.vv.log.FileLog;
import org.apache.maven.shared.invoker.*;
import org.junit.*;
import javassist.*;
import javassist.bytecode.BadBytecode;
import org.junit.BeforeClass;
import org.junit.runner.JUnitCore;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MutateTests {

    static ClassPool pool;
    static Loader loader;
    static Set<CtClass> classes = new HashSet<>();
    static String targetDir = "target/classes";
    static String targetProjectDir = "/home/mael/M2/VetV/TargetProject";
    static File classDir;

    @BeforeClass
    public static void initClass() throws Throwable {
        System.setProperty("maven.home", "/home/mael/Applications/apache-maven-3.5.0");
        pool = ClassPool.getDefault();
        loader = new Loader(pool);
        classDir = new File(targetProjectDir + "/target/classes");
        File testDir = new File(targetProjectDir + "/target/test-classes");

        pool.appendClassPath(classDir.getPath());
        pool.appendClassPath(testDir.getPath());
        classes = findClasses(classDir, "");
    }

    private static Set<CtClass> findClasses(File dir, String pkg) {
        Set<CtClass> res = new HashSet<>();
        for(File file : dir.listFiles()){
            if(file.isFile() && file.getName().endsWith(".class")){
                String fileName = file.getName();
                String className = pkg + fileName.substring(0, fileName.length() - 6);
                try {
                    res.add(pool.get(className));
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }else if(file.isDirectory()){
                res.addAll(findClasses(file, pkg + file.getName() + "."));
            }
        }
        return res;
    }

    @Before
    public void recompile(){
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( targetProjectDir + "/pom.xml" ) );
        request.setGoals( Collections.singletonList( "compile" ) );

        Invoker invoker = new DefaultInvoker();
        try {
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }
    }

    @After
    public void run() throws NotFoundException, CannotCompileException, IOException, InterruptedException {
        JavaProcess.exec(TestRunner.class, targetProjectDir);
        Mutators.deleteTargetClasses(classes, classDir);
    }

    @Test
    public void replaceReturnInDoubleMethodsTest() throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException {
        FileLog.log("MutateTests.replaceReturnInDoubleMethodsTest");
        for(CtClass ctClass : classes){
            ctClass.defrost();
            Mutators.replaceReturnInDoubleMethods(ctClass).writeFile(classDir.getPath());
        }
    }

    @Test
    public void setBooleanMethodsToTrue() throws NotFoundException, CannotCompileException, IOException {
        FileLog.log("MutateTests.setBooleanMethodsToTrue");
        for(CtClass ctClass : classes){
            System.out.println(ctClass.getName());
            ctClass.defrost();
            Mutators.setBooleanMethodsTo(ctClass, true).writeFile(classDir.getPath());
        }
    }

    @Test
    public void setBooleanMethodsToFalse() throws NotFoundException, CannotCompileException, IOException {
        FileLog.log("MutateTests.setBooleanMethodsToFalse");
        for(CtClass ctClass : classes){
            ctClass.defrost();
            Mutators.setBooleanMethodsTo(ctClass, false).writeFile(classDir.getPath());
        }
    }

    @Test
    public void arithmeticMutationsTest() throws BadBytecode, CannotCompileException, IOException, NotFoundException {
        FileLog.log("MutateTests.arithmeticMutationsTest");
        for(CtClass ctClass : classes){
            ctClass.defrost();
            Mutators.arithmeticMutations(ctClass).writeFile(classDir.getPath());
        }
    }
}
