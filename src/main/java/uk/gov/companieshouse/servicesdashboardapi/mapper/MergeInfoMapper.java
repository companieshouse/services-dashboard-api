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
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

@Mapper
public interface MergeInfoMapper {
   MergeInfoMapper INSTANCE = Mappers.getMapper(MergeInfoMapper.class);

// public interface DepTrackProjectMapper {

    @Mappings({
        @Mapping(source = "tags", target = "runtime", qualifiedByName = "mapTagsToRuntime"),
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

   // @Named("mapTagsToRuntime")
   // default String mapTagsToRuntime(List<DepTrackTag> tags) {
   //    if (tags != null) {
   //          for (DepTrackTag tag : tags) {
   //             if (tag.getName().startsWith("runtime:")) {
   //                return tag.getName().substring("runtime:".length());
   //             }
   //          }
   //    }
   //    return ""; // Return empty string if no runtime tag is found
   // }

   // Custom method to extract runtime tag (if available)
   @Named("mapTagsToRuntime")
   default String mapTagsToRuntime(List<DepTrackTag> tags) {
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

   // // Map a single DepTrackProjectInfo to VersionInfo
   // @Mapping(source = "version", target = "version")
   // @Mapping(source = "uuid", target = "uuid")
   // @Mapping(source = "lastBomImport", target = "lastBomImport")
   // @Mapping(source = "metrics", target = "depTrackMetrics")
   // @Mapping(source = "tags", target = "runtime",  qualifiedByName = "mapTagsToEnv")
   // VersionInfo depTrackToVersionInfo(DepTrackProjectInfo depTrackProjectInfo);

   //  // Custom mapping method to handle the grouping logic
   //  default Set<ProjectInfo> mapDepTrackListToProjectInfoSet(List<DepTrackProjectInfo> depTrackList) {
   //      // Group by 'name' and transform the data
   //      Map<String, List<DepTrackProjectInfo>> groupedByName = depTrackList.stream()
   //              .collect(Collectors.groupingBy(DepTrackProjectInfo::getName));

   //      // For each group, map to ProjectInfo
   //      Set<ProjectInfo> projectInfoSet = new HashSet<>();
   //      for (Map.Entry<String, List<DepTrackProjectInfo>> entry : groupedByName.entrySet()) {
   //          String name = entry.getKey();
   //          List<DepTrackProjectInfo> depTrackProjects = entry.getValue();

   //          // Map the versions to VersionInfo
   //          List<VersionInfo> versionInfoList = depTrackProjects.stream()
   //                  .map(this::depTrackToVersionInfo)
   //                  .collect(Collectors.toList());

   //          // Create the ProjectInfo
   //          ProjectInfo projectInfo = new ProjectInfo();
   //          projectInfo.setName(name);
   //          projectInfo.setDepTrackVersions(versionInfoList);
   //          ApiLogger.info("=====> Adding projectInfo to Set:" + projectInfo.toString());


   //          projectInfoSet.add(projectInfo);
   //      }

   //      return projectInfoSet;
   //  }

}
