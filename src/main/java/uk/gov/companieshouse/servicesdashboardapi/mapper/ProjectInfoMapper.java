package uk.gov.companieshouse.servicesdashboardapi.mapper;

import java.util.Date;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;

@Mapper
public interface ProjectInfoMapper {
    ProjectInfoMapper INSTANCE = Mappers.getMapper(ProjectInfoMapper.class);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "lastBomImport", target = "lastBomImport", qualifiedByName = "longToDate")
     MongoProjectInfo depTrackProjectInfoToMongoProjectInfo(DepTrackProjectInfo depTrackProjectInfo);

    // Method for mapping list of DepTrackProjectInfo to list of MongoProjectInfo
    List<MongoProjectInfo> depTrackProjectInfoListToMongoProjectInfoList(List<DepTrackProjectInfo> depTrackProjectInfoList);
   @Named("longToDate")
   static Date longToDate(long epoch) {
         return new Date(epoch);
   }
}
