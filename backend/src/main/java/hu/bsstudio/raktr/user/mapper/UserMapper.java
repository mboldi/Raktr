package hu.bsstudio.raktr.user.mapper;

import hu.bsstudio.raktr.dal.entity.User;
import hu.bsstudio.raktr.dto.user.UserDetailsDto;
import hu.bsstudio.raktr.dto.user.UserUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface UserMapper {

    UserDetailsDto entityToDetailsDto(User user);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "familyName", ignore = true)
    @Mapping(target = "givenName", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateDtoToEntity(@MappingTarget User user, UserUpdateDto updateDto);

}
