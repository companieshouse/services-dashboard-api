package uk.gov.companieshouse.servicesdashboardapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mongo")
public class MongoProperties {

    @Value("${mongo.user.secret}")
    private String user;

    @Value("${mongo.password.secret}")
    private String password;

    @Value("${mongo.hostandport.secret}")
    private String hostandport;

    @Value("${mongo.protocol.secret}")
    private String protocol;

    @Value("${mongo.dbname.secret}")
    private String dbname;

    private String collectionNameProj;
    private String collectionNameConf;

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

    public String getCollectionNameProj() { return collectionNameProj; }
    public void setCollectionNameProj(String collectionName) { this.collectionNameProj = collectionName; }

    public String getCollectionNameConf() { return collectionNameConf; }
    public void setCollectionNameConf(String collectionName) { this.collectionNameConf = collectionName; }
}
