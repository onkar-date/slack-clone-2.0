package com.slack.clone.identity.mapper;

import com.slack.clone.identity.dto.UserDTO;
import com.slack.clone.identity.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper for User entity
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDTO toDTO(User user);
}
