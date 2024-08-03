package uk.gov.companieshouse.servicesdashboardapi.mapper;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackMetricsInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitLastReleaseInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.VersionInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoGitInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoGitLastReleaseInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoMetricsInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoVersionInfo;

@Mapper
public interface ProjectInfoMapper {

    ProjectInfoMapper INSTANCE = Mappers.getMapper(ProjectInfoMapper.class);

    @Mapping(source = "depTrackVersions", target = "versions")
    MongoProjectInfo projectInfoToMongoProjectInfo(ProjectInfo projectInfo);

    List<MongoProjectInfo> projectInfoListToMongoProjectInfoList(List<ProjectInfo> projectInfoList);

    @Mapping(source = "lastBomImport", target = "lastBomImport", qualifiedByName = "longToDate")
    MongoVersionInfo versionInfoToMongoVersionInfo(VersionInfo versionInfo);

    MongoMetricsInfo depTrackMetricsInfoToMongoMetricsInfo(DepTrackMetricsInfo depTrackMetricsInfo);

    MongoGitInfo toMongoGitInfo(GitInfo gitInfo);

   @Mapping(source = "date", target = "date", qualifiedByName = "stringToDate")
    MongoGitLastReleaseInfo toMongoGitLastReleaseInfo(GitLastReleaseInfo gitLastReleaseInfo);

   // Custom mapping: long to Date
   @Named("longToDate")
     static Date longToDate(long epoch) {
      return new Date(epoch);
   }

    // Custom mapping: String to Date
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
