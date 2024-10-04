package uk.gov.companieshouse.servicesdashboardapi.model.endoflife;

import com.fasterxml.jackson.annotation.JsonProperty;

/* Here the responses to the following projects
         nodejs
         go
         amazon-corretto
         oracle-jdk
         spring-framework
         spring-boot
the numbers on the fields highlight the commonality of these 6 fields:
  1 "cycle"
  2 "releaseDate"
  3 "lts"
  4 "eol"
  5 "latest"
  6 "latestReleaseDate"

--------------------------[nodejs]        --------------------------[go]          --------------------------[spring-boot] --------------------------[spring-framework]    --------------------------[amazon-corretto]  --------------------------[oracle-jdk]
[                                         [                                       [                                       [                                               [                                            [
  {                                         {                                       {                                       {                                               {                                            {
  1 "cycle": "22",                          1 "cycle": "1.23",                      1 "cycle": "3.3",                       1 "cycle": "6.1",                               1 "cycle": "22",                             1 "cycle": "23",
  2 "releaseDate": "2024-04-24",            2 "releaseDate": "2024-08-13",            "supportedJavaVersions": "17 - 22",     "supportedJavaVersions": "17 - 23",           2 "releaseDate": "2024-03-19",               2 "releaseDate": "2024-09-17",
  3 "lts": "2024-10-29",                    4 "eol": false,                         2 "releaseDate": "2024-05-23",            "supportedJakartaEEVersions": "9 - 10",       4 "eol": "2024-10-31",                       4 "eol": "2025-03-18",
  4 "eol": "2027-04-30",                    5 "latest": "1.23.1",                   4 "eol": "2025-05-23",                  2 "releaseDate": "2023-11-16",                  5 "latest": "22.0.2.9.1",                    5 "latest": "23.0.0",
  5 "latest": "22.9.0",                     6 "latestReleaseDate": "2024-09-05",    5 "latest": "3.3.4",                    4 "eol": "2025-08-31",                          6 "latestReleaseDate": "2024-07-16",         6 "latestReleaseDate": "2024-09-17",
  6 "latestReleaseDate": "2024-09-17",      3 "lts": false                          6 "latestReleaseDate": "2024-09-19",    5 "latest": "6.1.13",                           3 "lts": false                                 "link": "https://www.oracle.com/java/technologies/javase/23all-relnotes.html",
    "support": "2025-10-21"                 },                                      3 "lts": false,                         6 "latestReleaseDate": "2024-09-12",            },                                           3 "lts": false,
  },                                        {                                         "extendedSupport": "2026-08-23"       3 "lts": false,                                 {                                              "extendedSupport": false
  {                                         1 "cycle": "1.22",                      },                                        "extendedSupport": "2026-12-31"               1 "cycle": "21",                             },
  1 "cycle": "21",                          2 "releaseDate": "2024-02-06",          {                                       },                                              3 "lts": true,                               {
  2 "releaseDate": "2023-10-17",            4 "eol": false,                         1 "cycle": "3.2",                       {                                               2 "releaseDate": "2023-09-21",               1 "cycle": "22",
  4 "eol": "2024-06-01",                    5 "latest": "1.22.7",                     "supportedJavaVersions": "17 - 21",   1 "cycle": "6.0",                               4 "eol": "2030-10-31",                       2 "releaseDate": "2024-03-19",
  5 "latest": "21.7.3",                     6 "latestReleaseDate": "2024-09-05",    2 "releaseDate": "2023-11-23",            "supportedJavaVersions": "17 - 21",           5 "latest": "21.0.4.7.1",                    4 "eol": "2024-09-17",
  6 "latestReleaseDate": "2024-04-10",      3 "lts": false                          4 "eol": "2024-11-23",                    "supportedJakartaEEVersions": "9 - 10",       6 "latestReleaseDate": "2024-07-16"          5 "latest": "22.0.2",
  3 "lts": false,                           },                                      5 "latest": "3.2.10",                   2 "releaseDate": "2022-11-16",                  },                                           6 "latestReleaseDate": "2024-07-16",
    "support": "2024-04-01"                 {                                       6 "latestReleaseDate": "2024-09-19",    4 "eol": "2024-08-31",                          {                                            3 "lts": false,
  },                                        1 "cycle": "1.21",                      3 "lts": false,                         5 "latest": "6.0.23",                           1 "cycle": "20",                               "extendedSupport": false
  {                                         2 "releaseDate": "2023-08-08",            "extendedSupport": "2026-02-23"       6 "latestReleaseDate": "2024-08-14",            2 "releaseDate": "2023-03-21",               },
  1 "cycle": "20",                          4 "eol": "2024-08-13",                  },                                      3 "lts": false,                                 4 "eol": "2023-10-17",                       {
  2 "releaseDate": "2023-04-18",            5 "latest": "1.21.13",                  {                                         "extendedSupport": "2025-12-31"               5 "latest": "20.0.2.10.1",                   1 "cycle": "21",
  3 "lts": "2023-10-24",                    6 "latestReleaseDate": "2024-08-06",    1 "cycle": "3.1",                       },                                              6 "latestReleaseDate": "2023-08-23",         3 "lts": true,
  4 "eol": "2026-04-30",                    3 "lts": false                            "supportedJavaVersions": "17 - 21",   {                                               3 "lts": false                               2 "releaseDate": "2023-09-19",
  5 "latest": "20.17.0",                    },                                      2 "releaseDate": "2023-05-18",          1 "cycle": "5.3",                               },                                           4 "eol": "2028-09-30",
  6 "latestReleaseDate": "2024-08-21",      {                                       4 "eol": "2024-05-18",                    "supportedJavaVersions": "8 - 21",            {                                            5 "latest": "21.0.4",
    "support": "2024-10-22"                 1 "cycle": "1.20",                      5 "latest": "3.1.12",                     "supportedJakartaEEVersions": "7 - 8",        1 "cycle": "19",                             6 "latestReleaseDate": "2024-07-16",
  },                                        2 "releaseDate": "2023-02-01",          6 "latestReleaseDate": "2024-05-23",    2 "releaseDate": "2020-10-27",                  2 "releaseDate": "2022-09-20",                 "extendedSupport": "2031-09-30"
  {                                         4 "eol": "2024-02-06",                  3 "lts": false,                         4 "eol": "2024-08-31",                          4 "eol": "2023-04-19",                       },
  1 "cycle": "19",                          5 "latest": "1.20.14",                    "extendedSupport": "2025-08-18"       3 "lts": true,                                  5 "latest": "19.0.2.7.1",                    {
  2 "releaseDate": "2022-10-18",            6 "latestReleaseDate": "2024-02-06",    },                                      5 "latest": "5.3.39",                           6 "latestReleaseDate": "2023-01-17",         1 "cycle": "20",
  4 "eol": "2023-06-01",                    3 "lts": false                          {                                       6 "latestReleaseDate": "2024-08-14",            3 "lts": false                               2 "releaseDate": "2023-03-21",
  5 "latest": "19.9.0",                     },                                      1 "cycle": "3.0",                         "extendedSupport": "2026-12-31"               },                                           4 "eol": "2023-09-19",
  6 "latestReleaseDate": "2023-04-10",                                                "supportedJavaVersions": "17 - 21",   },                                                                                           5 "latest": "20.0.2",
  3 "lts": false,                                                                   2 "releaseDate": "2022-11-24",          {                                                                                            6 "latestReleaseDate": "2023-07-18",
    "support": "2023-04-01"                                                         4 "eol": "2023-11-24",                  1 "cycle": "5.2",                                                                            3 "lts": false,
  },                                                                                5 "latest": "3.0.13",                     "supportedJavaVersions": "8, 11",                                                            "extendedSupport": false
                                                                                    6 "latestReleaseDate": "2023-11-23",      "supportedJakartaEEVersions": "N/A",                                                       },
                                                                                    3 "lts": false,                         2 "releaseDate": "2019-09-30",
                                                                                      "extendedSupport": "2025-02-24"       4 "eol": "2021-12-31",
                                                                                    },                                        "link": "https://github.com/spring-projects/spring-framework/releases/tag/v__LATEST__.RELEASE",
                                                                                                                            5 "latest": "5.2.25",
                                                                                                                            6 "latestReleaseDate": "2023-07-13",
                                                                                                                            3 "lts": false,
                                                                                                                              "extendedSupport": "2023-12-31"
*/
public class EndofLifeInfo  {

    @JsonProperty("cycle")
    private String cycle;

    @JsonProperty("releaseDate")
    private String releaseDate;

    @JsonProperty("lts")
    private String lts;

    @JsonProperty("eol")
    private String eol;

    @JsonProperty("latest")
    private String latest;

    @JsonProperty("latestReleaseDate")
    private String latestReleaseDate;

    // Getters and Setters

    public String getCycle() {
      return cycle;
    }
    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getLts() {
        return lts;
    }
    public void setLts(String lts) {
        this.lts = lts;
    }

    public String getEol() {
        return eol;
    }
    public void setEol(String eol) {
        this.eol = eol;
    }

    public String getLatest() {
        return latest;
    }
    public void setLatest(String latest) {
        this.latest = latest;
    }

    public String getLatestReleaseDate() {
        return latestReleaseDate;
    }
    public void setLatestReleaseDate(String latestReleaseDate) {
        this.latestReleaseDate = latestReleaseDate;
    }

    @Override
    public String toString() {
        return String.format("cycle=%s, releaseDate=%s, lts:%s, eol=%s, latest=%s, latestReleaseDate:%s",
        cycle,
        releaseDate,
        lts,
        eol,
        latest,
        latestReleaseDate);
    }
}
