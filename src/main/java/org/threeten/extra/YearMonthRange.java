/*
 * Copyright (c) 2022-present, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.threeten.extra;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Month;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A range of {@link YearMonth year months}.
 * <p>
 * A {@code YearMonthRange} represents a range of months, from a start month to an end month.
 * Instances can be constructed from either a half-open or a closed range of months.
 * Internally, the class stores the start and end months, with the start inclusive and the end exclusive.
 * The end month is always greater than or equal to the start month.
 * <p>
 * The constants {@code YearMonthRange.MIN_YEARMONTH} and {@code YearMonthRange.MAX_YEARMONTH} can be used
 * to indicate an unbounded far-past or far-future. Note that there is no difference
 * between a half-open and a closed range when the end is {@code YearMonthRange.MAX_YEARMONTH}.
 * Empty ranges are allowed.
 * <p>
 * No range can end at {@code YearMonthRange.MIN_YEARMONTH} or {@code YearMonthRange.MIN_YEARMONTH.plusMonths(1)}.
 * No range can start at {@code YearMonthRange.MAX_YEARMONTH} or {@code YearMonthRange.MAX_YEARMONTH.minusMonths(1)}.
 * No empty range can exist at {@code YearMonthRange.MIN_YEARMONTH} or {@code YearMonthRange.MAX_YEARMONTH}.
 * <p>
 * Year month ranges are not comparable. To compare the length of two ranges, it is
 * generally recommended to compare the number of months they contain.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 *
 * @see LocalDateRange
 */
