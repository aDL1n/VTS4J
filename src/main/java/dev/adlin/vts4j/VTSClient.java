package dev.adlin.vts4j;

import com.google.gson.JsonObject;
import dev.adlin.vts4j.entity.Request;
import dev.adlin.vts4j.entity.Response;
import dev.adlin.vts4j.event.Event;
import dev.adlin.vts4j.event.Listener;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The primary high-level client interface for interacting with the VTube Studio Public API.
 * <p>
 * Provides full control over the connection lifecycle, token authentication, request dispatching,
 * and event subscription management workflows.
 * </p>
 *
 * @see PluginMeta
 * @see Response
 * @see Request
 * @see Event
 * @see Listener
 */
public interface VTSClient {

    /**
     * Initiates a non-blocking background connection routine to the remote VTube Studio server.
     *
     * @return this client instance for fluent method chaining
     */
    VTSClient connect();

    /**
     * Establishes a synchronous connection to the remote server, blocking the calling thread
     * indefinitely until the connection handshake completes or fails.
     *
     * @return this client instance for fluent method chaining
     */
    VTSClient awaitConnect();

    /**
     * Establishes a synchronous connection to the remote server, blocking the calling thread
     * up to the specified timeout interval.
     *
     * @param timeout  the maximum duration to wait for a connection handshake
     * @param timeUnit the time unit of the timeout argument
     * @return this client instance for fluent method chaining
     */
    VTSClient awaitConnect(long timeout, final @NonNull TimeUnit timeUnit);

    /**
     * Initiates an asynchronous, non-blocking disconnection routine to close the socket session.
     */
    void disconnect();

    /**
     * Synchronously tears down the active socket session, blocking the calling thread
     * until the close handshake is acknowledged.
     */
    void awaitDisconnect();

    /**
     * Requests a brand new authentication token from VTube Studio for the specified plugin metadata.
     * <p>
     * This will trigger a pop-up confirmation window inside the VTube Studio UI for the end-user.
     * </p>
     *
     * @param pluginMeta the metadata configuration of the plugin requesting access
     * @return a {@link CompletableFuture} that resolves to the newly issued unique authentication token string
     */
    @NonNull
    CompletableFuture<String> authenticate(final @NonNull PluginMeta pluginMeta);

    /**
     * Authenticates the plugin session instantly using a previously saved existing authentication token.
     *
     * @param pluginMeta          the metadata configuration of the plugin
     * @param authenticationToken the pre-issued token string to authenticate with
     */
    void authenticate(final @NonNull PluginMeta pluginMeta, final @NonNull String authenticationToken);

    /**
     * Serializes and dispatches an asynchronous request packet to the server over the active socket session.
     *
     * @param request the request packet structure to be transmitted
     * @return a {@link CompletableFuture} containing the server's asynchronous {@link Response} package
     */
    CompletableFuture<Response> sendRequest(final @NonNull Request request);

    /**
     * Subscribes the current plugin session to a specific system event type using extended configuration arguments.
     * <p>
     * Re-subscribing to an already active event type will seamlessly overwrite its previous parameters.
     * For details regarding available event names and payloads, see the
     * <a href="https://github.com">VTube Studio Events Guide</a>.
     * </p>
     *
     * @param eventClass  the class type of the target {@link Event} to subscribe to
     * @param eventConfig custom JSON configuration properties and filters specific to the event type
     * @return a {@link CompletableFuture} containing the server response acknowledgment indicating operation status
     */
    CompletableFuture<Response> subscribe(
            final @NonNull Class<? extends Event> eventClass,
            final @NonNull JsonObject eventConfig
    );

    /**
     * Subscribes the current plugin session to a specific system event type using default configurations.
     *
     * @param eventClass the class type of the target {@link Event} to subscribe to
     * @return a {@link CompletableFuture} containing the server response acknowledgment indicating operation status
     */
    CompletableFuture<Response> subscribe(final @NonNull Class<? extends Event> eventClass);

    /**
     * Unsubscribes the current plugin session from a specific system event matching the provided configuration filters.
     *
     * @param eventClass  the class type of the target {@link Event} to unsubscribe from
     * @param eventConfig custom JSON configuration parameters to match for removal
     * @return a {@link CompletableFuture} containing the server response acknowledgment indicating operation status
     */
    CompletableFuture<Response> unsubscribe(
            final @NonNull Class<? extends Event> eventClass,
            final @NonNull JsonObject eventConfig
    );

    /**
     * Unsubscribes the current plugin session from all instances of the specified system event type.
     *
     * @param eventClass the class type of the target {@link Event} to unsubscribe from
     * @return a {@link CompletableFuture} containing the server response acknowledgment indicating operation status
     */
    CompletableFuture<Response> unsubscribe(final @NonNull Class<? extends Event> eventClass);

    /**
     * Registers an event listener object within the client's internal event bus.
     * <p>
     * The registered instance will intercept notifications for any system events that have
     * been activated using the {@code subscribe} methods.
     * </p>
     *
     * @param listener an instance of a custom class implementing the marker {@link Listener} interface
     */
    void registerEventListener(final @NonNull Listener listener);
}
