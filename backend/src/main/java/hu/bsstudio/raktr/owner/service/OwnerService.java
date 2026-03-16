package hu.bsstudio.raktr.owner.service;

import hu.bsstudio.raktr.dal.entity.Owner;
import hu.bsstudio.raktr.dal.entity.Scannable;
import hu.bsstudio.raktr.dal.repository.OwnerRepository;
import hu.bsstudio.raktr.dal.repository.ScannableRepository;
import hu.bsstudio.raktr.dto.owner.OwnerCreateDto;
import hu.bsstudio.raktr.dto.owner.OwnerDetailsDto;
import hu.bsstudio.raktr.dto.owner.OwnerUpdateDto;
import hu.bsstudio.raktr.exception.EntityAlreadyExistsException;
import hu.bsstudio.raktr.exception.EntityInUseException;
import hu.bsstudio.raktr.exception.EntityNotFoundException;
import hu.bsstudio.raktr.owner.mapper.OwnerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;

    private final ScannableRepository scannableRepository;

    private final OwnerMapper ownerMapper;

    public List<OwnerDetailsDto> listOwners() {
        var owners = ownerRepository.findAll();
        return owners.stream().map(ownerMapper::entityToDetailsDto).toList();
    }

    @Transactional
    public OwnerDetailsDto createOwner(OwnerCreateDto createDto) {
        createDto.setName(createDto.getName().trim());

        if (ownerRepository.existsByName(createDto.getName())) {
            throw new EntityAlreadyExistsException(Owner.class, createDto.getName());
        }

        var owner = ownerMapper.createDtoToEntity(createDto);

        owner = ownerRepository.saveAndFlush(owner);

        log.info("Created Owner with name [{}]", owner.getName());

        return ownerMapper.entityToDetailsDto(owner);
    }

    @Transactional
    public OwnerDetailsDto updateOwner(Long ownerId, OwnerUpdateDto updateDto) {
        var owner = getOwner(ownerId);

        if (ownerRepository.existsByNameAndIdNot(updateDto.getName(), ownerId)) {
            throw new EntityAlreadyExistsException(Owner.class, updateDto.getName());
        }

        ownerMapper.updateDtoToEntity(owner, updateDto);
        ownerRepository.saveAndFlush(owner);

        log.info("Updated Owner with ID [{}]", ownerId);

        return ownerMapper.entityToDetailsDto(owner);
    }

    @Transactional
    public void deleteOwner(Long ownerId) {
        var owner = getOwner(ownerId);

        if (scannableRepository.existsByOwner(owner)) {
            throw new EntityInUseException(Owner.class, ownerId, Scannable.class);
        }

        ownerRepository.delete(owner);

        log.info("Deleted Owner with name [{}]", owner.getName());
    }

    private Owner getOwner(Long ownerId) {
        return ownerRepository.findById(ownerId).orElseThrow(() -> new EntityNotFoundException(Owner.class, ownerId));
    }

}
