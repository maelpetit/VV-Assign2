package fr.istic.vv;

import fr.istic.vv.log.FileLog;
import javassist.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

            String[] _testClasses = findTestClasses(testDir, "").toArray(new String[0]);

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
//            FileLog.log("test: "+ testClass.getName());
            System.out.println("test: "+ testClass.getName());
            Result r = jUnitCore.run(testClass);
//            FileLog.log("Tests ran : " + r.getRunCount() + ", failed : " + r.getFailureCount());
            System.out.println("Tests ran : " + r.getRunCount() + ", failed : " + r.getFailureCount());
        }
    }

    private List<String> findTestClasses(File testDir, String pkg){
        List<String> res = new ArrayList<>();
        for(File file : testDir.listFiles()){
            if(file.isFile()){
                String fileName = file.getName();
                res.add(pkg + fileName.substring(0, fileName.length() - 6));
            }else if(file.isDirectory()){
                res.addAll(findTestClasses(file, pkg + file.getName() + "."));
            }
        }
        return res;
    }

    public static void main(String[] args) throws Throwable {
        String projectDir ="E:/Documents/M2/V&V/TargetProject";
        if(args.length > 1){
            projectDir = args[0];
        }

        TestRunner testRunner = new TestRunner(projectDir);
        testRunner.runTests();

//        FileLog.writeLog("TestRunner");
    }



}
