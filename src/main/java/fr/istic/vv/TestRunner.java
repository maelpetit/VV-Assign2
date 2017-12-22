package fr.istic.vv;

import fr.istic.vv.log.FileLog;
import javassist.*;
import org.apache.maven.shared.invoker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class TestRunner {

    private ClassPool pool;
    private Set<Class> testClasses = new HashSet<Class>();
    private String projectDir;
    private int allTestPassed; //0 = all tests passed, 1 = some failed, -1 = infinite loop
    private String mutation;
    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    private TestRunner(String projectPath, String mutation){
        projectDir = projectPath;
        this.mutation = mutation;
        pool = ClassPool.getDefault();
        File classDir = new File(projectPath + "/target/classes");
        File testDir = new File(projectPath + "/target/test-classes");
        try {
            pool.appendClassPath(classDir.getPath());
            pool.appendClassPath(testDir.getPath());

            String[] _testClasses = findTestClasses(testDir, "").toArray(new String[0]);

            for(CtClass ctClass : pool.get(_testClasses)){
                try {
                    testClasses.add(ctClass.toClass());
                }catch(CannotCompileException e){
                    e.printStackTrace();
                }
            }

        } catch (NotFoundException e) {
            e.printStackTrace();
        }


    }

    private void runTests() throws NotFoundException, CannotCompileException, MavenInvocationException {

        allTestPassed = MavenUtil.execGoals("surefire:test", projectDir, true);
        if(allTestPassed == 0){
            logger.info("Tous les tests sont passés avec succès");
        }
        else{
            logger.info("Erreur dans les tests, voir details au dessus");
        }
        String[] projectPath = projectDir.split("/");
        String result = allTestPassed == 0 ? "true" : allTestPassed > 0 ? "false" : "undefined";
        FileLog.writeLog( projectPath[projectPath.length-1]  ,  mutation + ";" + "true" + ";" + result + ";\n" ) ;

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

        if(args.length > 0){
            System.setProperty("maven.home", PropertiesLoader.getMavenHome());
            String[] arguments = args[0].split("%@%");
            String projectDir = arguments[0];
            String mutation = arguments[1];
            TestRunner testRunner = new TestRunner(projectDir, mutation);
            testRunner.runTests();
        }else {
            System.out.println("Missing argument");
        }
    }



}
