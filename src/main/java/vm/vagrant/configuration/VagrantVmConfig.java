package vm.vagrant.configuration;

import vm.vagrant.util.ChecksumType;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

/**
 * A configuration class that can be used to define and create a VM in Vagrant.
 *
 * @author oliver.ziegert
 */
public class VagrantVmConfig implements Serializable {

    private String name;
    private String hostName;
    private String boxName;
    private URL boxUrl;
    private List<VagrantProvisionerConfig> vagrantProvisionerConfigs;
    private List<VagrantNetworkConfig> vagrantNetworkConfigs;
    private List<VagrantSyncedFolderConfig> vagrantSyncedFolderConfigs;
    private Integer bootTimeout;
    private Boolean boxCheckUpdate;
    private String boxDownloadChecksum;
    private ChecksumType boxDownloadChecksumType;
    private File boxDownloadClientCert;
    private File boxDownloadCaCert;
    private File boxDownloadCaPath;
    private Boolean boxDownloadInsecure;
    private Boolean boxDownloadLocationTrusted;
    private String boxVersion;
    private String communicator;
    private Integer gracefulHaltTimeout;
    private String guest;
    private String postUpMessage;
    private String usablePortRange;
    private VagrantProviderConfig provider;

    public VagrantVmConfig(String name, String hostName, String boxName, URL boxUrl, List<VagrantProvisionerConfig> vagrantProvisionerConfigs, List<VagrantNetworkConfig> vagrantNetworkConfigs, List<VagrantSyncedFolderConfig> vagrantSyncedFolderConfigs, Integer bootTimeout, Boolean boxCheckUpdate, String boxDownloadChecksum, ChecksumType boxDownloadChecksumType, File boxDownloadClientCert, File boxDownloadCaCert, File boxDownloadCaPath, Boolean boxDownloadInsecure, Boolean boxDownloadLocationTrusted, String boxVersion, String communicator, Integer gracefulHaltTimeout, String guest, String postUpMessage, String usablePortRange, VagrantProviderConfig provider) {
        this.name = name;
        this.hostName = hostName;
        this.boxName = boxName;
        this.boxUrl = boxUrl;
        this.vagrantProvisionerConfigs = vagrantProvisionerConfigs;
        this.vagrantNetworkConfigs = vagrantNetworkConfigs;
        this.vagrantSyncedFolderConfigs = vagrantSyncedFolderConfigs;
        this.bootTimeout = bootTimeout;
        this.boxCheckUpdate = boxCheckUpdate;
        this.boxDownloadChecksum = boxDownloadChecksum;
        this.boxDownloadChecksumType = boxDownloadChecksumType;
        this.boxDownloadClientCert = boxDownloadClientCert;
        this.boxDownloadCaCert = boxDownloadCaCert;
        this.boxDownloadCaPath = boxDownloadCaPath;
        this.boxDownloadInsecure = boxDownloadInsecure;
        this.boxDownloadLocationTrusted = boxDownloadLocationTrusted;
        this.boxVersion = boxVersion;
        this.communicator = communicator;
        this.gracefulHaltTimeout = gracefulHaltTimeout;
        this.guest = guest;
        this.postUpMessage = postUpMessage;
        this.usablePortRange = usablePortRange;
        this.provider = provider;
    }

    public String name() {
        return name;
    }

    public String hostName() {
        return hostName;
    }

    public String boxName() {
        return boxName;
    }

    public URL boxUrl() {
        return boxUrl;
    }

    public List<VagrantProvisionerConfig> vagrantProvisionerConfigs() {
        return vagrantProvisionerConfigs;
    }

    public List<VagrantNetworkConfig> vagrantNetworkConfigs() {
        return vagrantNetworkConfigs;
    }

    public List<VagrantSyncedFolderConfig> vagrantSyncedFolderConfigs() {
        return vagrantSyncedFolderConfigs;
    }

    public Integer bootTimeout() {
        return bootTimeout;
    }

    public Boolean boxCheckUpdate() {
        return boxCheckUpdate;
    }

    public String boxDownloadChecksum() {
        return boxDownloadChecksum;
    }

    public ChecksumType boxDownloadChecksumType() {
        return boxDownloadChecksumType;
    }

    public File boxDownloadClientCert() {
        return boxDownloadClientCert;
    }

    public File boxDownloadCaCert() {
        return boxDownloadCaCert;
    }

    public File boxDownloadCaPath() {
        return boxDownloadCaPath;
    }

    public Boolean boxDownloadInsecure() {
        return boxDownloadInsecure;
    }

    public Boolean boxDownloadLocationTrusted() {
        return boxDownloadLocationTrusted;
    }

    public String boxVersion() {
        return boxVersion;
    }

    public String communicator() {
        return communicator;
    }

    public Integer gracefulHaltTimeout() {
        return gracefulHaltTimeout;
    }

    public String guest() {
        return guest;
    }

    public String postUpMessage() {
        return postUpMessage;
    }

    public String usablePortRange() {
        return usablePortRange;
    }

    public VagrantProviderConfig provider() {
        return provider;
    }
}
