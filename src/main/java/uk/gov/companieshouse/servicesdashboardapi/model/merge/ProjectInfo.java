package uk.gov.companieshouse.servicesdashboardapi.model.merge;

import java.util.List;
import java.util.Map;

public class ProjectInfo {

   private String name;
   private List<VersionInfo> depTrackVersions;
   private Map<String, Integer> sonarMetrics;

   // Getters and setters
   public String getName() {
      return name;
   }

  public void setName(String name) {
      this.name = name;
   }

  public List<VersionInfo> getDepTrackVersions() {
   return depTrackVersions;
  }

  public void setDepTrackVersions(List<VersionInfo> versions) {
    this.depTrackVersions = versions;
  }

  public void addVersion(VersionInfo version) {
     depTrackVersions.add(version);
 }

 public Map<String, Integer> getSonarMetrics() {
   return sonarMetrics;
}

public void setSonarMetrics(Map<String, Integer> sonarMetrics) {
   this.sonarMetrics = sonarMetrics;
}

 @Override
 public String toString() {
     return String.format("Merge:%s/%s (%s)", name, depTrackVersions, sonarMetrics);
 }
}
