package dev.adlin.vts4j.core.event.impl;

import dev.adlin.vts4j.core.event.Event;
import dev.adlin.vts4j.core.event.data.TestEventData;

public class TestEvent extends Event<TestEventData> {

    public TestEvent(TestEventData data) {
        super(data);
    }

}
