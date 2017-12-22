package fr.istic.vv;

import fr.istic.vv.log.FileLog;
import fr.istic.vv.report.Report;
import fr.istic.vv.report.ReportService;
import fr.istic.vv.report.ReportServiceImpl;
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
        String[] projectPath = targetProjectDir.split("/");
        new File("logs/" + projectPath[projectPath.length-1] + ".csv").delete();
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
    public void run() throws NotFoundException, CannotCompileException, IOException, InterruptedException {
        if(Mutators.HAS_MUTATED){
            JavaProcess.exec(TestRunner.class, targetProjectDir, currentMutation);
        }
        else{
            String[] projectPath = targetProjectDir.split("/");
            FileLog.writeLog( projectPath[projectPath.length-1] , currentMutation + ";" + "false" + ";" + "true" + ";\n") ;
        }
        Mutators.deleteTargetClasses(classes, classDir);
    }

    @Test
    public void replaceReturnInDoubleMethods() throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException {
        logger.info("Mutateur utilisé : MutateTests.replaceReturnInDoubleMethods");
        currentMutation = "replaceReturnInDoubleMethods" ;
        for(CtClass ctClass : classes){
            ctClass.defrost();
            Mutators.replaceReturnInDoubleMethods(ctClass).writeFile(classDir.getPath());
        }
    }

    @Test
    public void setBooleanMethodsToTrue() throws NotFoundException, CannotCompileException, IOException {
        logger.info("Mutateur utilisé : MutateTests.setBooleanMethodsToTrue");
        currentMutation = "SetBooleanMethodsToTrue" ;
        for(CtClass ctClass : classes){
            ctClass.defrost();
            Mutators.setBooleanMethodsTo(ctClass, true).writeFile(classDir.getPath());
        }
    }

    @Test
    public void setBooleanMethodsToFalse() throws NotFoundException, CannotCompileException, IOException {
        logger.info("Mutateur utilisé : MutateTests.setBooleanMethodsToFalse");
        currentMutation = "setBooleanMethodsToFalse" ;
        for(CtClass ctClass : classes){
            ctClass.defrost();
            Mutators.setBooleanMethodsTo(ctClass, false).writeFile(classDir.getPath());
        }
    }

    @Test
    public void addToSub(){
        logger.info("Mutateur utilisé : MutateTests.addToSub");
        currentMutation = "addToSub" ;
        replaceInClasses(Opcode.DADD, Opcode.DSUB);
    }

    @Test
    public void subToAdd(){
        logger.info("MutateTests.subToAdd");
        currentMutation = "subToAdd" ;
        replaceInClasses(Opcode.DSUB, Opcode.DADD);
    }

    @Test
    public void mulToDiv(){
        logger.info("MutateTests.mulToDiv");
        currentMutation = "mulToDiv" ;
        replaceInClasses(Opcode.DMUL, Opcode.DDIV);
    }

    @Test
    public void divToMul(){
        logger.info("MutateTests.divToMul");
        currentMutation = "divToMul" ;
        replaceInClasses(Opcode.DDIV, Opcode.DMUL);
    }

    @Test
    public void greaterToLower(){
        logger.info("MutateTests.greaterToLower");
        currentMutation = "greaterToLower" ;
        replaceInClasses( Opcode.DCMPG, Opcode.DCMPL);
    }

    @Test
    public void lowerToGreater(){
        logger.info("MutateTests.lowerToGreater");
        currentMutation = "lowerToGreater" ;
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
