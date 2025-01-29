package uk.gov.companieshouse.servicesdashboardapi.model.github;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
public class GitInfo {

   private String repo;
   private String lang;
   private String owner;
   private List<GitReleaseInfo> releases;

   // Getters and Setters
   public String getRepo() {
      return repo;
   }

   public void setRepo(String repo) {
      this.repo = repo;
   }

   public String getLang() {
      return lang;
   }

   public String getOwner() {
      return owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public void setLang(String lang) {
      this.lang = lang;
   }

   public List<GitReleaseInfo> getReleases() {
      return releases;
   }

   public void setReleases(List<GitReleaseInfo> gitReleases) {
      this.releases = filterReleases(gitReleases);
   }

   @Override
   public String toString() {
      return String.format("{r:%s l:%s o:%s [R:%s]}", repo, lang, owner, releases.toString());
   }


   // Filter out the 2 latest releases that are not part of the same release cycle
   // ex.
   //    "ecs-service-1.0.22"  |
   //    "ecs-service-1.0.21"  |
   //    "ecs-service-1.0.20"  |-----> "ecs-service-1.0.22"
   //    "4.0.11"              |       "4.0.11"
   //    "4.0.10"              |
   //    "ecs-service-1.0.20"  |
   private List<GitReleaseInfo> filterReleases(List<GitReleaseInfo> gitReleases) {
        List<GitReleaseInfo> rel = new ArrayList<GitReleaseInfo>();
        if (! gitReleases.isEmpty()) {
            GitReleaseInfo firstRelease = gitReleases.get(0);
            rel.add(firstRelease);

            String regexPattern = buildRegexPattern(firstRelease.getVersion());
            // System.out.println("Generated Regex: " + regexPattern);

            Pattern pattern = Pattern.compile(regexPattern);

            // The built regex are generic and could potentially match more random strings
            // ex the regex from v1.177.0-rc3" matches something like "Z1876545.22222.564-LL99999"
            // but that's ok considering that our GitHub releases' format is consistent
            for (GitReleaseInfo r : gitReleases) {
                if (!pattern.matcher(r.getVersion()).matches()) {
                    rel.add(r); // Add any optional different format and then break. (We'll then have max 2 releases)
                    break;
                }
            }
        }
        return rel;
    }

    // build a regex from a provided Release string
    // ex:
    // "v1.177.0-rc3"       --> [a-zA-Z]\d+\.\d+\.\d+\-[a-zA-Z][a-zA-Z]\d+])
    // "5.17.0"             --> \d+\.\d+\.\d+
    // "ecs-service-1.0.22" --> [a-zA-Z][a-zA-Z][a-zA-Z]\-[a-zA-Z][a-zA-Z][a-zA-Z][a-zA-Z][a-zA-Z][a-zA-Z][a-zA-Z]\-\d+\.\d+\.\d+

    private static String buildRegexPattern(String example) {
        StringBuilder regex = new StringBuilder();
        boolean inNumber = false; // Flag to track digit sequences

        for (int i = 0; i < example.length(); i++) {
            char c = example.charAt(i);

            if (Character.isLetter(c)) {
                regex.append("[a-zA-Z]");
                inNumber = false; // Reset number flag
            } else if (Character.isDigit(c)) {
                if (!inNumber) { // Only append \d+ for the first digit in a sequence
                    regex.append("\\d+");
                    inNumber = true;
                }
            } else {
                regex.append("\\").append(c); // Escape special characters
                inNumber = false; // Reset number flag
            }
        }

        return regex.toString();
    }
}

