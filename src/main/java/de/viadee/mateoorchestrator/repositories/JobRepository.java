package de.viadee.mateoorchestrator.repositories;

import de.viadee.mateoorchestrator.enums.JobStatus;
import de.viadee.mateoorchestrator.models.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author Marcel_Flasskamp
 */
@Repository
public interface JobRepository extends JpaRepository<JobEntity, UUID> {

    @Query(value = "SELECT * FROM jobs j WHERE j.status = 'QUEUED' ORDER BY j.priority_value DESC, j.asked_times DESC, j.create_date  LIMIT 1",
            nativeQuery = true)
    JobEntity findQueuedJobSorted();

    @Query(value = "SELECT * FROM jobs j WHERE j.status = 'RUNNING' ORDER BY j.priority_value DESC, j.asked_times DESC, j.create_date ",
            nativeQuery = true)
    List<JobEntity> findAllRunningJobsSorted();

    List<JobEntity> findAllByStatus(JobStatus status);

}
