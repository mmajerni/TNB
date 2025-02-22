package software.tnb.google.cloud.bigquery.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.Service;
import software.tnb.google.cloud.bigquery.validation.BigQueryValidation;
import software.tnb.google.cloud.common.account.GoogleCloudAccount;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auto.service.AutoService;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@AutoService(GoogleBigQuery.class)
public class GoogleBigQuery implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleBigQuery.class);

    private GoogleCloudAccount account;
    private BigQueryValidation validation;

    private BigQuery client;

    public GoogleCloudAccount account() {
        if (account == null) {
            account = AccountFactory.create(GoogleCloudAccount.class);
        }
        return account;
    }

    protected BigQuery client() throws IOException {
        if (client == null) {
            LOG.debug("Creating new Google BigQuery client");
            try {
                client =
                    BigQueryOptions.newBuilder().setCredentials(credentialsProvider().getCredentials()).build().getService();
            } catch (Exception e) {
                throw new RuntimeException("Unable to create new Google Storage client", e);
            }
        }
        return client;
    }

    private CredentialsProvider credentialsProvider() throws IOException {
        InputStream serviceAccountKey = new ByteArrayInputStream(Base64.getDecoder().decode(account().serviceAccountKey()));
        return FixedCredentialsProvider.create(GoogleCredentials.fromStream(serviceAccountKey));
    }

    public BigQueryValidation validation() {
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating Google BigQuery validation");
        validation = new BigQueryValidation(client(), account().projectId());
    }
}
