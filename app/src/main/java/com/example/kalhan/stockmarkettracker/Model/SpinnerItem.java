package com.example.kalhan.stockmarkettracker.Model;

/**
 * Created by Kalhan on 11/22/2017.
 */

public class SpinnerItem {
    public SpinnerItem(String type, Boolean enabled) {
        this.type = type;
        this.enabled = enabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    private String type;
    private Boolean enabled;
}
