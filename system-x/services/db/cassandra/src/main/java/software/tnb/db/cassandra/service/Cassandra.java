package software.tnb.db.cassandra.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.db.cassandra.account.CassandraAccount;
import software.tnb.db.cassandra.validation.CassandraValidation;

import com.datastax.oss.driver.api.core.CqlSession;

import java.util.Map;

public abstract class Cassandra extends Service<CassandraAccount, CqlSession, CassandraValidation> implements WithDockerImage {

    public static final int CASSANDRA_PORT = 9042;

    public String defaultImage() {
        // official library image required hacks in openshift, bitnami works out of the box
        return "quay.io/fuse_qe/cassandra:4.0.7";
    }

    public CassandraValidation validation() {
        if (validation == null) {
            validation = new CassandraValidation(client());
        }
        return validation;
    }

    public abstract int port();

    public abstract String host();

    public String cassandraUrl(String keyspace) {
        return String.format("cql:%s:%s/%s?username=%s&password=%s", host(), port(), keyspace, account().username(), account().password());
    }

    public Map<String, String> containerEnvironment() {
        return Map.of(
            "CASSANDRA_USER", account().username(),
            "CASSANDRA_PASSWORD", account().password(),
            "CASSANDRA_PASSWORD_SEEDER", "yes"
        );
    }
}
