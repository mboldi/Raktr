package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.exception.ObjectConflictException;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.Project;
import hu.bsstudio.raktr.model.Rent;
import hu.bsstudio.raktr.model.User;
import hu.bsstudio.raktr.repository.ProjectRepository;
import hu.bsstudio.raktr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private final RentService rentService;

    public Optional<Project> create(final Project projectRequest) {
        Optional<User> foundProducer = userRepository.findByUsername(projectRequest.getProdManager().getUsername());

        if (foundProducer.isEmpty()) {
            log.error("User not found with given username: {}", foundProducer);
            return empty();
        }

        Optional<Project> foundProject = projectRepository.findById(projectRequest.getId());

        if (foundProject.isPresent()) {
            log.error("Project in database with given ID: {}", projectRequest);
            throw new ObjectConflictException();
        }

        projectRequest.setProdManager(foundProducer.get());
        projectRequest.setIsDeleted(false);

        Project saved = projectRepository.save(projectRequest);

        log.info("New project saved: {}", saved);

        return of(saved);
    }

    public Optional<Project> update(final Project projectUpdateRequest) {
        Optional<Project> projectToUpdate = projectRepository.findById(projectUpdateRequest.getId());
        Optional<User> foundProducer = userRepository.findByUsername(projectUpdateRequest.getProdManager().getUsername());

        if (projectToUpdate.isEmpty()) {
            log.error("Project not found with given ID: {}", projectToUpdate);
            return empty();
        }

        if (foundProducer.isEmpty()) {
            log.error("User not found with given username: {}", foundProducer);
            return empty();
        }

        projectToUpdate.get().setName(projectUpdateRequest.getName());
        projectToUpdate.get().setProdManager(foundProducer.get());
        projectToUpdate.get().setStartDate(projectUpdateRequest.getStartDate());
        projectToUpdate.get().setExpEndDate(projectUpdateRequest.getExpEndDate());

        Project saved = projectRepository.save(projectToUpdate.get());

        log.info("Project updated: {}", saved);

        return of(saved);
    }

    public List<Project> getAll() {
        List<Project> fetched = projectRepository.findAll();

        log.info("Found projects: {}", fetched);

        return fetched;
    }

    public List<Project> getAllDeleted() {
        List<Project> fetched = projectRepository.findAllDeleted();

        log.info("Found deleted projects: {}", fetched);

        return fetched;
    }

    public Optional<Project> getOne(final Long projectId) {
        Optional<Project> foundProject = projectRepository.findById(projectId);

        if (foundProject.isEmpty()) {
            log.error("Project not found with id: {}", projectId);
        }

        return foundProject;
    }

    public Optional<Project> delete(final Long projectId) {
        Optional<Project> toDelete = projectRepository.findById(projectId);

        if (toDelete.isEmpty()) {
            log.error("Project not found with id to delete: {}", projectId);
        } else {
            toDelete.get().setDeletedData();

            projectRepository.save(toDelete.get());
        }

        return toDelete;
    }

    public Optional<Project> unDelete(final Long projectId) {
        Optional<Project> toUnDelete = projectRepository.findById(projectId);

        if (toUnDelete.isEmpty()) {
            log.error("Project not found with id to restore: {}", projectId);
        } else {
            toUnDelete.get().setUndeletedData();

            projectRepository.save(toUnDelete.get());
        }

        return toUnDelete;
    }

    public Optional<Project> addNewRentToProject(final Long projectId, final Rent rentRequest) {
        Optional<Project> projectToAddTo = projectRepository.findById(projectId);

        if (projectToAddTo.isEmpty()) {
            log.error("Project not found with id: {}", projectId);
            return empty();
        }

        Rent rent = rentService.create(rentRequest);

        projectToAddTo.get().getRents().add(rent);

        Project savedProject = projectRepository.save(projectToAddTo.get());

        log.info("New rent added to project: {}", savedProject);

        return of(savedProject);
    }
}
