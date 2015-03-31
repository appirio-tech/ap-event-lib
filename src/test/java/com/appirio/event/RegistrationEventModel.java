package com.appirio.event;

import java.util.Date;

/**
 * Created by thabo on 3/31/15.
 */
public class RegistrationEventModel {
    private Integer programId;
    private String handle;
    private Date timestamp;

    public RegistrationEventModel() {
    }

    public RegistrationEventModel(Integer programId, String handle, Date timestamp) {
        this.programId = programId;
        this.handle = handle;
        this.timestamp = timestamp;
    }

    public Integer getProgramId() {
        return programId;
    }
    public String getHandle() {
        return handle;
    }
    public  Date getTimestamp() {
        return timestamp;
    }
}


