package SolidityInfo;

public enum SolidityVersion {
    UNKNOWN(">=0.4.17 <0.9.0"),
    FROM_0_4_17_TO_0_5_8(">=0.4.17 <0.5.8"),
    FROM_0_5_9_TO_0_5_11(">=0.5.9 <0.5.11"),
    FROM_0_5_12_TO_0_5_15(">=0.5.12 <0.5.15"),
    FROM_0_4_17_TO_0_5_8_EXPERIMENTAL(">=0.4.17 <0.5.8 experimental"),
    FROM_0_5_9_TO_0_5_11_EXPERIMENTAL(">=0.5.9 <0.5.11 experimental"),
    FROM_0_5_12_TO_0_5_15_EXPERIMENTAL(">=0.5.12 <0.5.15 experimental"),
    FROM_0_6_0_TO_0_6_1(">=0.6.0 <0.6.1"),
    FROM_0_6_2_TO_LATEST(">=0.6.2 <0.9.0");

    private final String versionString;

    SolidityVersion(String versionString) {
        this.versionString = versionString;
    }

    public String getVersionString() {
        return versionString;
    }
}
