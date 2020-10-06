package net.smelly.disparser.feedback.exceptions;

/**
 * A simple interface used for creating new instances of an exception.
 * Ideally all types implementing this interface can be used as builders for an exception.
 * Types implementing this interface can add more create methods if they wish.
 *
 * @param <E> The type of exception to create.
 * @author Luke Tonon
 */
public interface CommandExceptionCreator<E extends Exception> {
	E create();
}
