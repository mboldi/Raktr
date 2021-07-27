package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.model.Location;
import hu.bsstudio.raktr.model.Owner;
import hu.bsstudio.raktr.repository.LocationRepository;
import hu.bsstudio.raktr.repository.OwnerRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public final List<Owner> getAll() {
        final List<Owner> fetched = ownerRepository.findAll();
        log.info("Locations fetched from DB: {}", fetched);
        return fetched;
    }
}
