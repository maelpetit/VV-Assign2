package fr.istic.vv;

import fr.istic.vv.log.FileLog;
import javafx.util.Pair;
import javassist.bytecode.Opcode;
import org.apache.maven.shared.invoker.*;
import org.junit.*;
import javassist.*;
import javassist.bytecode.BadBytecode;
import org.junit.BeforeClass;
import org.junit.runner.JUnitCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MutateTests {

    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);
    static ClassPool pool;
    static Loader loader;
    static Set<CtClass> classes = new HashSet<>();
    static String targetProjectDir = PropertiesLoader.getTargetProject();
    static File classDir;
    static String currentMutation;

    @BeforeClass
    public static void initClass() throws Throwable {
        System.setProperty("maven.home", PropertiesLoader.getMavenHome());
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

        Mutators.HAS_MUTATED = false;
    }

    @After
    public void run() throws NotFoundException, CannotCompileException, IOException, InterruptedException {
        if(Mutators.HAS_MUTATED){
            JavaProcess.exec(TestRunner.class, targetProjectDir, currentMutation);
        }
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
        logger.info("Mutateur utilisé : MutateTests.setBooleanMethodsToTrue");
        for(CtClass ctClass : classes){
            ctClass.defrost();
            Mutators.setBooleanMethodsTo(ctClass, true).writeFile(classDir.getPath());
        }
    }

    @Test
    public void setBooleanMethodsToFalse() throws NotFoundException, CannotCompileException, IOException {
        logger.info("Mutateur utilisé : MutateTests.setBooleanMethodsToFalse");
        for(CtClass ctClass : classes){
            ctClass.defrost();
            Mutators.setBooleanMethodsTo(ctClass, false).writeFile(classDir.getPath());
        }
    }

    @Test
    public void addToSub(){
        replaceInClasses(Opcode.DADD, Opcode.DSUB);
    }

    @Test
    public void subToAdd(){
        replaceInClasses(Opcode.DSUB, Opcode.DADD);
    }

    @Test
    public void mulToDiv(){
        replaceInClasses(Opcode.DMUL, Opcode.DDIV);
    }

    @Test
    public void divToMul(){
        replaceInClasses(Opcode.DDIV, Opcode.DMUL);
    }

    @Test
    public void greaterToLower(){
        replaceInClasses( Opcode.DCMPG, Opcode.DCMPL);
    }

    @Test
    public void lowerToGreater(){
        replaceInClasses(Opcode.DCMPL, Opcode.DCMPG);
    }

    private void replaceInClasses(int oldByteCode, int newByteCode){
        for (CtClass ctClass : classes) {
            ctClass.defrost();
            try {
                Mutators.replace(ctClass, oldByteCode, newByteCode).writeFile(classDir.getPath());
            } catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
