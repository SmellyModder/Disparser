package net.smelly.disparser.feedback.exceptions;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A builder interface for creating new instances of an exception.
 * Ideally all types implementing this interface can be used as builders for an exception.
 * Types implementing this interface can add more create methods if they wish.
 *
 * @param <E> The type of exception to create.
 * @author Luke Tonon
 */
@ThreadSafe
public interface ExceptionCreator<E extends Exception> {
	/**
	 * Creates the exception with no arguments.
	 *
	 * @return A default created exception.
	 */
	E create();
}
