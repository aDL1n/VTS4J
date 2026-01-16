package dev.adlin.vts4j;

import dev.adlin.vts4j.core.event.Listener;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VTSClientBuilder {
    private final List<Listener> listeners = new ArrayList<>();
    private URI websocketAddress;

    public static VTSClientBuilder create() {
        return new VTSClientBuilder();
    }

    public VTSClientBuilder setAddress(URI address) {
        this.websocketAddress = address;
        return this;
    }

    public VTSClientBuilder registerListeners(Listener... listeners) {
        if (listeners == null) return this;

        Collections.addAll(this.listeners, listeners);
        return this;
    }

    public VTSClient build() {
        VTSClient client = websocketAddress != null
                ? new VTSClient(websocketAddress) : new VTSClient();

        if (!listeners.isEmpty())
            listeners.forEach(client::registerEventListener);


        return client;
    }
}
