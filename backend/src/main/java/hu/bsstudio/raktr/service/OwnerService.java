package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.exception.ObjectConflictException;
import hu.bsstudio.raktr.model.Owner;
import hu.bsstudio.raktr.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public final Optional<Owner> create(final Owner owner) {
        final var found = ownerRepository.findByName(owner.getName());

        if (found.isEmpty()) {
            final Owner saved = ownerRepository.save(owner);
            log.info("Owner created: {}", saved);
            return Optional.of(saved);
        } else {
            log.info("Owner exists with given new name: {}", owner.getName());
        }

        return found;
    }

    public final Optional<Owner> update(final Owner owner) {
        Optional<Owner> ownerToUpdate = ownerRepository.findById(owner.getId());

        if (ownerToUpdate.isEmpty()) {
            log.error("Owner not found with id: {}", owner.getId());
            return Optional.empty();
        }

        Optional<Owner> byName = ownerRepository.findByName(owner.getName());

        if (byName.isPresent() && !byName.get().getId().equals(ownerToUpdate.get().getId())) {
            log.error("Owner present with given name: {}", owner.getName());
            throw new ObjectConflictException();
        }

        ownerToUpdate.get().setName(owner.getName());
        ownerToUpdate.get().setInSchInventory(owner.isInSchInventory());

        Owner saved = ownerRepository.save(ownerToUpdate.get());

        log.info("Owner updated: {}", saved);
        return Optional.of(saved);
    }

    public final List<Owner> getAll() {
        final List<Owner> fetched = ownerRepository.findAll();
        log.info("Locations fetched from DB: {}", fetched);
        return fetched;
    }

    public final Optional<Owner> deleteOwner(final Long id) {
        Optional<Owner> ownerToDelete = ownerRepository.findById(id);

        if (ownerToDelete.isEmpty()) {
            log.error("Owner not found with id: {}", id);
            return Optional.empty();
        }

        if (!ownerToDelete.get().getDevices().isEmpty()) {
            log.error("Owner cannot be deleted because it has {} devices: {}", ownerToDelete.get().getDevices().size(), ownerToDelete.get());
            throw new ObjectConflictException();
        }

        ownerRepository.delete(ownerToDelete.get());
        log.info("Owner removed: {}", ownerToDelete.get());

        return ownerToDelete;
    }
}
