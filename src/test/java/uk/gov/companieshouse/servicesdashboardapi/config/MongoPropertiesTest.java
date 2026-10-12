package uk.gov.companieshouse.servicesdashboardapi.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MongoPropertiesTest {

    @Test
    void shouldSetAndGetUser() {
        MongoProperties properties = new MongoProperties();
        properties.setUser("testUser");
        assertThat(properties.getUser()).isEqualTo("testUser");
    }

    @Test
    void shouldSetAndGetPassword() {
        MongoProperties properties = new MongoProperties();
        properties.setPassword("testPassword");
        assertThat(properties.getPassword()).isEqualTo("testPassword");
    }

    @Test
    void shouldSetAndGetHostandport() {
        MongoProperties properties = new MongoProperties();
        properties.setHostandport("localhost:27017");
        assertThat(properties.getHostandport()).isEqualTo("localhost:27017");
    }

    @Test
    void shouldSetAndGetProtocol() {
        MongoProperties properties = new MongoProperties();
        properties.setProtocol("mongodb");
        assertThat(properties.getProtocol()).isEqualTo("mongodb");
    }

    @Test
    void shouldSetAndGetDbname() {
        MongoProperties properties = new MongoProperties();
        properties.setDbname("testDb");
        assertThat(properties.getDbname()).isEqualTo("testDb");
    }

    @Test
    void shouldSetAndGetCollectionNameProj() {
        MongoProperties properties = new MongoProperties();
        properties.setCollectionNameProj("projects");
        assertThat(properties.getCollectionNameProj()).isEqualTo("projects");
    }

    @Test
    void shouldSetAndGetCollectionNameConf() {
        MongoProperties properties = new MongoProperties();
        properties.setCollectionNameConf("configs");
        assertThat(properties.getCollectionNameConf()).isEqualTo("configs");
    }

    @Test
    void shouldHandleNullUserValue() {
        MongoProperties properties = new MongoProperties();
        properties.setUser(null);
        assertThat(properties.getUser()).isNull();
    }

    @Test
    void shouldHandleNullPasswordValue() {
        MongoProperties properties = new MongoProperties();
        properties.setPassword(null);
        assertThat(properties.getPassword()).isNull();
    }

    @Test
    void shouldHandleNullHostandportValue() {
        MongoProperties properties = new MongoProperties();
        properties.setHostandport(null);
        assertThat(properties.getHostandport()).isNull();
    }

    @Test
    void shouldHandleNullProtocolValue() {
        MongoProperties properties = new MongoProperties();
        properties.setProtocol(null);
        assertThat(properties.getProtocol()).isNull();
    }

    @Test
    void shouldHandleNullDbnameValue() {
        MongoProperties properties = new MongoProperties();
        properties.setDbname(null);
        assertThat(properties.getDbname()).isNull();
    }

    @Test
    void shouldHandleNullCollectionNameProj() {
        MongoProperties properties = new MongoProperties();
        properties.setCollectionNameProj(null);
        assertThat(properties.getCollectionNameProj()).isNull();
    }

    @Test
    void shouldHandleNullCollectionNameConf() {
        MongoProperties properties = new MongoProperties();
        properties.setCollectionNameConf(null);
        assertThat(properties.getCollectionNameConf()).isNull();
    }

    @Test
    void shouldHandleEmptyStringValues() {
        MongoProperties properties = new MongoProperties();
        properties.setUser("");
        properties.setPassword("");
        properties.setHostandport("");
        properties.setProtocol("");
        properties.setDbname("");
        properties.setCollectionNameProj("");
        properties.setCollectionNameConf("");

        assertThat(properties.getUser()).isEmpty();
        assertThat(properties.getPassword()).isEmpty();
        assertThat(properties.getHostandport()).isEmpty();
        assertThat(properties.getProtocol()).isEmpty();
        assertThat(properties.getDbname()).isEmpty();
        assertThat(properties.getCollectionNameProj()).isEmpty();
        assertThat(properties.getCollectionNameConf()).isEmpty();
    }

    @Test
    void shouldAllowMultiplePropertiesSetIndependently() {
        MongoProperties properties = new MongoProperties();
        properties.setUser("user1");
        properties.setPassword("pass1");
        properties.setHostandport("host1:27017");
        properties.setProtocol("mongodb");
        properties.setDbname("db1");
        properties.setCollectionNameProj("proj1");
        properties.setCollectionNameConf("conf1");

        assertThat(properties.getUser()).isEqualTo("user1");
        assertThat(properties.getPassword()).isEqualTo("pass1");
        assertThat(properties.getHostandport()).isEqualTo("host1:27017");
        assertThat(properties.getProtocol()).isEqualTo("mongodb");
        assertThat(properties.getDbname()).isEqualTo("db1");
        assertThat(properties.getCollectionNameProj()).isEqualTo("proj1");
        assertThat(properties.getCollectionNameConf()).isEqualTo("conf1");
    }

    @Test
    void shouldAllowOverwritingExistingValues() {
        MongoProperties properties = new MongoProperties();
        properties.setUser("firstUser");
        properties.setUser("secondUser");

        assertThat(properties.getUser()).isEqualTo("secondUser");
    }

    @Test
    void shouldHandleSpecialCharactersInValues() {
        MongoProperties properties = new MongoProperties();
        String specialChars = "user@host$db#123!";
        properties.setUser(specialChars);

        assertThat(properties.getUser()).isEqualTo(specialChars);
    }

    @Test
    void shouldHandleWhitespaceInValues() {
        MongoProperties properties = new MongoProperties();
        properties.setUser("  spaced  user  ");
        properties.setPassword("  spaced  password  ");

        assertThat(properties.getUser()).isEqualTo("  spaced  user  ");
        assertThat(properties.getPassword()).isEqualTo("  spaced  password  ");
    }
}
