package uk.gov.companieshouse.servicesdashboardapi.model.merge;

import java.util.List;

public class ProjectInfo {

   private String name;
   private List<VersionInfo> depTrackVersions;

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
 @Override
 public String toString() {
     return String.format("Merge:%s/%s", name, depTrackVersions);
 }
}
