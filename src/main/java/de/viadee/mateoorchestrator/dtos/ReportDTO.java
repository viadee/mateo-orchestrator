package de.viadee.mateoorchestrator.dtos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcel_Flasskamp
 */
public class ReportDTO implements Serializable {

    private String filePath;

    private String filename;

    private String testSetName;

    private String resultLevel;

    private String resultString;

    private String startTime;

    private String originFileName;

    private String runIndex;

    private String vtfVersion;

    private String mateoInstanz;

    private Map<String, String> outputVariables = new HashMap<>();

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getTestSetName() {
        return testSetName;
    }

    public void setTestSetName(String testSetName) {
        this.testSetName = testSetName;
    }

    public String getResultLevel() {
        return resultLevel;
    }

    public void setResultLevel(String resultLevel) {
        this.resultLevel = resultLevel;
    }

    public String getResultString() {
        return resultString;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getOriginFileName() {
        return originFileName;
    }

    public void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
    }

    public String getRunIndex() {
        return runIndex;
    }

    public void setRunIndex(String runIndex) {
        this.runIndex = runIndex;
    }

    public String getVtfVersion() {
        return vtfVersion;
    }

    public void setVtfVersion(String vtfVersion) {
        this.vtfVersion = vtfVersion;
    }

    public String getMateoInstanz() {
        return mateoInstanz;
    }

    public void setMateoInstanz(String mateoInstanz) {
        this.mateoInstanz = mateoInstanz;
    }

    public Map<String, String> getOutputVariables() {
        return outputVariables;
    }

    public void setOutputVariables(Map<String, String> outputVariables) {
        this.outputVariables = outputVariables;
    }

    @Override
    public String toString() {
        return "ReportDTO{" +
                "filePath='" + filePath + '\'' +
                ", filename='" + filename + '\'' +
                ", testSetName='" + testSetName + '\'' +
                ", resultLevel='" + resultLevel + '\'' +
                ", resultString='" + resultString + '\'' +
                ", startTime='" + startTime + '\'' +
                ", originFileName='" + originFileName + '\'' +
                ", runIndex='" + runIndex + '\'' +
                ", vtfVersion='" + vtfVersion + '\'' +
                ", mateoInstanz='" + mateoInstanz + '\'' +
                ", outputVariables=" + outputVariables.toString() +
                '}';
    }
}
