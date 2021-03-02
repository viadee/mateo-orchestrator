package de.viadee.mateoorchestrator.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Marcel_Flasskamp
 */
public enum JobPriority {

    HIGH("HIGH", 10),
    MEDIUM("MEDIUM", 5),
    URGENT("URGENT", 16),
    LOW("LOW", 2),
    DEFAULT("DEFAULT", 0);

    private final String name;

    private final int value;

    JobPriority(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int compare(JobPriority that) {
        return Integer.compare(this.value, that.value);
    }

    public String toStringGerman() {
        return switch (getName()) {
            case "Urgent" -> "Dringend";
            case "High" -> "Hoch";
            case "Medium" -> "Mittel";
            case "Low" -> "Gering";
            default  -> "Standard";
        };
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * get JobStatus enum from given name (german or english)
     *
     * @param germanNameOrEnglishName jobstatus value to transform
     * @return transformed value as jobstatus object
     */
    public static JobPriority getByNameOrEnglishName(String germanNameOrEnglishName) {
        Optional<JobPriority> resultLevel = Arrays.stream(JobPriority.values())
                .filter(rl -> rl.toString().equals(germanNameOrEnglishName) || rl.toStringGerman().equals(germanNameOrEnglishName))
                .findFirst();
        if (resultLevel.isPresent()) {
            return resultLevel.get();
        }
        throw new IllegalArgumentException(
                String.format("No JobStatus for entry German Name or English Name '%s' found!", germanNameOrEnglishName));
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
