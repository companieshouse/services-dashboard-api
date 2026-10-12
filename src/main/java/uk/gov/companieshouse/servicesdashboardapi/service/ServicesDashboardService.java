package uk.gov.companieshouse.servicesdashboardapi.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.servicesdashboardapi.mapper.ProjectInfoMapper;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ServicesInfo;
import uk.gov.companieshouse.servicesdashboardapi.repository.CustomMongoProjectInfoRepository;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

import java.util.List;

@Service
public class ServicesDashboardService {

    private final ServicesInfo servicesInfo;

    private final CustomMongoProjectInfoRepository customMongoProjectInfoRepository;

    public ServicesDashboardService(ServicesInfo servicesInfo, CustomMongoProjectInfoRepository customMongoProjectInfoRepository) {
        this.servicesInfo = servicesInfo;
        this.customMongoProjectInfoRepository = customMongoProjectInfoRepository;
    }

    public void createServicesDashboard() {
        ApiLogger.info("---------Create Serv START");
        List<MongoProjectInfo> mongoProjectInfoList = ProjectInfoMapper.INSTANCE.mapProjectInfoMap(servicesInfo.getProjectInfoMap());
        ApiLogger.info("---------Saving Project Infos to DB");
        customMongoProjectInfoRepository.saveProjectInfos(mongoProjectInfoList);
        ApiLogger.info("---------Create Serv END");
    }
}


