package dev.adlin.vts4j.event;

/**
 * A marker interface representing the root of all system and network-driven events.
 * <p>
 * Any class that acts as a dispatchable message or data payload within the event bus
 * must implement this interface.
 * </p>
 * <p>
 * To ensure integration across the entire event architecture, custom implementations
 * should adhere to the following ecosystem requirements:
 * <ul>
 *     <li>Must be registered bidirectionally in the {@link EventRegistry} to support JSON serialization.</li>
 *     <li>Can be subscribed to via methods marked with {@link EventListener} inside a {@link Listener} class.</li>
 *     <li>Are dispatched synchronously by passing the instance to {@link EventHandler#callEvent(Event)}.</li>
 * </ul>
 * </p>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>{@code
 * public final class ModelLoadedEvent implements Event {
 *     private final String modelId;
 *
 *     public ModelLoadedEvent(String modelId) {
 *         this.modelId = modelId;
 *     }
 *
 *     public String getModelId() {
 *         return modelId;
 *     }
 * }
 * }</pre>
 *
 * @see EventHandler
 * @see EventRegistry
 * @see Listener
 */
public interface Event {
}
