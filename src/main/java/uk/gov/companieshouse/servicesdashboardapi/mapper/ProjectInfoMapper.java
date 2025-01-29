package uk.gov.companieshouse.servicesdashboardapi.mapper;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackMetricsInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitReleaseInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.VersionInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoGitReleaseInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoMetricsInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoVersionInfo;

@Mapper
public interface ProjectInfoMapper {
   ProjectInfoMapper INSTANCE = Mappers.getMapper(ProjectInfoMapper.class);

    // Map from ProjectInfo to MongoProjectInfo, with custom mapping for the "depTrackVersions" field.
    @Mapping(source = "depTrackVersions", target = "versions")
    MongoProjectInfo mapProjectInfoToMongoProjectInfo(ProjectInfo projectInfo);

    // Mapping for nested objects (List<MongoVersionInfo> to List<VersionInfo>)
    List<MongoVersionInfo> mapVersionInfoList(List<VersionInfo> versionInfoList);

    // Map each VersionInfo to MongoVersionInfo
    // Custom mapping metrics & Long to Date
   @Mapping(source = "lastBomImport", target = "lastBomImport", qualifiedByName = "longToDate")
   @Mapping(source = "depTrackMetrics", target = "metrics")
    MongoVersionInfo mapVersionInfoToMongoVersionInfo(VersionInfo versionInfo);

    // Map each DepTrackMetricsInfo to MongoMetricsInfo
    MongoMetricsInfo mapDepTrackMetricsInfoToMongoMetricsInfo(DepTrackMetricsInfo metricsInfo);

    // Map List<ProjectInfo> to List<MongoProjectInfo>
    default List<MongoProjectInfo> mapProjectInfoList(List<ProjectInfo> projectInfoList) {
        return projectInfoList.stream()
                .map(this::mapProjectInfoToMongoProjectInfo)
                .collect(java.util.stream.Collectors.toList());
    }

    // Map Map<String, ProjectInfo> to List<MongoProjectInfo> (only mapping the values of the Map)
    default List<MongoProjectInfo> mapProjectInfoMap(Map<String, ProjectInfo> projectInfoMap) {
        return mapProjectInfoList(new ArrayList<>(projectInfoMap.values()));
    }

    // Custom mapping Release (single entry)  (String to Date)
    @Mapping(source = "date", target = "date", qualifiedByName = "stringToDate")
    MongoGitReleaseInfo toMongoGitReleaseInfo(GitReleaseInfo gitReleaseInfo);

    // mapping Releases (full List)
    List<MongoGitReleaseInfo> toMongoGitReleaseInfoList(List<GitReleaseInfo> releases);

    // Long to Date
    @Named("longToDate")
        static Date longToDate(long epoch) {
        return new Date(epoch);
    }

   // String to Date
   @Named("stringToDate")
   default Date stringToDate(String date) {
      String defaultDateString = "1970-01-01";
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      String extractedDate = defaultDateString;

      try {
          Pattern pattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}"); // "yyyy-MM-dd"
          Matcher matcher = pattern.matcher(date);
          if (matcher.find()) {
              extractedDate = matcher.group();
          } else {
            ApiLogger.info("Invalid date format: " + date + ", defaulting to " + defaultDateString);
          }
      } catch (Exception e) {
          ApiLogger.info("Unexpected error while parsing date: " + date);
      }

      try {
          return sdf.parse(extractedDate);
      } catch (ParseException e) {
          ApiLogger.info("Failed to parse extracted date: " + extractedDate);
          try {
              return sdf.parse(defaultDateString);
          } catch (ParseException ex) {
              throw new RuntimeException("Failed to set default date", ex);
          }
      }
  }
}
