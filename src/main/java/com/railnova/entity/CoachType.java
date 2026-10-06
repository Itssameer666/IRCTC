package com.railnova.entity;

public enum CoachType {
    FIRST_AC("1A", "AC First Class"),
    SECOND_AC("2A", "AC 2 Tier"),
    THIRD_AC("3A", "AC 3 Tier"),
    SLEEPER("SL", "Sleeper Class"),
    CHAIR_CAR("CC", "AC Chair Car"),
    EXECUTIVE_CHAIR_CAR("EC", "Exec Chair Car"),
    SECOND_SITTING("2S", "Second Sitting");

    private final String code;
    private final String displayName;

    CoachType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CoachType fromCode(String code) {
        for (CoachType ct : values()) {
            if (ct.getCode().equalsIgnoreCase(code) || ct.name().equalsIgnoreCase(code)) {
                return ct;
            }
        }
        return SLEEPER;
    }
}
