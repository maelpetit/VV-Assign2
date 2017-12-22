package fr.istic.vv;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private static PropertiesLoader instance = new PropertiesLoader();

    Properties prop = new Properties();
    InputStream input = null;

    private PropertiesLoader() {
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private static String getProperty(String property){
        return instance.prop.getProperty(property);
    }

    public static String getMavenHome(){
        return getProperty("maven-home");
    }

    public static String getTargetProject(){
        return getProperty("target-project");
    }
}
