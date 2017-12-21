package fr.istic.vv;

public class Main {

    public static void main(String[] args){
//        System.setProperty("maven.home", "/home/mael/Applications/apache-maven-3.5.0");
//        System.out.println(System.getProperty("maven.home"));
        System.out.println("Main.main");
        for(String arg : args){
            System.out.println(arg);
        }
    }
}
