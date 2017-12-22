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
    static String csvFile;

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
        csvFile = "log/" + projectPath[projectPath.length-1] + ".csv";
        new File(csvFile).delete();
    }

    @AfterClass
    public static void afterClass(){
        HTMLGenerator.genFile(csvFile);
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
        MavenUtil.execGoals("compile", targetProjectDir, false);
        Mutators.HAS_MUTATED = false;
    }

    @After
    public void run() {
        String[] projectPath = targetProjectDir.split("/");
        if(Mutators.HAS_MUTATED){
            try {
                JavaProcess.exec(TestRunner.class, targetProjectDir, currentMutation);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            FileLog.writeLog( projectPath[projectPath.length-1] , currentMutation + ";" + "false" + ";" + "true" + ";\n") ;
        }
        Mutators.deleteTargetClasses(classes, classDir);
    }

    @Test
    public void replaceReturnInDoubleMethods(){
        logger.info("Mutateur utilisé : MutateTests.replaceReturnInDoubleMethods");
        currentMutation = "body of boolean methods to 'return false'" ;
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
        currentMutation = "body of boolean methods to 'return true'" ;
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
        currentMutation = "body of boolean methods to 'return false'" ;
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
    public void addToSubD(){
        logger.info("Mutateur utilisé : MutateTests.addToSubD");
        currentMutation = "(double) '+' is replaced by '-'" ;
        replaceInClasses(Opcode.DADD, Opcode.DSUB);
    }

    @Test
    public void subToAddD(){
        logger.info("MutateTests.subToAddD");
        currentMutation = "(double) '-' is replaced by '+'" ;
        replaceInClasses(Opcode.DSUB, Opcode.DADD);
    }

    @Test
    public void mulToDivD(){
        logger.info("MutateTests.mulToDivD");
        currentMutation = "(double) '*' is replaced by '/'" ;
        replaceInClasses(Opcode.DMUL, Opcode.DDIV);
    }

    @Test
    public void divToMulD(){
        logger.info("MutateTests.divToMulD");
        currentMutation = "(double) '/' is replaced by '*'" ;
        replaceInClasses(Opcode.DDIV, Opcode.DMUL);
    }

    @Test
    public void greaterToLowerD(){
        logger.info("MutateTests.greaterToLowerD");
        currentMutation = "(double) '>' is replaced by '<'" ;
        replaceInClasses( Opcode.DCMPG, Opcode.DCMPL);
    }

    @Test
    public void lowerToGreaterD(){
        logger.info("MutateTests.lowerToGreater");
        currentMutation = "(double) '<' is replaced by '>'" ;
        replaceInClasses(Opcode.DCMPL, Opcode.DCMPG);
    }

    @Test
    public void addToSubF(){
        logger.info("Mutateur utilisé : MutateTests.addToSub");
        currentMutation = "(float) '+' is replaced by '-'" ;
        replaceInClasses(Opcode.FADD, Opcode.FSUB);
    }

    @Test
    public void subToAddF(){
        logger.info("MutateTests.subToAdd");
        currentMutation = "(float) '-' is replaced by '+'" ;
        replaceInClasses(Opcode.FSUB, Opcode.FADD);
    }

    @Test
    public void mulToDivF(){
        logger.info("MutateTests.mulToDiv");
        currentMutation = "(float) '*' is replaced by '/'" ;
        replaceInClasses(Opcode.FMUL, Opcode.FDIV);
    }

    @Test
    public void divToMulF(){
        logger.info("MutateTests.divToMul");
        currentMutation = "(float) '/' is replaced by '*'" ;
        replaceInClasses(Opcode.FDIV, Opcode.FMUL);
    }

    @Test
    public void greaterToLowerF(){
        logger.info("MutateTests.greaterToLower");
        currentMutation = "(float) '>' is replaced by '<'" ;
        replaceInClasses( Opcode.FCMPG, Opcode.FCMPL);
    }

    @Test
    public void lowerToGreaterF(){
        logger.info("MutateTests.lowerToGreater");
        currentMutation = "(float) '<' is replaced by '>'" ;
        replaceInClasses(Opcode.FCMPL, Opcode.FCMPG);
    }

    @Test
    public void addToSubI(){
        logger.info("Mutateur utilisé : MutateTests.addToSub");
        currentMutation = "(integer) '+' is replaced by '-'" ;
        replaceInClasses(Opcode.IADD, Opcode.ISUB);
    }

    @Test
    public void subToAddI(){
        logger.info("MutateTests.subToAdd");
        currentMutation = "(integer) '-' is replaced by '+'" ;
        replaceInClasses(Opcode.ISUB, Opcode.IADD);
    }

    @Test
    public void mulToDivI(){
        logger.info("MutateTests.mulToDiv");
        currentMutation = "(integer) '*' is replaced by '/'" ;
        replaceInClasses(Opcode.IMUL, Opcode.IDIV);
    }

    @Test
    public void divToMulI(){
        logger.info("MutateTests.divToMul");
        currentMutation = "(integer) '/' is replaced by '*'" ;
        replaceInClasses(Opcode.IDIV, Opcode.IMUL);
    }

    @Test
    public void addToSubL(){
        logger.info("Mutateur utilisé : MutateTests.addToSub");
        currentMutation = "(long) '+' is replaced by '-'" ;
        replaceInClasses(Opcode.LADD, Opcode.LSUB);
    }

    @Test
    public void subToAddL(){
        logger.info("MutateTests.subToAdd");
        currentMutation = "(long) '-' is replaced by '+'" ;
        replaceInClasses(Opcode.LSUB, Opcode.LADD);
    }

    @Test
    public void mulToDivL(){
        logger.info("MutateTests.mulToDiv");
        currentMutation = "(long) '*' is replaced by '/'" ;
        replaceInClasses(Opcode.LMUL, Opcode.LDIV);
    }

    @Test
    public void divToMulL(){
        logger.info("MutateTests.divToMul");
        currentMutation = "(long) '/' is replaced by '*'" ;
        replaceInClasses(Opcode.LDIV, Opcode.LMUL);
    }

    @Test
    public void ifEqToIfNe(){
        logger.info("MutateTests.ifEqToIfNe");
        currentMutation = "'if==' is replaced by 'if!='" ;
        replaceInClasses(Opcode.IFEQ, Opcode.IFNE);
    }

    @Test
    public void ifNeToIfEq(){
        logger.info("MutateTests.ifNeToIfEq");
        currentMutation = "'if!=' is replaced by 'if=='" ;
        replaceInClasses(Opcode.IFNE, Opcode.IFEQ);
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
