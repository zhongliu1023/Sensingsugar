package me.sensingself.sensingsugar.Common.FragmentsBus;

/**
 * Created by liujie on 3/5/18.
 */

public class FragmentsEvents {

    private String eventName;

    public FragmentsEvents(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}