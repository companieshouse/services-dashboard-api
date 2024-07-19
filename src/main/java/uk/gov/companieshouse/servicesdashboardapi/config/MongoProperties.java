package uk.gov.companieshouse.servicesdashboardapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mongo")
public class MongoProperties {
   
    private String protocol;
    private String user;
    private String password;
    private String hostandport;
    private String dbname;
    private String collectionName;

    // Getters and Setters
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getHostandport() { return hostandport; }
    public void setHostandport(String hostandport) { this.hostandport = hostandport; }

    public String getDbname() { return dbname; }
    public void setDbname(String dbname) { this.dbname = dbname; }

    public String getCollectionName() { return collectionName; }
    public void setCollectionName(String collectionName) { this.collectionName = collectionName; }
}
