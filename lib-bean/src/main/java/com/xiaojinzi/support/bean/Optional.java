
package com.xiaojinzi.support.bean;

import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Copy from {@link java.util.Optional}
 * 因为 {@link java.util.Optional} 在目前的低版本没法使用, 所以复制一个出来做一个删减, 用一用
 * 这类还是很好用的, 表示一些空的对象. 因为有些地方对象不能为空, 但是要表示空. 这个类的作用就来了
 */
public final class Optional<T> {
  /**
   * Common instance for {@code empty()}.
   */
  private static final Optional<?> EMPTY = new Optional<>();

  /**
   * If non-null, the value; if null, indicates no value is present
   */
  private final T value;

  /**
   * Constructs an empty instance.
   *
   * @implNote Generally only one empty instance, {@link Optional#EMPTY},
   * should exist per VM.
   */
  private Optional() {
    this.value = null;
  }

  /**
   * Returns an empty {@code Optional} instance.  No value is present for this
   * Optional.
   *
   * @param <T> Type of the non-existent value
   * @return an empty {@code Optional}
   * @apiNote Though it may be tempting to do so, avoid testing if an object
   * is empty by comparing with {@code ==} against instances returned by
   * {@code Option.empty()}. There is no guarantee that it is a singleton.
   * Instead, use {@link #isPresent()}.
   */
  public static <T> Optional<T> empty() {
    @SuppressWarnings("unchecked")
    Optional<T> t = (Optional<T>) EMPTY;
    return t;
  }

  /**
   * Constructs an instance with the value present.
   *
   * @param value the non-null value to be present
   * @throws NullPointerException if value is null
   */
  private Optional(T value) {
    this.value = Objects.requireNonNull(value);
  }

  /**
   * Returns an {@code Optional} with the specified present non-null value.
   *
   * @param <T> the class of the value
   * @param value the value to be present, which must be non-null
   * @return an {@code Optional} with the value present
   * @throws NullPointerException if value is null
   */
  public static <T> Optional<T> of(T value) {
    return new Optional<>(value);
  }

  /**
   * Returns an {@code Optional} describing the specified value, if non-null,
   * otherwise returns an empty {@code Optional}.
   *
   * @param <T> the class of the value
   * @param value the possibly-null value to describe
   * @return an {@code Optional} with a present value if the specified value
   * is non-null, otherwise an empty {@code Optional}
   */
  public static <T> Optional<T> ofNullable(T value) {
    return value == null ? empty() : of(value);
  }

  /**
   * If a value is present in this {@code Optional}, returns the value,
   * otherwise throws {@code NoSuchElementException}.
   *
   * @return the non-null value held by this {@code Optional}
   * @throws NoSuchElementException if there is no value present
   * @see Optional#isPresent()
   */
  public T get() {
    if (value == null) {
      throw new NoSuchElementException("No value present");
    }
    return value;
  }

  /**
   * Return {@code true} if there is a value present, otherwise {@code false}.
   *
   * @return {@code true} if there is a value present, otherwise {@code false}
   */
  public boolean isPresent() {
    return value != null;
  }

  /**
   * Return the value if present, otherwise return {@code other}.
   *
   * @param other the value to be returned if there is no value present, may
   * be null
   * @return the value, if present, otherwise {@code other}
   */
  @Nullable
  public T orElse(@Nullable T other) {
    return value != null ? value : other;
  }

  /**
   * Indicates whether some other object is "equal to" this Optional. The
   * other object is considered equal if:
   * <ul>
   * <li>it is also an {@code Optional} and;
   * <li>both instances have no value present or;
   * <li>the present values are "equal to" each other via {@code equals()}.
   * </ul>
   *
   * @param obj an object to be tested for equality
   * @return {code true} if the other object is "equal to" this object
   * otherwise {@code false}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Optional)) {
      return false;
    }
    Optional<?> other = (Optional<?>) obj;
    return (value == other.value) || (value != null && value.equals(other.value));
  }

  /**
   * Returns the hash code value of the present value, if any, or 0 (zero) if
   * no value is present.
   *
   * @return hash code value of the present value or 0 if no value is present
   */
  @Override
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
  }

  /**
   * Returns a non-empty string representation of this Optional suitable for
   * debugging. The exact presentation format is unspecified and may vary
   * between implementations and versions.
   *
   * @return the string representation of this instance
   * @implSpec If a value is present the result must include its string
   * representation in the result. Empty and present Optionals must be
   * unambiguously differentiable.
   */
  @Override
  public String toString() {
    return value != null
        ? String.format("Optional[%s]", value)
        : "Optional.empty";
  }
}
