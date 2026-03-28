package hu.bsstudio.raktr.comment.mapper;

import hu.bsstudio.raktr.dal.entity.Comment;
import hu.bsstudio.raktr.dto.comment.CommentCreateDto;
import hu.bsstudio.raktr.dto.comment.CommentDetailsDto;
import hu.bsstudio.raktr.mapper.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = DateTimeMapper.class)
public interface CommentMapper {

    CommentDetailsDto entityToDetailsDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Comment createDtoToEntity(CommentCreateDto createDto);

}
