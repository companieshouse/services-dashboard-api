package uk.gov.companieshouse.servicesdashboardapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoGitReleaseInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoMetricsInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoVersionInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackMetricsInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitReleaseInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.VersionInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mapper
public interface ProjectInfoMapper {
    ProjectInfoMapper INSTANCE = Mappers.getMapper(ProjectInfoMapper.class);

    // Long (epoch millis) to Instant
    @Named("longToInstant")
    static Instant longToInstant(long epoch) {
        return Instant.ofEpochMilli(epoch);
    }

    // Map from ProjectInfo to MongoProjectInfo, with custom mapping for the "depTrackVersions" field.
    @Mapping(source = "depTrackVersions", target = "versions")
    MongoProjectInfo mapProjectInfoToMongoProjectInfo(ProjectInfo projectInfo);

    // Mapping for nested objects (List<MongoVersionInfo> to List<VersionInfo>)
    List<MongoVersionInfo> mapVersionInfoList(List<VersionInfo> versionInfoList);

    // Map each VersionInfo to MongoVersionInfo
    // Custom mapping metrics & Long to Instant
    @Mapping(source = "lastBomImport", target = "lastBomImport", qualifiedByName = "longToInstant")
    @Mapping(source = "depTrackMetrics", target = "metrics")
    MongoVersionInfo mapVersionInfoToMongoVersionInfo(VersionInfo versionInfo);

    // Map each DepTrackMetricsInfo to MongoMetricsInfo
    MongoMetricsInfo mapDepTrackMetricsInfoToMongoMetricsInfo(DepTrackMetricsInfo metricsInfo);

    // Map List<ProjectInfo> to List<MongoProjectInfo>
    default List<MongoProjectInfo> mapProjectInfoList(List<ProjectInfo> projectInfoList) {
        return projectInfoList.stream()
                .map(this::mapProjectInfoToMongoProjectInfo)
                .toList();
    }

    // Map<String, ProjectInfo> to List<MongoProjectInfo> (only mapping the values of the Map)
    default List<MongoProjectInfo> mapProjectInfoMap(Map<String, ProjectInfo> projectInfoMap) {
        return mapProjectInfoList(new ArrayList<>(projectInfoMap.values()));
    }

    // Custom mapping Release (single entry)  (String to LocalDate)
    @Mapping(source = "date", target = "date", qualifiedByName = "stringToLocalDate")
    MongoGitReleaseInfo toMongoGitReleaseInfo(GitReleaseInfo gitReleaseInfo);

    // mapping Releases (full List)
    List<MongoGitReleaseInfo> toMongoGitReleaseInfoList(List<GitReleaseInfo> releases);

    // String to LocalDate
    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(String date) {
        String defaultDateString = "1970-01-01";
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
            return LocalDate.parse(extractedDate);
        } catch (DateTimeParseException e) {
            ApiLogger.info("Failed to parse extracted date: " + extractedDate);
            return LocalDate.parse(defaultDateString);
        }
    }
}
