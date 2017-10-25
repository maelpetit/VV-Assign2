package fr.istic.vv;

import javassist.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.io.File;

public class App 
{
    public static void main(String[] args )
    {
        try {
            //TODO: Your code goes here
            ClassPool pool = ClassPool.getDefault();
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

            Loader loader = new Loader(pool);
            Translator logger = new Translator() {
                public void start(ClassPool classPool) throws NotFoundException, CannotCompileException {
                    System.out.println("Starting");
                }

                public void onLoad(ClassPool classPool, String className) throws NotFoundException, CannotCompileException {
                    System.out.println(className);
                }
            };
            File classDir = new File("../TargetProject/target/classes");
            System.out.println(classDir.exists());
            System.out.println(classDir.getAbsolutePath());
            loader.addTranslator(pool, logger);
            pool.appendClassPath(classDir.getPath());
            loader.run("fr.istic.vv.TargetApp", args);
            JUnitCore jUnitCore= new JUnitCore();
            Result r = jUnitCore.run();
        }

        catch(Throwable exc) {
            System.out.println("Oh, no! Something went wrong.");
            System.out.println(exc.getMessage());
            exc.printStackTrace();
        }

    }
}
