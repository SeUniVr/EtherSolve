package main;

import opcodes.Opcode;

public class SecurityDetection {
    private final SecurityVulnerability vulnerability;
    private final Opcode location;
    private final String message;

    public SecurityDetection(SecurityVulnerability vulnerability, Opcode location) {
        this(vulnerability, location, "");
    }

    public SecurityDetection(SecurityVulnerability vulnerability, Opcode location, String message) {
        this.vulnerability = vulnerability;
        this.location = location;
        this.message = message;
    }

    public SecurityVulnerability getVulnerability() {
        return vulnerability;
    }

    public Opcode getLocation() {
        return location;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("%s at opcode %s - %s", vulnerability.getName(), location, message);
    }
}
