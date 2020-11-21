package net.smelly.disparser.properties;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

/**
 * This interface works as a property key for a command, and an annotation processor.
 * <p>Since all commands are immutable for thread-safety, information about configurations for them must be stored externally.</p>
 * <p>All types implementing this interface <b>MUST</b> also be immutable.</p>
 *
 * @param <T> The type for the property.
 * @param <A> The annotation for this property, e.g. {@link net.smelly.disparser.annotations.Aliases} for {@link AliasesProperty}.
 * @author Luke Tonon
 * @see AliasesProperty
 * @see PermissionsProperty
 */
@Immutable
public interface CommandProperty<T, A extends Annotation> {
	T get(@Nullable A annotation);

	/**
	 * A class extending {@link AtomicReference} for holding the value of a {@link CommandProperty} key.
	 * Objects passed into this class are <b>NOT</b> guaranteed to be thread-safe, so it is required to have all types passed into this class be immutable.
	 * <p>Even though this object is considered thread-safe, there are still ways to cause inconsistencies if the methods offered by {@link AtomicReference} are not used when best suited.</p>
	 *
	 * @param <T> The type of the reference.
	 * @author Luke Tonon
	 * @see CommandPropertyMap
	 */
	@ThreadSafe
	class Value<T> extends AtomicReference<T> {

		private Value(T value) {
			super(value);
		}

		/**
		 * Creates a new {@link Value} instance with an initial value.
		 *
		 * @param value The value to set. This should also be immutable!
		 * @param <T>   The type of the value.
		 * @return A new {@link Value} instance with an initial value.
		 */
		public static <T> Value<T> create(T value) {
			return new Value<>(value);
		}

		/**
		 * Creates a new {@link Value} instance with an initial value from {@link CommandProperty#get(Annotation)} with null passed in as the annotation, this just means default value of a property.
		 *
		 * @param property The {@link CommandProperty} to get the initial value from.
		 * @param <T>      The type of the value.
		 * @param <P>      The type of the {@link CommandProperty}.
		 * @return A new {@link Value} instance with an initial value from {@link CommandProperty#get(Annotation)} with null passed in as the annotation
		 */
		public static <T, P extends CommandProperty<T, ?>> Value<T> create(P property) {
			return new Value<>(property.get(null));
		}

		/**
		 * Unsafely assigns a new value to this {@link Value}.
		 * This should only be used for dynamic scenarios where the property type is unknown.
		 *
		 * @param object The object to set the new value as.
		 */
		@SuppressWarnings("unchecked")
		public void unsafeSet(Object object) {
			this.set((T) object);
		}

		/**
		 * Unsafely gets and updates a value for this {@link Value}.
		 *
		 * @param unaryOperator A {@link UnaryOperator} to operate over the value.
		 * @return The updated value.
		 * @see #getAndUpdate(UnaryOperator)
		 */
		@SuppressWarnings("unchecked")
		public T unsafeGetAndUpdate(UnaryOperator<Object> unaryOperator) {
			T prev, next;
			do {
				prev = this.get();
				next = (T) unaryOperator.apply(prev);
			} while (!this.compareAndSet(prev, next));
			return prev;
		}

		@Override
		public String toString() {
			return "CommandProperty$Value{" +
					"value=" + this.get() +
					'}';
		}
	}
}
