package vm.vagrant.configuration.builder;

import vm.vagrant.configuration.*;
import vm.vagrant.configuration.builder.util.VagrantBuilderException;
import vm.vagrant.util.ChecksumType;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VagrantVmConfigBuilder implements Serializable {
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

    public VagrantVmConfigBuilder() {
        vagrantProvisionerConfigs = new ArrayList<VagrantProvisionerConfig>();
        vagrantNetworkConfigs = new ArrayList<VagrantNetworkConfig>();
        vagrantSyncedFolderConfigs = new ArrayList<VagrantSyncedFolderConfig>();
        name = UUID.randomUUID().toString();
        bootTimeout = 300;
        boxCheckUpdate = true;
        boxDownloadInsecure = false;
        boxDownloadLocationTrusted = false;
        gracefulHaltTimeout = 60;
        usablePortRange = "2200..3200";
    }

    public static VagrantVmConfigBuilder create() {
        return new VagrantVmConfigBuilder();
    }

    public VagrantVmConfigBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public VagrantVmConfigBuilder withHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public VagrantVmConfigBuilder withBoxName(String boxName) {
        this.boxName = boxName;
        return this;
    }

    public VagrantVmConfigBuilder withBoxUrl(URL boxUrl) {
        this.boxUrl = boxUrl;
        return this;
    }

    public VagrantVmConfigBuilder withVagrantProvisionerConfig(VagrantProvisionerConfig vagrantProvisionerConfigs) {
        this.vagrantProvisionerConfigs.add(vagrantProvisionerConfigs);
        return this;
    }

    public VagrantVmConfigBuilder withVagrantNetworkConfig(VagrantNetworkConfig vagrantNetworkConfigs) {
        this.vagrantNetworkConfigs.add(vagrantNetworkConfigs);
        return this;
    }

    public VagrantVmConfigBuilder withVagrantSyncedFolderConfig(VagrantSyncedFolderConfig vagrantSyncedFolderConfigs) {
        this.vagrantSyncedFolderConfigs.add(vagrantSyncedFolderConfigs);
        return this;
    }

    public VagrantVmConfigBuilder withBootTimeout(Integer bootTimeout) {
        this.bootTimeout = bootTimeout;
        return this;
    }

    public VagrantVmConfigBuilder withBoxCheckUpdate(Boolean boxCheckUpdate) {
        this.boxCheckUpdate = boxCheckUpdate;
        return this;
    }

    public VagrantVmConfigBuilder withBoxDownloadChecksum(String boxDownloadChecksum) {
        this.boxDownloadChecksum = boxDownloadChecksum;
        return this;
    }

    public VagrantVmConfigBuilder withBoxDownloadChecksumType(ChecksumType boxDownloadChecksumType) {
        this.boxDownloadChecksumType = boxDownloadChecksumType;
        return this;
    }

    public VagrantVmConfigBuilder withBoxDownloadClientCert(File boxDownloadClientCert) {
        this.boxDownloadClientCert = boxDownloadClientCert;
        return this;
    }

    public VagrantVmConfigBuilder withBoxDownloadCaCert(File boxDownloadCaCert) {
        this.boxDownloadCaCert = boxDownloadCaCert;
        return this;
    }

    public VagrantVmConfigBuilder withBoxDownloadCaPath(File boxDownloadCaPath) {
        this.boxDownloadCaPath = boxDownloadCaPath;
        return this;
    }

    public VagrantVmConfigBuilder withBoxDownloadInsecure(Boolean boxDownloadInsecure) {
        this.boxDownloadInsecure = boxDownloadInsecure;
        return this;
    }

    public VagrantVmConfigBuilder withBoxDownloadLocationTrusted(Boolean boxDownloadLocationTrusted) {
        this.boxDownloadLocationTrusted = boxDownloadLocationTrusted;
        return this;
    }

    public VagrantVmConfigBuilder withBoxVersion(String boxVersion) {
        this.boxVersion = boxVersion;
        return this;
    }

    public VagrantVmConfigBuilder withCommunicator(String communicator) {
        this.communicator = communicator;
        return this;
    }

    public VagrantVmConfigBuilder withGracefulHaltTimeout(Integer gracefulHaltTimeout) {
        this.gracefulHaltTimeout = gracefulHaltTimeout;
        return this;
    }

    public VagrantVmConfigBuilder withGuest(String guest) {
        this.guest = guest;
        return this;
    }

    public VagrantVmConfigBuilder withPostUpMessage(String postUpMessage) {
        this.postUpMessage = postUpMessage;
        return this;
    }

    public VagrantVmConfigBuilder withUsablePortRange(String usablePortRange) {
        this.usablePortRange = usablePortRange;
        return this;
    }

    public VagrantVmConfigBuilder withProvider(VagrantProviderConfig provider) {
        this.provider = provider;
        return this;
    }

    public VagrantVmConfig build() {
        if (boxName == null && !boxName.isEmpty()) throw new VagrantBuilderException("No boxName defined");
        if (provider == null) throw new VagrantBuilderException("No provider defined");
        return new VagrantVmConfig(name, hostName, boxName, boxUrl, vagrantProvisionerConfigs, vagrantNetworkConfigs, vagrantSyncedFolderConfigs, bootTimeout, boxCheckUpdate, boxDownloadChecksum, boxDownloadChecksumType, boxDownloadClientCert, boxDownloadCaCert, boxDownloadCaPath, boxDownloadInsecure, boxDownloadLocationTrusted, boxVersion, communicator, gracefulHaltTimeout, guest, postUpMessage, usablePortRange, provider);
    }
}
