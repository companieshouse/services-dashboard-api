package uk.gov.companieshouse.servicesdashboardapi.model.merge;

import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.gov.companieshouse.servicesdashboardapi.model.github.GitInfo;

public class ProjectInfo {

   private String name;
   private List<VersionInfo> depTrackVersions;
   private String sonarKey;
   private Map<String, Integer> sonarMetrics;
   private GitInfo gitInfo;
   // private Map<String, Set<String>> ecs;


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

   public String getSonarKey() {
      return sonarKey;
   }

   public void setSonarKey(String sonarKey) {
      this.sonarKey = sonarKey;
   }

   public Map<String, Integer> getSonarMetrics() {
      return sonarMetrics;
   }

   public void setSonarMetrics(Map<String, Integer> sonarMetrics) {
      this.sonarMetrics = sonarMetrics;
   }

   public GitInfo getGitInfo() {
      return gitInfo;
   }

   public void setGitInfo(GitInfo gitInfo) {
      this.gitInfo = gitInfo;
   }

   // public Map<String, Set<String>> getEcs() {
   //    return ecs;
   // }

   // public void setEcs(Map<String, Set<String>> ecs) {
   //       this.ecs = ecs;
   // }

   @Override
   public String toString() {
      return String.format("Merge:%s/%s (%s) (%s) [%s]", name, depTrackVersions, sonarMetrics, gitInfo, sonarKey);
   }
}
