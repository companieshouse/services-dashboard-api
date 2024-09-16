package uk.gov.companieshouse.servicesdashboardapi.model.merge;

import java.util.List;
import java.util.Map;

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.mapper.MergeInfoMapper;

import org.springframework.stereotype.Component;

@Component
public class ServicesInfo {
   private  Map<String, ProjectInfo> projectInfoMap;


   public Map<String, ProjectInfo> getProjectInfoMap(){
      return this.projectInfoMap;
   }
   public Map<String, ProjectInfo> setProjectInfoMap(List<DepTrackProjectInfo> listDepTrack) {
      this.projectInfoMap = MergeInfoMapper.INSTANCE.mapDepTrackListToProjectInfoMap(listDepTrack);
      return this.projectInfoMap;
   }
}
