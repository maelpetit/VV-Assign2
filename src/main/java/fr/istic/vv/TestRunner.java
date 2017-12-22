package fr.istic.vv;

import fr.istic.vv.log.FileLog;
import fr.istic.vv.report.ReportServiceImpl;
import javassist.*;
import org.apache.maven.shared.invoker.*;
import org.junit.runner.JUnitCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import fr.istic.vv.report.Report;
import fr.istic.vv.report.ReportService;

public class TestRunner {

    private ClassPool pool;
    private Set<Class> testClasses = new HashSet<Class>();
    private String projectDir;
    private boolean allTestPassed;
    private String mutation;
    private ReportService reportService = new ReportServiceImpl();
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

    private void runTests() throws NotFoundException, CannotCompileException, MavenInvocationException, IOException {

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( projectDir + "/pom.xml" ) );
        request.setGoals( Collections.singletonList( "surefire:test" ) );

        Invoker invoker = new DefaultInvoker();
        InvocationResult result = invoker.execute( request );
        allTestPassed = result.getExitCode() == 0;
        if(allTestPassed ){
            logger.info("Tous les tests sont passés avec succès");
        }
        else{
            logger.info("Erreur dans les tests, voir details au dessus");
        }
        String fichiercsv = mutation + ";" + "true" + ";" + allTestPassed + ";\n" ;
        reportService.addReport(new Report(allTestPassed), fichiercsv );
        reportService.generateCSV(fichiercsv);
        //br.close();
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
