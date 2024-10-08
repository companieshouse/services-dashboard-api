package uk.gov.companieshouse.servicesdashboardapi.mapper;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackTag;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.VersionInfo;

@Mapper
public interface MergeInfoMapper {
   MergeInfoMapper INSTANCE = Mappers.getMapper(MergeInfoMapper.class);

// public interface DepTrackProjectMapper {

    @Mappings({
        @Mapping(source = "tags", target = "lang", qualifiedByName = "extractLang"),
        @Mapping(source = "tags", target = "runtime", qualifiedByName = "extractRuntime"),
        @Mapping(source = "metrics", target = "depTrackMetrics"),
    })
    VersionInfo mapToVersionInfo(DepTrackProjectInfo depTrackProjectInfo);

    @Mappings({
        @Mapping(source = "name", target = "name"),
        @Mapping(target = "depTrackVersions", ignore = true),  // handled manually, down here
        @Mapping(target = "sonarKey",         ignore = true),  // no mapping required. Field set later
        @Mapping(target = "sonarMetrics",     ignore = true),  // no mapping required. Field set later
        @Mapping(target = "gitInfo",          ignore = true),  // no mapping required. Field set later
        @Mapping(target = "ecs",              ignore = true)   // no mapping required. Field set later
    })
    ProjectInfo mapToProjectInfo(DepTrackProjectInfo depTrackProjectInfo);

    // Custom method to map List<DepTrackProjectInfo> to Map<String, ProjectInfo>
    default Map<String, ProjectInfo> mapDepTrackListToProjectInfoMap(List<DepTrackProjectInfo> projectList) {
        // Create a map where each unique project name is a key, and each ProjectInfo contains versions
        Map<String, ProjectInfo> projectInfoMap = new HashMap<>();

        for (DepTrackProjectInfo depTrackProjectInfo : projectList) {
            // Get or create the ProjectInfo for the given name
            ProjectInfo projectInfo = projectInfoMap.computeIfAbsent(depTrackProjectInfo.getName(), name -> mapToProjectInfo(depTrackProjectInfo));

            // Map the current DepTrackProjectInfo to VersionInfo
            VersionInfo versionInfo = mapToVersionInfo(depTrackProjectInfo);

            // Add the version info to the project
            if (projectInfo.getDepTrackVersions() == null) {
                projectInfo.setDepTrackVersions(new ArrayList<>());
            }
            projectInfo.getDepTrackVersions().add(versionInfo);
        }

        return projectInfoMap;
    }

    // Named method to extract "lang"
    @Named("extractLang")
    default String getLangTagValue(List<DepTrackTag> tags) {
        return getTagValue(tags, "lang");
    }

    // Named method to extract "runtime"
    @Named("extractRuntime")
    default String getRuntimeTagValue(List<DepTrackTag> tags) {
        return getTagValue(tags, "runtime");
    }

    // Generic method that extracts a tag value based on the given prefix
    default String getTagValue(List<DepTrackTag> tags, String prefix) {
        prefix += ":";
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        for (DepTrackTag tag : tags) {
            if (tag.getName() != null && tag.getName().startsWith(prefix)) {
                return tag.getName().substring(prefix.length());
            }
        }
        return "";
    }
}
