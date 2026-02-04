package dev.adlin.vts4j;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.event.Event;
import dev.adlin.vts4j.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface VTSClient {

    VTSClient connect();

    VTSClient awaitConnect();

    void awaitConnect(long timeout, final @NotNull TimeUnit timeUnit);

    void disconnect();

    void disconnectBlocking();

    /**
     * Sends an authentication request with the specified plugin name and author.
     * @param pluginMeta
     * @return authentication token
     */
    @NotNull String authenticate(final @NotNull PluginMeta pluginMeta);

    void authenticate(final @NotNull PluginMeta pluginMeta, final @NotNull String authToken);

    /**
     * Sends a request to the server and returns a CompletableFuture to dispatch the response.
     * The request is converted to JSON format and sent via a socket.
     * The returned CompletableFuture can be used to obtain the server's response once it is available.
     * @param request object to be sent to the server. This object will be serialized to JSON
     * @return response from the server
     */
    CompletableFuture<Response> sendRequest(final @NotNull Request request);

    /**
     * Sends a request with an event type and
     * returns a CompletableFuture that can be used to get the server's response
     * and determine the status of the operation.
     *
     * @param eventClass the class of event you need to subscribe
     * @param eventConfig additional configuration for the event
     *                    (more details at <a href="https://github.com/DenchiSoft/VTubeStudio/tree/master/Events#events">this page</a>)
     * @return CompletableFuture with a response about the success of the operation
     */
    CompletableFuture<Response> subscribe(@NotNull Class<? extends Event> eventClass, @Nullable JsonObject eventConfig);

    /**
     * Sends a request with an eventName and config,
     * and returns a CompletableFuture that can be used to get the server's response
     * and determine the status of the operation.
     * @param eventClass the class of event you need to subscribe
     * @return CompletableFuture with a response about the success of the operation
     */
    CompletableFuture<Response> subscribe(@NotNull Class<? extends Event> eventClass);

    CompletableFuture<Response> unsubscribe(@NotNull Class<? extends Event> eventClass, @Nullable JsonObject eventConfig);

    CompletableFuture<Response> unsubscribe(@NotNull Class<? extends Event> eventClass);

    /**
     * Registers event listener.
     * <p>
     * The registered listener will receive notifications for events that
     * have been subscribed to using the {@link #subscribe(Class<? extends Event>, JsonObject)}
     * or {@link #subscribe(Class<? extends Event>)} method.
     *
     * @param listener an instance of a class implementing the {@link Listener} interface.
     */
    void registerEventListener(final @NotNull Listener listener);
}
