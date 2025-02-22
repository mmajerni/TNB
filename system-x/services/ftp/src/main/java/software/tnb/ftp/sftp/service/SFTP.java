package software.tnb.ftp.sftp.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.ftp.common.FileTransferService;
import software.tnb.ftp.sftp.account.SFTPAccount;
import software.tnb.ftp.sftp.validation.SFTPValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import net.schmizz.sshj.sftp.SFTPClient;

public abstract class SFTP implements FileTransferService, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(SFTP.class);

    private SFTPAccount account;
    private SFTPValidation validation;

    public abstract SFTPClient client();

    @Override
    public int port() {
        return 22;
    }

    @Override
    public SFTPAccount account() {
        if (account == null) {
            account = AccountFactory.create(SFTPAccount.class);
        }
        return account;
    }

    @Override
    public SFTPValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Ftp validation");
            validation = new SFTPValidation(account(), client());
        }
        return validation;
    }

    public Map<String, String> containerEnvironment() {
        return Map.of(
            "SFTP_USERS", String.format("%s:%s:::%s", account().username(), account().password(), account().baseDir())
        );
    }

    public String defaultImage() {
        return "quay.io/fuse_qe/sftp:alpine";
    }
}
