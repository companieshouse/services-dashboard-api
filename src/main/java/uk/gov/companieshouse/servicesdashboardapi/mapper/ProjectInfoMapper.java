package uk.gov.companieshouse.servicesdashboardapi.mapper;

import java.util.Date;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackMetricsInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.VersionInfo;
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

   // Custom mapping method to convert long to Date
   @Named("longToDate")
     static Date longToDate(long epoch) {
      return new Date(epoch);
   }
}
