package fr.istic.vv;

import java.io.*;

public class HTMLGenerator {

    public static void genFile(String csvFile){
        String[] filePath = csvFile.split("/");
        String projectName = filePath[filePath.length - 1];
        projectName = projectName.substring(0, projectName.length() - 4);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("log/" + projectName + ".html"));
            bw.write(genString(csvFile, projectName));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String genString(String csvFile, String projectName){
        String header = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table {\n" +
                "    font-family: arial, sans-serif;\n" +
                "    border-collapse: collapse;\n" +
                "    width: 100%;\n" +
                "}\n" +
                "\n" +
                "td, th {\n" +
                "    border: 1px solid #dddddd;\n" +
                "    text-align: left;\n" +
                "    padding: 8px;\n" +
                "}\n" +
                "\n" +
                "tr:nth-child(even) {\n" +
                "    background-color: #dddddd;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n";
        String body = "<body>\n" +
                "<h1>" +
                projectName +
                "</h1>" +
                "<table>\n" +
                "  <tr>\n" +
                "    <th>Mutation</th>\n" +
                "    <th>Has Mutated</th>\n" +
                "    <th>Is Alive</th>\n" +
                "  </tr>\n" +
                generateRows(csvFile) +
                "</table>\n" +
                "\n" +
                "</body>\n" +
                "</html>"
                ;

        return header + body;
    }

    private static String generateRows(String csvFile) {
        String rows = "";
        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(new File(csvFile)));
            while ((line = br.readLine()) != null) {
                String[] splitLine =  line.split(";");
                boolean modified = splitLine[1].equals("true");
                boolean alive = splitLine[1].equals("true") && splitLine[2].equals("false");
                rows += "<tr>\n" + "<th>" + splitLine[0] + "</th>\n" +
                        "<th>" + (modified ? "<font color=\"green\">" + splitLine[1] + "</font>" : splitLine[1]) + "</th>\n" +
                        "<th>" + (alive ? "<font color=\"green\">" + splitLine[2] + "</font>" : splitLine[2]) + "</th>\n" + "</tr>\n";
            }
            br.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return rows + "</tr>\n";
    }
}
