package fr.istic.vv;

import fr.istic.vv.log.FileLog;
import javassist.bytecode.Opcode;
import org.apache.maven.shared.invoker.*;
import org.junit.*;
import javassist.*;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
        String[] projectPath = targetProjectDir.split("/");
        new File("log/" + projectPath[projectPath.length-1] + ".csv").delete();
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
        try {
            MavenUtil.execGoals("compile", targetProjectDir);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }
        Mutators.HAS_MUTATED = false;
    }

    @After
    public void run() {
        if(Mutators.HAS_MUTATED){
            try {
                JavaProcess.exec(TestRunner.class, targetProjectDir, currentMutation);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            String[] projectPath = targetProjectDir.split("/");
            FileLog.writeLog( projectPath[projectPath.length-1] , currentMutation + ";" + "false" + ";" + "true" + ";\n") ;
        }
        Mutators.deleteTargetClasses(classes, classDir);
    }

    @Test
    public void replaceReturnInDoubleMethods(){
        logger.info("Mutateur utilisé : MutateTests.replaceReturnInDoubleMethods");
        currentMutation = "replace Double Methods" ;
        for(CtClass ctClass : classes){
            ctClass.defrost();
            try {
                Mutators.replaceReturnInDoubleMethods(ctClass).writeFile(classDir.getPath());
            } catch (CannotCompileException | IOException | ClassNotFoundException | NotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void setBooleanMethodsToTrue(){
        logger.info("Mutateur utilisé : MutateTests.setBooleanMethodsToTrue");
        currentMutation = "Boolean Methods -> return true" ;
        for(CtClass ctClass : classes){
            ctClass.defrost();
            try {
                Mutators.setBooleanMethodsTo(ctClass, true).writeFile(classDir.getPath());
            } catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void setBooleanMethodsToFalse(){
        logger.info("Mutateur utilisé : MutateTests.setBooleanMethodsToFalse");
        currentMutation = "Boolean Methods -> return false" ;
        for(CtClass ctClass : classes){
            ctClass.defrost();
            try {
                Mutators.setBooleanMethodsTo(ctClass, false).writeFile(classDir.getPath());
            } catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void addToSub(){
        logger.info("Mutateur utilisé : MutateTests.addToSub");
        currentMutation = "'+' is replaced by '-'" ;
        replaceInClasses(Opcode.DADD, Opcode.DSUB);
    }

    @Test
    public void subToAdd(){
        logger.info("MutateTests.subToAdd");
        currentMutation = "'-' is replaced by '+'" ;
        replaceInClasses(Opcode.DSUB, Opcode.DADD);
    }

    @Test
    public void mulToDiv(){
        logger.info("MutateTests.mulToDiv");
        currentMutation = "'*' is replaced by '/'" ;
        replaceInClasses(Opcode.DMUL, Opcode.DDIV);
    }

    @Test
    public void divToMul(){
        logger.info("MutateTests.divToMul");
        currentMutation = "'/' is replaced by '*'" ;
        replaceInClasses(Opcode.DDIV, Opcode.DMUL);
    }

    @Test
    public void greaterToLower(){
        logger.info("MutateTests.greaterToLower");
        currentMutation = "'>' is replaced by '<'" ;
        replaceInClasses( Opcode.DCMPG, Opcode.DCMPL);
    }

    @Test
    public void lowerToGreater(){
        logger.info("MutateTests.lowerToGreater");
        currentMutation = "'<' is replaced by '>'" ;
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

    @Test
    public void removeBodyInVoidMethods(){
        logger.info("Mutateur utilisé : MutateTests.removeBodyInVoidMethods");
        currentMutation = "removed body of void methods" ;
        for(CtClass ctClass : classes){
            ctClass.defrost();
            try {
                Mutators.removeBodyInVoidMethods(ctClass).writeFile(classDir.getPath());
            } catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
