package uk.gov.companieshouse.servicesdashboardapi.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.VersionInfo;

@Mapper
public interface MergeInfoMapper {
   MergeInfoMapper INSTANCE = Mappers.getMapper(MergeInfoMapper.class);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "depTrackVersions", source = "versions")
    ProjectInfo map(String name, List<VersionInfo> versions);

    @Mapping(target = "version", source = "version")
    @Mapping(target = "lastBomImport", source = "lastBomImport")
    @Mapping(target = "metrics", source = "metrics")
    VersionInfo map(DepTrackProjectInfo info);

    default List<ProjectInfo> mapList(List<DepTrackProjectInfo> list) {
        Map<String, List<VersionInfo>> groupedMap = list.stream()
            .collect(Collectors.groupingBy(DepTrackProjectInfo::getName,
                    Collectors.mapping(this::map, Collectors.toList())));

        return groupedMap.entrySet().stream()
            .map(entry -> map(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
}
