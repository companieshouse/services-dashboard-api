package uk.gov.companieshouse.servicesdashboardapi.mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackTag;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.VersionInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

@Mapper
public interface MergeInfoMapper {
   MergeInfoMapper INSTANCE = Mappers.getMapper(MergeInfoMapper.class);

   // Map a single DepTrackProjectInfo to VersionInfo
   @Mapping(source = "version", target = "version")
   @Mapping(source = "uuid", target = "uuid")
   @Mapping(source = "lastBomImport", target = "lastBomImport")
   @Mapping(source = "metrics", target = "depTrackMetrics")
   @Mapping(source = "tags", target = "runtime",  qualifiedByName = "mapTagsToEnv")
   VersionInfo depTrackToVersionInfo(DepTrackProjectInfo depTrackProjectInfo);

    // Custom mapping method to handle the grouping logic
    default Set<ProjectInfo> mapDepTrackListToProjectInfoSet(List<DepTrackProjectInfo> depTrackList) {
        // Group by 'name' and transform the data
        Map<String, List<DepTrackProjectInfo>> groupedByName = depTrackList.stream()
                .collect(Collectors.groupingBy(DepTrackProjectInfo::getName));

        // For each group, map to ProjectInfo
        Set<ProjectInfo> projectInfoSet = new HashSet<>();
        for (Map.Entry<String, List<DepTrackProjectInfo>> entry : groupedByName.entrySet()) {
            String name = entry.getKey();
            List<DepTrackProjectInfo> depTrackProjects = entry.getValue();

            // Map the versions to VersionInfo
            List<VersionInfo> versionInfoList = depTrackProjects.stream()
                    .map(this::depTrackToVersionInfo)
                    .collect(Collectors.toList());

            // Create the ProjectInfo
            ProjectInfo projectInfo = new ProjectInfo();
            projectInfo.setName(name);
            projectInfo.setDepTrackVersions(versionInfoList);
            ApiLogger.info("=====> Adding projectInfo to Set:" + projectInfo.toString());


            projectInfoSet.add(projectInfo);
        }

        return projectInfoSet;
    }

   @Named("mapTagsToEnv")
   default String mapTagsToEnv(List<DepTrackTag> tags) {
      if (tags == null || tags.isEmpty()) {
         return "";
      }
      for (DepTrackTag tag : tags) {
         if (tag.getName() != null && tag.getName().startsWith("runtime:")) {
               return tag.getName().substring("runtime:".length());
         }
      }
      return "";
   }
}
