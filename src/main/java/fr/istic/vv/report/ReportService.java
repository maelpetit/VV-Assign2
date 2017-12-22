package fr.istic.vv.report;

import java.io.InputStream;

/**
 * Report Service aggregates all reports generate during mutation testing
 */
public interface ReportService {

    /**
     * Adds a report to the report service
     *
     * @param report
     */
    public void addReport(Report report,String inputStream);

    void generateHTML(InputStream inputStream);

    void generateCSV(String addString);

    void startMutationTesting();

    void stopMutationTesting();

    void setProjectName(String projectName);
}