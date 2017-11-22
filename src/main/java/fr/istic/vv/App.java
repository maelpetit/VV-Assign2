package fr.istic.vv;

import javassist.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class App 
{
    /*CtClass pointClass = pool.makeClass("Point");
            CtField xField = CtField.make("public int x;", pointClass);
            CtField yField = CtField.make("public int y;", pointClass);
            pointClass.addField(xField);
            pointClass.addField(yField);
            pointClass.addMethod(CtNewMethod.getter("getX", xField));
            pointClass.addMethod(CtNewMethod.getter("getY", yField));
            pointClass.addMethod(CtNewMethod.make(
                    "public String toString() { return String.format(\"%d-%d\",new Object[]{x,y});}",
                    pointClass));
            pointClass.writeFile();*/

    public static void main(String[] args )
    {
        try {
            ClassPool pool = ClassPool.getDefault();

            Loader loader = new Loader(pool);
            Translator translator = new MyTranslator();
            File classDir = new File("TargetProject/target/classes");
            File testDir = new File("TargetProject/target/test-classes");
            loader.addTranslator(pool, translator);
            pool.appendClassPath(classDir.getPath());
            pool.appendClassPath(testDir.getPath());
            loader.run("fr.istic.vv.TargetApp", args);

            JUnitCore jUnitCore= new JUnitCore();
            String[] classes = {"fr.istic.vv.AdditionTest",
                    "fr.istic.vv.MultiplicationTest",
                    "fr.istic.vv.DivisionTest",
                    "fr.istic.vv.SubtractionTest"};
            for(CtClass ctClass : pool.get(classes)){
                System.out.println("test: "+ ctClass.getName());
                Request request = Request.aClass(ctClass.toClass());
                Result r = jUnitCore.run(request);
                System.out.println("Tests ran : " + r.getRunCount() + ", failed : " + r.getFailureCount());
            }

        }

        catch(Throwable exc) {
            System.out.println("Oh, no! Something went wrong.");
            System.out.println(exc.getMessage());
            exc.printStackTrace();
        }
    }
}
