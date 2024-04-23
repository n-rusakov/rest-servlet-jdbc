package org.example.restservlet.mapper;

import org.example.restservlet.entity.Publisher;
import org.example.restservlet.web.dto.PublisherResponse;
import org.example.restservlet.web.dto.PublisherUpsertRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PublisherMapper {

    public static final PublisherMapper INSTANCE =
            Mappers.getMapper(PublisherMapper.class);

    public abstract Publisher toEntity(PublisherUpsertRequest request);

    @Mapping(target = "id", source = "publisherId")
    public abstract Publisher toEntity(Long publisherId, PublisherUpsertRequest request);


    public abstract PublisherResponse toResponse(Publisher publisher);

    public abstract List<PublisherResponse> toListResponse(List<Publisher> publishers);

}
