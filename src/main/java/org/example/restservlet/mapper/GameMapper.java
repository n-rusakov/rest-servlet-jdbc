package org.example.restservlet.mapper;

import org.example.restservlet.entity.Game;
import org.example.restservlet.entity.Publisher;
import org.example.restservlet.service.PublisherService;
import org.example.restservlet.web.dto.GameResponse;
import org.example.restservlet.web.dto.GameUpsertRequest;
import org.example.restservlet.web.dto.GameUsersResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = PublisherMapper.class)
public abstract class GameMapper {

    public static final GameMapper INSTANCE =
            Mappers.getMapper(GameMapper.class);


    @Mapping(target = "publisher", source = "publisher")
    public abstract GameResponse toGameResponse(Game game);

    public abstract List<GameResponse> toGameResponseList(List<Game> games);


    @Mapping(target = "publisher", source = "publisher")
    public abstract GameUsersResponse toGameUsersResponse(Game game);

    public abstract List<GameUsersResponse> toGameUsersResponseList(List<Game> games);

    public abstract Game toEntity(GameUpsertRequest request,
                                  @Context PublisherService publisherService);

    @Mapping(target = "id", source = "gameId")
    public abstract Game toEntity(Long gameId, GameUpsertRequest request,
                                  @Context PublisherService publisherService);

    @ObjectFactory
    Game lookupLinks(GameUpsertRequest request,@Context PublisherService publisherService) {
        Game game = new Game();
        game.setTitle(request.getTitle());

        Publisher publisher = publisherService.findById(request.getPublisherId());
        game.setPublisher(publisher);

        return game;
    }



}
