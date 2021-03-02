package de.viadee.mateoorchestrator.models;

import de.viadee.mateoorchestrator.enums.JobPriority;
import de.viadee.mateoorchestrator.enums.JobStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Marcel_Flasskamp
 * A Job has an uuid, scriptName, status and reportDto.
 */
@Entity
@Table(name = "jobs")
public class JobEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String scriptName;

    @ElementCollection
    @CollectionTable(name = "input_variable_mapping",
            joinColumns = { @JoinColumn(name = "jobs_id", referencedColumnName = "id") })
    @MapKeyColumn(name = "variable_key")
    @Column(name = "variable_value")
    private Map<String, String> inputVariables;

    @ElementCollection
    @CollectionTable(name = "output_variable_mapping",
            joinColumns = { @JoinColumn(name = "jobs_id", referencedColumnName = "id") })
    @MapKeyColumn(name = "variable_key")
    @Column(name = "variable_value")
    private Map<String, String> outputVariables;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Enumerated(EnumType.STRING)
    private JobPriority priority;

    private Integer priorityValue;

    @Column
    private Integer askedTimes;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;

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

    public JobEntity() {
        super();
    }

    public JobEntity(String scriptName, JobPriority priority, Map<String, String> inputVariables,
            List<String> outputVariables) {
        this.scriptName = URLDecoder.decode(scriptName, StandardCharsets.UTF_8);
        this.priority = (priority != null) ? priority : JobPriority.DEFAULT;
        this.inputVariables = inputVariables;
        this.outputVariables = outputVariables.stream().collect(Collectors.toMap(x -> x, x -> ""));
        this.status = JobStatus.QUEUED;
        this.priorityValue = (priority != null) ? priority.getValue() : JobPriority.DEFAULT.getValue();
        this.askedTimes = 0;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getPriorityValue() {
        return priority.getValue();
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public Map<String, String> getInputVariables() {
        return inputVariables;
    }

    public void setInputVariables(Map<String, String> inputVariables) {
        this.inputVariables = inputVariables;
    }

    public Map<String, String> getOutputVariables() {
        return outputVariables;
    }

    public void setOutputVariables(Map<String, String> outputVariables) {
        this.outputVariables = outputVariables;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public JobPriority getPriority() {
        return priority;
    }

    public void setPriority(JobPriority priority) {
        this.priority = priority;
        this.priorityValue = priority.getValue();
    }

    public Integer getAskedTimes() {
        return askedTimes;
    }

    public void setAskedTimes(Integer askedTimes) {
        this.askedTimes = askedTimes;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

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

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        JobEntity jobEntity = (JobEntity) o;

        return new EqualsBuilder().append(id, jobEntity.id)
                .append(scriptName, jobEntity.scriptName).append(inputVariables, jobEntity.inputVariables)
                .append(outputVariables, jobEntity.outputVariables).append(status, jobEntity.status)
                .append(priority, jobEntity.priority).append(askedTimes, jobEntity.askedTimes)
                .append(createDate, jobEntity.createDate).append(modifyDate, jobEntity.modifyDate)
                .append(filePath, jobEntity.filePath).append(filename, jobEntity.filename)
                .append(testSetName, jobEntity.testSetName).append(resultLevel, jobEntity.resultLevel)
                .append(resultString, jobEntity.resultString).append(startTime, jobEntity.startTime)
                .append(originFileName, jobEntity.originFileName).append(runIndex, jobEntity.runIndex)
                .append(vtfVersion, jobEntity.vtfVersion).append(mateoInstanz, jobEntity.mateoInstanz).isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(scriptName).append(inputVariables).append(outputVariables)
                .append(status).append(priority).append(askedTimes).append(createDate).append(modifyDate)
                .append(filePath)
                .append(filename).append(testSetName).append(resultLevel).append(resultString).append(startTime)
                .append(originFileName).append(runIndex).append(vtfVersion).append(mateoInstanz).toHashCode();
    }
}
