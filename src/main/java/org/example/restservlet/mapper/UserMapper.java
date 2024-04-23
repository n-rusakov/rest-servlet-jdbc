package org.example.restservlet.mapper;

import org.example.restservlet.entity.User;
import org.example.restservlet.web.dto.UserGamesResponse;
import org.example.restservlet.web.dto.UserResponse;
import org.example.restservlet.web.dto.UserUpsertRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {GameMapper.class})
public abstract class UserMapper {

    public static final UserMapper INSTANCE =
            Mappers.getMapper(UserMapper.class);

    public abstract UserResponse toUserResponse(User user);

    public abstract List<UserResponse> toUserResponseList(List<User> users);

    public abstract UserGamesResponse toUserGamesResponse(User user);

    public abstract List<UserGamesResponse> toUserGamesRresponseList(List<User> users);

    public abstract User toEntity(UserUpsertRequest request);

    @Mapping(target = "id", source = "userId")
    public abstract User toEntity(Long userId, UserUpsertRequest request);

}
