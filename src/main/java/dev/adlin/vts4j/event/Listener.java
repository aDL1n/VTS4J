package dev.adlin.vts4j.event;

/**
 * A marker interface used to discover and register event listener classes
 * within the {@link EventHandler}.
 * <p>
 * Classes implementing this interface serve as containers for methods annotated
 * with {@link EventListener}. For a method to be successfully registered as an active
 * subscriber, it must satisfy the following criteria:
 * <ul>
 *     <li>The enclosing class must implement this {@code Listener} interface.</li>
 *     <li>The method must be annotated with {@link EventListener}.</li>
 *     <li>The method must be public and non-static.</li>
 *     <li>The method must accept exactly one parameter which inherits from the {@link Event} class.</li>
 * </ul>
 * </p>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>{@code
 * public class MyListener implements Listener {
 *
 *     @EventListener(priority = EventPriority.HIGH)
 *     public void onModelLoaded(ModelLoadedEvent event) {
 *         // Handle the event here
 *     }
 * }
 * }</pre>
 *
 * @see EventListener
 * @see EventHandler
 * @see EventPriority
 */
public interface Listener {
}