public final class YearMonthRange
        implements Serializable {

    /**
     * The minimum supported year month, 'January -999,999,999'.
     */
    public static final YearMonth MIN_YEARMONTH = YearMonth.of(Year.MIN_VALUE, Month.JANUARY);
    /**
     * The maximum supported year month, 'December +999,999,999'.
     */
    public static final YearMonth MAX_YEARMONTH = YearMonth.of(Year.MAX_VALUE, Month.DECEMBER);
    /**
     * The month after the MIN month.
     */
    private static final YearMonth MINP1 = YearMonthRange.MIN_YEARMONTH.plusMonths(1);
    /**
     * The month before the MAX month.
     */
    private static final YearMonth MAXM1 = YearMonthRange.MAX_YEARMONTH.minusMonths(1);
    /**
     * A range over the whole time-line.
     */
    public static final YearMonthRange ALL = new YearMonthRange(YearMonthRange.MIN_YEARMONTH, YearMonthRange.MAX_YEARMONTH);

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 3358656715467L;

    /**
     * The start month (inclusive).
     */
    private final YearMonth start;
    /**
     * The end month (exclusive).
     */
    private final YearMonth end;

    //-----------------------------------------------------------------------
    /**
     * Obtains a half-open range of months, including the start and excluding the end.
     * <p>
     * The range includes the start month and excludes the end month, unless the end is {@code YearMonthRange.MAX_YEARMONTH}.
     * The end month must be equal to or after the start month.
     * This definition permits an empty range located at a specific month.
     * <p>
     * The constants {@code YearMonthRange.MIN_YEARMONTH} and {@code YearMonthRange.MAX_YEARMONTH} can be used
     * to indicate an unbounded far-past or far-future.
     * <p>
     * The start inclusive month must not be {@code YearMonthRange.MAX_YEARMONTH} or {@code YearMonthRange.MAX_YEARMONTH.minusMonths(1)}.
     * The end inclusive month must not be {@code YearMonthRange.MIN_YEARMONTH} or {@code YearMonthRange.MIN_YEARMONTH.plusMonths(1)}.
     * No empty range can exist at {@code YearMonthRange.MIN_YEARMONTH} or {@code YearMonthRange.MAX_YEARMONTH}.
     *
     * @param startInclusive  the inclusive start month, not null
     * @param endExclusive  the exclusive end month, not null
     * @return the half-open range, not null
     * @throws DateTimeException if the end is before the start,
     *   or the start month is {@code YearMonthRange.MAX_YEARMONTH} or {@code YearMonthRange.MAX_YEARMONTH.minusMonths(1)},
     *   or the end month is {@code YearMonthRange.MIN_YEARMONTH} or {@code YearMonthRange.MIN_YEARMONTH.plusMonths(1)}
     */
    public static YearMonthRange of(YearMonth startInclusive, YearMonth endExclusive) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(endExclusive, "endExclusive");
        return new YearMonthRange(startInclusive, endExclusive);
    }

    /**
     * Obtains a closed range of months, including the start and end.
     * <p>
     * The range includes the start month and the end month.
     * The end month must be equal to or after the start month.
     * <p>
     * The constants {@code YearMonthRange.MIN_YEARMONTH} and {@code YearMonthRange.MAX_YEARMONTH} can be used
     * to indicate an unbounded far-past or far-future. In addition, an end month of
     * {@code YearMonthRange.MAX_YEARMONTH.minusMonths(1)} will also create an unbounded far-future range.
     * <p>
     * The start inclusive month must not be {@code YearMonthRange.MAX_YEARMONTH} or {@code YearMonthRange.MAX_YEARMONTH.minusMonths(1)}.
     * The end inclusive month must not be {@code YearMonthRange.MIN_YEARMONTH}.
     *
     * @param startInclusive  the inclusive start month, not null
     * @param endInclusive  the inclusive end month, not null
     * @return the closed range
     * @throws DateTimeException if the end is before the start,
     *   or the start month is {@code YearMonthRange.MAX_YEARMONTH} or {@code YearMonthRange.MAX_YEARMONTH.minusMonths(1)},
     *   or the end month is {@code YearMonthRange.MIN_YEARMONTH}
     */
    public static YearMonthRange ofClosed(YearMonth startInclusive, YearMonth endInclusive) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(endInclusive, "endInclusive");
        if (endInclusive.isBefore(startInclusive)) {
            throw new DateTimeException("Start month must be on or before end month");
        }
        YearMonth end = (endInclusive.equals(YearMonthRange.MAX_YEARMONTH) ? YearMonthRange.MAX_YEARMONTH : endInclusive.plusMonths(1));
        return new YearMonthRange(startInclusive, end);
    }

    /**
     * Obtains an instance of {@code YearMonthRange} from the start and a period.
     * <p>
     * The end month is calculated as the start plus the duration.
     * The period must not be negative.
     * <p>
     * The constant {@code YearMonthRange.MIN_YEARMONTH} can be used to indicate an unbounded far-past.
     * <p>
     * The period must not be zero or one month when the start month is {@code YearMonthRange.MIN_YEARMONTH}.
     *
     * @param startInclusive  the inclusive start month, not null
     * @param period  the period from the start to the end, not null
     * @return the range, not null
     * @throws DateTimeException if the end is before the start,
     *  or if the period addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs when adding the period
     */
    public static YearMonthRange of(YearMonth startInclusive, Period period) {
        Objects.requireNonNull(startInclusive, "startInclusive");
        Objects.requireNonNull(period, "period");
        if (period.isNegative()) {
            throw new DateTimeException("Period must not be zero or negative");
        }
        return new YearMonthRange(startInclusive, startInclusive.plus(period));
    }

    /**
     * Obtains an empty month range located at the specified month.
     * <p>
     * The empty range has zero length and contains no other months or ranges.
     * An empty range cannot be located at {@code YearMonthRange.MIN_YEARMONTH}, {@code YearMonthRange.MIN_YEARMONTH.plusMonths(1)},
     * {@code YearMonthRange.MAX_YEARMONTH} or {@code YearMonthRange.MAX_YEARMONTH.minusMonths(1)}.
     *
     * @param month  the month where the empty range is located, not null
     * @return the empty range, not null
     * @throws DateTimeException if the month is {@code YearMonthRange.MIN_YEARMONTH}, {@code YearMonthRange.MIN_YEARMONTH.plusMonths(1)},
     *   {@code YearMonthRange.MAX_YEARMONTH} or {@code YearMonthRange.MAX_YEARMONTH.minusMonths(1)}
     */
    public static YearMonthRange ofEmpty(YearMonth month) {
        Objects.requireNonNull(month, "month");
        return new YearMonthRange(month, month);
    }

    /**
     * Obtains a range that is unbounded at the start and end.
     *
     * @return the range, with an unbounded start and unbounded end
     */
    public static YearMonthRange ofUnbounded() {
        return ALL;
    }

    /**
     * Obtains a range up to, but not including, the specified end month.
     * <p>
     * The range includes all months from the unbounded start, denoted by {@code YearMonthRange.MIN_YEARMONTH}, to the end month.
     * The end month is exclusive and cannot be {@code YearMonthRange.MIN_YEARMONTH} or {@code YearMonthRange.MIN_YEARMONTH.plusMonths(1)}.
     *
     * @param endExclusive  the exclusive end month, {@code YearMonthRange.MAX_YEARMONTH} treated as unbounded, not null
     * @return the range, with an unbounded start
     * @throws DateTimeException if the end month is {@code YearMonthRange.MIN_YEARMONTH} or  {@code YearMonthRange.MIN_YEARMONTH.plusMonths(1)}
     */
    public static YearMonthRange ofUnboundedStart(YearMonth endExclusive) {
        return YearMonthRange.of(YearMonthRange.MIN_YEARMONTH, endExclusive);
    }

    /**
     * Obtains a range from and including the specified start month.
     * <p>
     * The range includes all months from the start month to the unbounded end, denoted by {@code YearMonthRange.MAX_YEARMONTH}.
     * The start month is inclusive and cannot be {@code YearMonthRange.MAX_YEARMONTH} or {@code YearMonthRange.MAX_YEARMONTH.minusMonths(1)}.
     *
     * @param startInclusive  the inclusive start month, {@code YearMonthRange.MIN_YEARMONTH} treated as unbounded, not null
     * @return the range, with an unbounded end
     * @throws DateTimeException if the start month is {@code YearMonthRange.MAX_YEARMONTH} or {@code YearMonthRange.MAX_YEARMONTH.minusMonths(1)}
     */
    public static YearMonthRange ofUnboundedEnd(YearMonth startInclusive) {
        return YearMonthRange.of(startInclusive, YearMonthRange.MAX_YEARMONTH);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearMonthRange} from a text string such as
     * {@code 2007-12/2007-12}, where the end month is exclusive.
     * <p>
     * The string must consist of one of the following three formats:
     * <ul>
     * <li>a representations of an {@link YearMonth}, followed by a forward slash,
     *  followed by a representation of a {@link YearMonth}
     * <li>a representation of an {@link YearMonth}, followed by a forward slash,
     *  followed by a representation of a {@link Period}
     * <li>a representation of a {@link Period}, followed by a forward slash,
     *  followed by a representation of an {@link YearMonth}
     * </ul>
     *
     * @param text  the text to parse, not null
     * @return the parsed range, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    @FromString
    public static YearMonthRange parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '/') {
                char firstChar = text.charAt(0);
                if (firstChar == 'P' || firstChar == 'p') {
                    // period followed by month
                    Period duration = Period.parse(text.subSequence(0, i));
                    YearMonth end = YearMonth.parse(text.subSequence(i + 1, text.length()));
                    return YearMonthRange.of(end.minus(duration), end);
                } else {
                    // month followed by month or period
                    YearMonth start = YearMonth.parse(text.subSequence(0, i));
                    if (i + 1 < text.length()) {
                        char c = text.charAt(i + 1);
                        if (c == 'P' || c == 'p') {
                            Period duration = Period.parse(text.subSequence(i + 1, text.length()));
                            return YearMonthRange.of(start, start.plus(duration));
                        }
                    }
                    YearMonth end = YearMonth.parse(text.subSequence(i + 1, text.length()));
                    return YearMonthRange.of(start, end);
                }
            }
        }
        throw new DateTimeParseException("YearMonthRange cannot be parsed, no forward slash found", text, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param startInclusive  the start month, inclusive, validated not null
     * @param endExclusive  the end month, exclusive, validated not null
     */
    private YearMonthRange(YearMonth startInclusive, YearMonth endExclusive) {
        if (endExclusive.isBefore(startInclusive)) {
            throw new DateTimeException("End month must be on or after start month");
        }
        if (startInclusive.equals(MAXM1)) {
            throw new DateTimeException("Range must not start at YearMonthRange.MAX_YEARMONTH.minusMonths(1)");
        }
        if (endExclusive.equals(MINP1)) {
            throw new DateTimeException("Range must not end at YearMonthRange.MIN_YEARMONTH.plusMonths(1)");
        }
        if (endExclusive.equals(YearMonthRange.MIN_YEARMONTH) || startInclusive.equals(YearMonthRange.MAX_YEARMONTH)) {
            throw new DateTimeException("Empty range must not be at YearMonthRange.MIN_YEARMONTH or YearMonthRange.MAX_YEARMONTH");
        }
        this.start = startInclusive;
        this.end = endExclusive;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the start month of this range, inclusive.
     * <p>
     * This will return {@code YearMonthRange#MIN_YEARMONTH} if the range is unbounded at the start.
     * In this case, the range includes all months into the far-past.
     * <p>
     * This never returns {@code YearMonthRange.MAX_YEARMONTH} or {@code YearMonthRange.MAX_YEARMONTH.minusMonths(1)}.
     *
     * @return the start month
     */
    public YearMonth getStart() {
        return start;
    }

    /**
     * Gets the end month of this range, exclusive.
     * <p>
     * This will return {@code YearMonthRange.MAX_YEARMONTH} if the range is unbounded at the end.
     * In this case, the range includes all months into the far-future.
     * <p>
     * This never returns {@code YearMonthRange.MIN_YEARMONTH} or {@code YearMonthRange.MIN_YEARMONTH.plusMonths(1)}.
     *
     * @return the end month, exclusive
     */
    public YearMonth getEnd() {
        return end;
    }

    /**
     * Gets the end month of this range, inclusive.
     * <p>
     * This will return {@code YearMonthRange.MAX_YEARMONTH} if the range is unbounded at the end.
     * In this case, the range includes all months into the far-future.
     * <p>
     * This returns the month before the end month.
     * <p>
     * This never returns {@code YearMonthRange.MIN_YEARMONTH}.
     * 
     * @return the end month, inclusive
     */
    public YearMonth getEndInclusive() {
        if (isUnboundedEnd()) {
            return YearMonthRange.MAX_YEARMONTH;
        }
        return end.minusMonths(1);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the range is empty.
     * <p>
     * An empty range occurs when the start month equals the end month.
     * <p>
     * An empty range is never unbounded.
     * 
     * @return true if the range is empty
     */
    public boolean isEmpty() {
        return start.equals(end);
    }

    /**
     * Checks if the start of the range is unbounded.
     * <p>
     * An unbounded range is never empty.
     * 
     * @return true if start is unbounded
     */
    public boolean isUnboundedStart() {
        return start.equals(YearMonthRange.MIN_YEARMONTH);
    }

    /**
     * Checks if the end of the range is unbounded.
     * <p>
     * An unbounded range is never empty.
     * 
     * @return true if end is unbounded
     */
    public boolean isUnboundedEnd() {
        return end.equals(YearMonthRange.MAX_YEARMONTH);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this range with the start month adjusted.
     * <p>
     * This returns a new instance with the start month altered.
     * Since {@code YearMonth} implements {@code TemporalAdjuster} any
     * month can simply be passed in.
     * <p>
     * For example, to adjust the start to one year earlier:
     * <pre>
     *  range = range.withStart(month -&gt; month.minus(1, ChronoUnit.YEARS));
     * </pre>
     * 
     * @param adjuster  the adjuster to use, not null
     * @return a copy of this range with the start month adjusted
     * @throws DateTimeException if the new start month is after the current end month
     */
    public YearMonthRange withStart(TemporalAdjuster adjuster) {
        return YearMonthRange.of(start.with(adjuster), end);
    }

    /**
     * Returns a copy of this range with the end month adjusted.
     * <p>
     * This returns a new instance with the exclusive end month altered.
     * Since {@code YearMonth} implements {@code TemporalAdjuster} any
     * month can simply be passed in.
     * <p>
     * For example, to adjust the end to one year later:
     * <pre>
     *  range = range.withEnd(month -&gt; month.plus(1, ChronoUnit.YEARS));
     * </pre>
     * 
     * @param adjuster  the adjuster to use, not null
     * @return a copy of this range with the end month adjusted
     * @throws DateTimeException if the new end month is before the current start month
     */
    public YearMonthRange withEnd(TemporalAdjuster adjuster) {
        return YearMonthRange.of(start, end.with(adjuster));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this range contains the specified month.
     * <p>
     * This checks if the specified month is within the bounds of this range.
     * If this range is empty then this method always returns false.
     * Else if this range has an unbounded start then {@code contains(YearMonthRange#MIN_YEARMONTH)} returns true.
     * Else if this range has an unbounded end then {@code contains(YearMonthRange#MAX_YEARMONTH)} returns true.
     * 
     * @param month  the month to check for, not null
     * @return true if this range contains the month
     */
    public boolean contains(YearMonth month) {
        Objects.requireNonNull(month, "month");
        return start.compareTo(month) <= 0 && (month.compareTo(end) < 0 || isUnboundedEnd());
    }

    /**
     * Checks if this range encloses the specified range.
     * <p>
     * This checks if the bounds of the specified range are within the bounds of this range.
     * An empty range encloses itself.
     * 
     * @param other  the other range to check for, not null
     * @return true if this range contains all months in the other range
     */
    public boolean encloses(YearMonthRange other) {
        Objects.requireNonNull(other, "other");
        return start.compareTo(other.start) <= 0 && other.end.compareTo(end) <= 0;
    }

    /**
     * Checks if this range abuts the specified range.
     * <p>
     * The result is true if the end of this range is the start of the other, or vice versa.
     * An empty range does not abut itself.
     *
     * @param other  the other range, not null
     * @return true if this range abuts the other range
     */
    public boolean abuts(YearMonthRange other) {
        Objects.requireNonNull(other, "other");
        return end.equals(other.start) ^ start.equals(other.end);
    }

    /**
     * Checks if this range is connected to the specified range.
     * <p>
     * The result is true if the two ranges have an enclosed range in common, even if that range is empty.
     * An empty range is connected to itself.
     * <p>
     * This is equivalent to {@code (overlaps(other) || abuts(other))}.
     *
     * @param other  the other range, not null
     * @return true if this range is connected to the other range
     */
    public boolean isConnected(YearMonthRange other) {
        Objects.requireNonNull(other, "other");
        return this.equals(other) || (start.compareTo(other.end) <= 0 && other.start.compareTo(end) <= 0);
    }

    /**
     * Checks if this range overlaps the specified range.
     * <p>
     * The result is true if the two ranges share some part of the time-line.
     * An empty range overlaps itself.
     * <p>
     * This is equivalent to {@code (isConnected(other) && !abuts(other))}.
     *
     * @param other  the time range to compare to, null means a zero length range now
     * @return true if the time ranges overlap
     */
    public boolean overlaps(YearMonthRange other) {
        Objects.requireNonNull(other, "other");
        return other.equals(this) || (start.compareTo(other.end) < 0 && other.start.compareTo(end) < 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the range that is the intersection of this range and the specified range.
     * <p>
     * This finds the intersection of two ranges.
     * This throws an exception if the two ranges are not {@linkplain #isConnected(YearMonthRange) connected}.
     * 
     * @param other  the other range to check for, not null
     * @return the range that is the intersection of the two ranges
     * @throws DateTimeException if the ranges do not connect
     */
    public YearMonthRange intersection(YearMonthRange other) {
        Objects.requireNonNull(other, "other");
        if (isConnected(other) == false) {
            throw new DateTimeException("Ranges do not connect: " + this + " and " + other);
        }
        int cmpStart = start.compareTo(other.start);
        int cmpEnd = end.compareTo(other.end);
        if (cmpStart >= 0 && cmpEnd <= 0) {
            return this;
        } else if (cmpStart <= 0 && cmpEnd >= 0) {
            return other;
        } else {
            YearMonth newStart = (cmpStart >= 0 ? start : other.start);
            YearMonth newEnd = (cmpEnd <= 0 ? end : other.end);
            return YearMonthRange.of(newStart, newEnd);
        }
    }

    /**
     * Calculates the range that is the union of this range and the specified range.
     * <p>
     * This finds the union of two ranges.
     * This throws an exception if the two ranges are not {@linkplain #isConnected(YearMonthRange) connected}.
     * 
     * @param other  the other range to check for, not null
     * @return the range that is the union of the two ranges
     * @throws DateTimeException if the ranges do not connect
     */
    public YearMonthRange union(YearMonthRange other) {
        Objects.requireNonNull(other, "other");
        if (isConnected(other) == false) {
            throw new DateTimeException("Ranges do not connect: " + this + " and " + other);
        }
        int cmpStart = start.compareTo(other.start);
        int cmpEnd = end.compareTo(other.end);
        if (cmpStart >= 0 && cmpEnd <= 0) {
            return other;
        } else if (cmpStart <= 0 && cmpEnd >= 0) {
            return this;
        } else {
            YearMonth newStart = (cmpStart >= 0 ? other.start : start);
            YearMonth newEnd = (cmpEnd <= 0 ? other.end : end);
            return YearMonthRange.of(newStart, newEnd);
        }
    }

    /**
     * Calculates the smallest range that encloses this range and the specified range.
     * <p>
     * The result of this method will {@linkplain #encloses(YearMonthRange) enclose}
     * this range and the specified range.
     * 
     * @param other  the other range to check for, not null
     * @return the range that spans the two ranges
     */
    public YearMonthRange span(YearMonthRange other) {
        Objects.requireNonNull(other, "other");
        int cmpStart = start.compareTo(other.start);
        int cmpEnd = end.compareTo(other.end);
        YearMonth newStart = (cmpStart >= 0 ? other.start : start);
        YearMonth newEnd = (cmpEnd <= 0 ? other.end : end);
        return YearMonthRange.of(newStart, newEnd);
    }

    //-----------------------------------------------------------------------
    /**
     * Streams the set of months included in the range.
     * <p>
     * This returns a stream consisting of each month in the range.
     * The stream is ordered.
     * 
     * @return the stream of month from the start to the end
     */
    public Stream<YearMonth> stream() {
        long count = end.getLong(ChronoField.PROLEPTIC_MONTH) - start.getLong(ChronoField.PROLEPTIC_MONTH) + (isUnboundedEnd() ? 1 : 0);
        Spliterator<YearMonth> spliterator = new Spliterators.AbstractSpliterator<YearMonth>(
                count,
                Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.DISTINCT | Spliterator.ORDERED |
                        Spliterator.SORTED | Spliterator.SIZED | Spliterator.SUBSIZED) {

            private YearMonth current = start;
            
            @Override
            public boolean tryAdvance(Consumer<? super YearMonth> action) {
                if (current != null) {
                    if (current.isBefore(end)) {
                        action.accept(current);
                        current = current.plusMonths(1);
                        return true;
                    }
                    if (current.equals(YearMonthRange.MAX_YEARMONTH)) {
                        action.accept(YearMonthRange.MAX_YEARMONTH);
                        current = null;
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public Comparator<? super YearMonth> getComparator() {
                return null;
            }
        };
        return StreamSupport.stream(spliterator, false);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this range is after the specified month.
     * <p>
     * The result is true if every month in this range is after the specified month.
     * An empty range behaves as though it is a month for comparison purposes.
     *
     * @param month  the other month to compare to, not null
     * @return true if the start of this range is after the specified month
     */
    public boolean isAfter(YearMonth month) {
        return start.compareTo(month) > 0;
    }

    /**
     * Checks if this range is before the specified month.
     * <p>
     * The result is true if every month in this range is before the specified month.
     * An empty range behaves as though it is a month for comparison purposes.
     *
     * @param month  the other month to compare to, not null
     * @return true if the start of this range is before the specified month
     */
    public boolean isBefore(YearMonth month) {
        return end.compareTo(month) <= 0 && start.compareTo(month) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this range is after the specified range.
     * <p>
     * The result is true if every month in this range is after every month in the specified range.
     * An empty range behaves as though it is a month for comparison purposes.
     *
     * @param other  the other range to compare to, not null
     * @return true if every month in this range is after every month in the other range
     */
    public boolean isAfter(YearMonthRange other) {
        return start.compareTo(other.end) >= 0 && !other.equals(this);
    }

    /**
     * Checks if this range is before the specified range.
     * <p>
     * The result is true if every month in this range is before every month in the specified range.
     * An empty range behaves as though it is a month for comparison purposes.
     *
     * @param range  the other range to compare to, not null
     * @return true if every month in this range is before every month in the other range
     */
    public boolean isBefore(YearMonthRange range) {
        return end.compareTo(range.start) <= 0 && !range.equals(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains the length of this range in months.
     * <p>
     * This returns the number of months between the start and end months.
     * If the range is too large, the length will be {@code Integer.MAX_VALUE}.
     * Unbounded ranges return {@code Integer.MAX_VALUE}.
     *
     * @return the length in months, Integer.MAX_VALUE if unbounded or too large
     */
    public int lengthInMonths() {
        if (isUnboundedStart() || isUnboundedEnd()) {
            return Integer.MAX_VALUE;
        }
        long length = end.getLong(ChronoField.PROLEPTIC_MONTH) - start.getLong(ChronoField.PROLEPTIC_MONTH);
        return length > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) length;
    }

    /**
     * Obtains the length of this range as a period.
     * <p>
     * This returns the {@link Period} between the start and end months.
     * Unbounded ranges throw {@link ArithmeticException}.
     *
     * @return the period of the range
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Period},
     *   or the range is unbounded
     */
    public Period toPeriod() {
        if (isUnboundedStart() || isUnboundedEnd()) {
            throw new ArithmeticException("Unbounded range cannot be converted to a Period");
        }
        long months = start.until(end, ChronoUnit.MONTHS);
        if (months > Integer.MAX_VALUE){
            throw new ArithmeticException();
        }
        return Period.ofMonths((int) months);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this range is equal to another range.
     * <p>
     * Compares this {@code YearMonthRange} with another ensuring that the two months are the same.
     * Only objects of type {@code YearMonthRange} are compared, other types return false.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other range
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof YearMonthRange) {
            YearMonthRange other = (YearMonthRange) obj;
            return start.equals(other.start) && end.equals(other.end);
        }
        return false;
    }

    /**
     * A hash code for this range.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return start.hashCode() ^ end.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this range as a {@code String}, such as {@code 2007-12/2007-12}.
     * <p>
     * The output will be the ISO-8601 format formed by combining the
     * {@code toString()} methods of the two months, separated by a forward slash.
     *
     * @return a string representation of this month, not null
     */
    @Override
    @ToString
    public String toString() {
        return start.toString() + '/' + end.toString();
    }

}
