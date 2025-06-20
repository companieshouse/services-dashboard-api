package uk.gov.companieshouse.servicesdashboardapi.mapper;

import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import uk.gov.companieshouse.servicesdashboardapi.model.merge.ConfigInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoConfigInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoEndoflifeInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.endoflife.EndofLifeInfo;


@Mapper
public interface ConfigInfoMapper {

    ConfigInfoMapper INSTANCE = Mappers.getMapper(ConfigInfoMapper.class);

    // Map ConfigInfo to MongoConfigInfo, ignoring 'id' and 'lastScan' so their values are preserved in MongoConfigInfo
    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "lastScan", ignore = true)
    MongoConfigInfo configInfoToMongoConfigInfo(ConfigInfo configInfo);

    // Map lists of EndofLifeInfo and MongoEndoflifeInfo
    List<MongoEndoflifeInfo> endofLifeInfoListToMongoEndoflifeInfoList(List<EndofLifeInfo> endofLifeInfoList);

    // Map the inner Map structure for the 'endol' field
    Map<String, List<MongoEndoflifeInfo>> endolToMongoEndol(Map<String, List<EndofLifeInfo>> endol);
}
