/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
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

import com.google.common.collect.Range;
import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test year month range.
 */
public class TestYearMonthRange {

    private static final YearMonth MINP1 = YearMonthRange.MIN_YEARMONTH.plusMonths(1);
    private static final YearMonth MINP2 = YearMonthRange.MIN_YEARMONTH.plusMonths(2);
    private static final YearMonth MINP3 = YearMonthRange.MIN_YEARMONTH.plusMonths(3);
    private static final YearMonth MAXM1 = YearMonthRange.MAX_YEARMONTH.minusMonths(1);
    private static final YearMonth MAXM2 = YearMonthRange.MAX_YEARMONTH.minusMonths(2);
    private static final YearMonth MONTH_2012_01 = YearMonth.of(2012, 1);
    private static final YearMonth MONTH_2012_07 = YearMonth.of(2012, 7);
    private static final YearMonth MONTH_2012_08 = YearMonth.of(2012, 8);
    private static final YearMonth MONTH_2012_09 = YearMonth.of(2012, 9);
    private static final YearMonth MONTH_2012_10 = YearMonth.of(2012, 10);
    private static final YearMonth MONTH_2012_11 = YearMonth.of(2012, 11);
    private static final YearMonth MONTH_2012_12 = YearMonth.of(2012, 12);
    private static final YearMonth MONTH_2013_01 = YearMonth.of(2013, 1);
    private static final YearMonth MONTH_2013_12 = YearMonth.of(2013, 12);


    //-----------------------------------------------------------------------
    @Test
    public void test_ALL() {
        YearMonthRange test = YearMonthRange.ALL;
        assertEquals(YearMonthRange.MIN_YEARMONTH, test.getStart());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEndInclusive());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(true, test.isUnboundedEnd());
        assertEquals("-999999999-01/999999999-12", test.toString());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_07, MONTH_2012_09);
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(MONTH_2012_08, test.getEndInclusive());
        assertEquals(MONTH_2012_09, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals(2, test.lengthInMonths());
        assertEquals("2012-07/2012-09", test.toString());
    }

    @Test
    public void test_of_MIN() {
        YearMonthRange test = YearMonthRange.of(YearMonthRange.MIN_YEARMONTH, MONTH_2012_08);
        assertEquals(YearMonthRange.MIN_YEARMONTH, test.getStart());
        assertEquals(MONTH_2012_07, test.getEndInclusive());
        assertEquals(MONTH_2012_08, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals(Integer.MAX_VALUE, test.lengthInMonths());
        assertEquals("-999999999-01/2012-08", test.toString());
    }

    @Test
    public void test_of_MAX() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_07, YearMonthRange.MAX_YEARMONTH);
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEndInclusive());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(true, test.isUnboundedEnd());
        assertEquals(Integer.MAX_VALUE, test.lengthInMonths());
        assertEquals("2012-07/999999999-12", test.toString());
    }

    @Test
    public void test_of_MIN_MAX() {
        YearMonthRange test = YearMonthRange.of(YearMonthRange.MIN_YEARMONTH, YearMonthRange.MAX_YEARMONTH);
        assertEquals(YearMonthRange.MIN_YEARMONTH, test.getStart());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEndInclusive());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(true, test.isUnboundedEnd());
        assertEquals(Integer.MAX_VALUE, test.lengthInMonths());
        assertEquals("-999999999-01/999999999-12", test.toString());
    }

    @Test
    public void test_of_MIN_MIN() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.of(YearMonthRange.MIN_YEARMONTH, YearMonthRange.MIN_YEARMONTH));
    }

    @Test
    public void test_of_MIN_MINP1() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.of(YearMonthRange.MIN_YEARMONTH, MINP1));
    }

    @Test
    public void test_of_MINP1_MINP1() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.of(MINP1, MINP1));
    }

    @Test
    public void test_of_MIN_MINP2() {
        YearMonthRange test = YearMonthRange.of(YearMonthRange.MIN_YEARMONTH, MINP2);
        assertEquals(YearMonthRange.MIN_YEARMONTH, test.getStart());
        assertEquals(MINP1, test.getEndInclusive());
        assertEquals(MINP2, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals(Integer.MAX_VALUE, test.lengthInMonths());
        assertEquals("-999999999-01/-999999999-03", test.toString());
    }

    @Test
    public void test_of_MINP1_MINP2() {
        YearMonthRange test = YearMonthRange.of(MINP1, MINP2);
        assertEquals(MINP1, test.getStart());
        assertEquals(MINP1, test.getEndInclusive());
        assertEquals(MINP2, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals(1, test.lengthInMonths());
        assertEquals("-999999999-02/-999999999-03", test.toString());
    }

    @Test
    public void test_of_MINP2_MINP2() {
        YearMonthRange test = YearMonthRange.of(MINP2, MINP2);
        assertEquals(MINP2, test.getStart());
        assertEquals(MINP1, test.getEndInclusive());
        assertEquals(MINP2, test.getEnd());
        assertEquals(true, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals(0, test.lengthInMonths());
        assertEquals("-999999999-03/-999999999-03", test.toString());
    }

    @Test
    public void test_of_MAX_MAX() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.of(YearMonthRange.MAX_YEARMONTH, YearMonthRange.MAX_YEARMONTH));
    }

    @Test
    public void test_of_MAXM1_MAX() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.of(MAXM1, YearMonthRange.MAX_YEARMONTH));
    }

    @Test
    public void test_of_MAXM1_MAXM1() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.of(MAXM1, MAXM1));
    }

    @Test
    public void test_of_empty() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, MONTH_2012_08);
        assertEquals(MONTH_2012_08, test.getStart());
        assertEquals(MONTH_2012_07, test.getEndInclusive());
        assertEquals(MONTH_2012_08, test.getEnd());
        assertEquals(true, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals(0, test.lengthInMonths());
        assertEquals("2012-08/2012-08", test.toString());
    }

    @Test
    public void test_of_badOrder() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.of(MONTH_2012_08, MONTH_2012_07));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ofClosed() {
        YearMonthRange test = YearMonthRange.ofClosed(MONTH_2012_07, MONTH_2012_08);
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(MONTH_2012_08, test.getEndInclusive());
        assertEquals(MONTH_2012_09, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals(2, test.lengthInMonths());
        assertEquals("2012-07/2012-09", test.toString());
    }

    @Test
    public void test_ofClosed_MIN() {
        YearMonthRange test = YearMonthRange.ofClosed(YearMonthRange.MIN_YEARMONTH, MONTH_2012_07);
        assertEquals(YearMonthRange.MIN_YEARMONTH, test.getStart());
        assertEquals(MONTH_2012_07, test.getEndInclusive());
        assertEquals(MONTH_2012_08, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals(Integer.MAX_VALUE, test.lengthInMonths());
        assertEquals("-999999999-01/2012-08", test.toString());
    }

    @Test
    public void test_ofClosed_MAX() {
        YearMonthRange test = YearMonthRange.ofClosed(MONTH_2012_07, YearMonthRange.MAX_YEARMONTH);
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEndInclusive());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(true, test.isUnboundedEnd());
        assertEquals(Integer.MAX_VALUE, test.lengthInMonths());
        assertEquals("2012-07/999999999-12", test.toString());
    }

    @Test
    public void test_ofClosed_MIN_MAX() {
        YearMonthRange test = YearMonthRange.ofClosed(YearMonthRange.MIN_YEARMONTH, YearMonthRange.MAX_YEARMONTH);
        assertEquals(YearMonthRange.MIN_YEARMONTH, test.getStart());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEndInclusive());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(true, test.isUnboundedEnd());
        assertEquals(Integer.MAX_VALUE, test.lengthInMonths());
        assertEquals("-999999999-01/999999999-12", test.toString());
    }

    @Test
    public void test_ofClosed_MIN_MIN() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.ofClosed(YearMonthRange.MIN_YEARMONTH, YearMonthRange.MIN_YEARMONTH));
    }

    @Test
    public void test_ofClosed_MIN_MINP1() {
        YearMonthRange test = YearMonthRange.ofClosed(YearMonthRange.MIN_YEARMONTH, MINP1);
        assertEquals(YearMonthRange.MIN_YEARMONTH, test.getStart());
        assertEquals(MINP1, test.getEndInclusive());
        assertEquals(MINP2, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals(Integer.MAX_VALUE, test.lengthInMonths());
        assertEquals("-999999999-01/-999999999-03", test.toString());
    }

    @Test
    public void test_ofClosed_MINP1_MINP1() {
        YearMonthRange test = YearMonthRange.ofClosed(MINP1, MINP1);
        assertEquals(MINP1, test.getStart());
        assertEquals(MINP1, test.getEndInclusive());
        assertEquals(MINP2, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals(1, test.lengthInMonths());
        assertEquals("-999999999-02/-999999999-03", test.toString());
    }

    @Test
    public void test_ofClosed_MIN_MINP2() {
        YearMonthRange test = YearMonthRange.ofClosed(YearMonthRange.MIN_YEARMONTH, MINP2);
        assertEquals(YearMonthRange.MIN_YEARMONTH, test.getStart());
        assertEquals(MINP2, test.getEndInclusive());
        assertEquals(MINP3, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals(Integer.MAX_VALUE, test.lengthInMonths());
        assertEquals("-999999999-01/-999999999-04", test.toString());
    }

    @Test
    public void test_ofClosed_MINP1_MINP2() {
        YearMonthRange test = YearMonthRange.ofClosed(MINP1, MINP2);
        assertEquals(MINP1, test.getStart());
        assertEquals(MINP2, test.getEndInclusive());
        assertEquals(MINP3, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals(2, test.lengthInMonths());
        assertEquals("-999999999-02/-999999999-04", test.toString());
    }

    @Test
    public void test_ofClosed_MINP2_MINP2() {
        YearMonthRange test = YearMonthRange.ofClosed(MINP2, MINP2);
        assertEquals(MINP2, test.getStart());
        assertEquals(MINP2, test.getEndInclusive());
        assertEquals(MINP3, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals(1, test.lengthInMonths());
        assertEquals("-999999999-03/-999999999-04", test.toString());
    }

    @Test
    public void test_ofClosed_MAX_MAX() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.ofClosed(YearMonthRange.MAX_YEARMONTH, YearMonthRange.MAX_YEARMONTH));
    }

    @Test
    public void test_ofClosed_MAXM1_MAX() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.ofClosed(MAXM1, YearMonthRange.MAX_YEARMONTH));
    }

    @Test
    public void test_ofClosed_MAXM1_MAXM1() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.ofClosed(MAXM1, MAXM1));
    }

    @Test
    public void test_ofClosed_badOrder() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.ofClosed(MONTH_2012_08, MONTH_2012_07));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ofEmpty() {
        YearMonthRange test = YearMonthRange.ofEmpty(MONTH_2012_08);
        assertEquals(MONTH_2012_08, test.getStart());
        assertEquals(MONTH_2012_07, test.getEndInclusive());
        assertEquals(MONTH_2012_08, test.getEnd());
        assertEquals(true, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals("2012-08/2012-08", test.toString());
    }

    @Test
    public void test_ofEmpty_MIN() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.ofEmpty(YearMonthRange.MIN_YEARMONTH));
    }

    @Test
    public void test_ofEmpty_MINP1() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.ofEmpty(MINP1));
    }

    @Test
    public void test_ofEmpty_MAX() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.ofEmpty(YearMonthRange.MAX_YEARMONTH));
    }

    @Test
    public void test_ofEmpty_MAXM1() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.ofEmpty(MAXM1));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ofUnbounded() {
        YearMonthRange test = YearMonthRange.ofUnbounded();
        assertEquals(YearMonthRange.MIN_YEARMONTH, test.getStart());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEndInclusive());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(true, test.isUnboundedEnd());
        assertEquals("-999999999-01/999999999-12", test.toString());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ofUnboundedStart() {
        YearMonthRange test = YearMonthRange.ofUnboundedStart(MONTH_2012_08);
        assertEquals(YearMonthRange.MIN_YEARMONTH, test.getStart());
        assertEquals(MONTH_2012_07, test.getEndInclusive());
        assertEquals(MONTH_2012_08, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(true, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals("-999999999-01/2012-08", test.toString());
    }

    @Test
    public void test_ofUnboundedStart_MIN() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.ofUnboundedStart(YearMonthRange.MIN_YEARMONTH));
    }

    @Test
    public void test_ofUnboundedStart_MINP1() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.ofUnboundedStart(MINP1));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ofUnboundedEnd() {
        YearMonthRange test = YearMonthRange.ofUnboundedEnd(MONTH_2012_08);
        assertEquals(MONTH_2012_08, test.getStart());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEndInclusive());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEnd());
        assertEquals(false, test.isEmpty());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(true, test.isUnboundedEnd());
        assertEquals("2012-08/999999999-12", test.toString());
    }

    @Test
    public void test_ofUnboundedEnd_MAX() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.ofUnboundedEnd(YearMonthRange.MAX_YEARMONTH));
    }

    @Test
    public void test_ofUnboundedEnd_MAXM1() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.ofUnboundedEnd(MAXM1));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_of_period() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_07, Period.ofMonths(2));
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(MONTH_2012_08, test.getEndInclusive());
        assertEquals(MONTH_2012_09, test.getEnd());
        assertEquals(false, test.isUnboundedStart());
        assertEquals(false, test.isUnboundedEnd());
        assertEquals("2012-07/2012-09", test.toString());
    }

    @Test
    public void test_of_period_negative() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.of(MONTH_2012_07, Period.ofDays(-1)));
    }

    @Test
    public void test_of_period_atMIN() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.of(YearMonthRange.MIN_YEARMONTH, Period.ofDays(0)));
    }

    @Test
    public void test_of_period_atMAX() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.of(YearMonthRange.MAX_YEARMONTH, Period.ofDays(0)));
    }

    @Test
    public void test_of_period_atMAXM1_0D() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.of(MAXM1, Period.ofDays(0)));
    }

    @Test
    public void test_of_period_atMAXM1_1D() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.of(MAXM1, Period.ofDays(1)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_parse_CharSequence() {
        YearMonthRange test = YearMonthRange.parse(MONTH_2012_07 + "/" + MONTH_2012_08);
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(MONTH_2012_08, test.getEnd());
    }

    @Test
    public void test_parse_CharSequence_PeriodYearMonth() {
        YearMonthRange test = YearMonthRange.parse("P2M/" + MONTH_2012_09);
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(MONTH_2012_09, test.getEnd());
    }

    @Test
    public void test_parse_CharSequence_PeriodYearMonth_case() {
        YearMonthRange test = YearMonthRange.parse("p2m/" + MONTH_2012_09);
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(MONTH_2012_09, test.getEnd());
    }

    @Test
    public void test_parse_CharSequence_YearMonthPeriod() {
        YearMonthRange test = YearMonthRange.parse(MONTH_2012_07 + "/P2M");
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(MONTH_2012_09, test.getEnd());
    }

    @Test
    public void test_parse_CharSequence_YearMonthPeriod_case() {
        YearMonthRange test = YearMonthRange.parse(MONTH_2012_07 + "/p2m");
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(MONTH_2012_09, test.getEnd());
    }

    @Test
    public void test_parse_CharSequence_empty() {
        YearMonthRange test = YearMonthRange.parse(MONTH_2012_07 + "/" + MONTH_2012_07);
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(MONTH_2012_07, test.getEnd());
    }

    @Test
    public void test_parse_CharSequence_badOrder() {
        assertThrows(DateTimeException.class, () -> YearMonthRange.parse(MONTH_2012_08 + "/" + MONTH_2012_07));
    }

    @Test
    public void test_parse_CharSequence_badFormat() {
        assertThrows(DateTimeParseException.class, () -> YearMonthRange.parse(MONTH_2012_07 + "-" + MONTH_2012_07));
    }

    @Test
    public void test_parse_CharSequence_null() {
        assertThrows(NullPointerException.class, () -> YearMonthRange.parse(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(YearMonthRange.class));
    }

    @Test
    public void test_serialization() throws Exception {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_07, MONTH_2012_08);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(test);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            assertEquals(test, ois.readObject());
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_withStart() {
        YearMonthRange base = YearMonthRange.of(MONTH_2012_08, MONTH_2012_09);
        YearMonthRange test = base.withStart(MONTH_2012_07);
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(MONTH_2012_08, test.getEndInclusive());
        assertEquals(MONTH_2012_09, test.getEnd());
    }

    @Test
    public void test_withStart_adjuster() {
        YearMonthRange base = YearMonthRange.of(MONTH_2012_07, MONTH_2012_08);
        YearMonthRange test = base.withStart(date -> date.minus(1, ChronoUnit.MONTHS));
        assertEquals(MONTH_2012_07.minusMonths(1), test.getStart());
        assertEquals(MONTH_2012_07, test.getEndInclusive());
        assertEquals(MONTH_2012_08, test.getEnd());
    }

    @Test
    public void test_withStart_min() {
        YearMonthRange base = YearMonthRange.of(MONTH_2012_07, MONTH_2012_09);
        YearMonthRange test = base.withStart(YearMonthRange.MIN_YEARMONTH);
        assertEquals(YearMonthRange.MIN_YEARMONTH, test.getStart());
        assertEquals(MONTH_2012_08, test.getEndInclusive());
        assertEquals(MONTH_2012_09, test.getEnd());
    }

    @Test
    public void test_withStart_empty() {
        YearMonthRange base = YearMonthRange.of(MONTH_2012_07, MONTH_2012_08);
        YearMonthRange test = base.withStart(MONTH_2012_08);
        assertEquals(MONTH_2012_08, test.getStart());
        assertEquals(MONTH_2012_07, test.getEndInclusive());
        assertEquals(MONTH_2012_08, test.getEnd());
    }

    @Test
    public void test_withStart_invalid() {
        YearMonthRange base = YearMonthRange.of(MONTH_2012_07, MONTH_2012_08);
        assertThrows(DateTimeException.class, () -> base.withStart(MONTH_2012_09));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_withEnd() {
        YearMonthRange base = YearMonthRange.of(MONTH_2012_07, MONTH_2012_08);
        YearMonthRange test = base.withEnd(MONTH_2012_08);
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(MONTH_2012_07, test.getEndInclusive());
        assertEquals(MONTH_2012_08, test.getEnd());
    }

    @Test
    public void test_withEnd_adjuster() {
        YearMonthRange base = YearMonthRange.of(MONTH_2012_07, MONTH_2012_08);
        YearMonthRange test = base.withEnd(date -> date.plus(1, ChronoUnit.MONTHS));
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(MONTH_2012_07.plusMonths(1), test.getEndInclusive());
        assertEquals(MONTH_2012_08.plusMonths(1), test.getEnd());
    }

    @Test
    public void test_withEnd_max() {
        YearMonthRange base = YearMonthRange.of(MONTH_2012_07, MONTH_2012_08);
        YearMonthRange test = base.withEnd(YearMonthRange.MAX_YEARMONTH);
        assertEquals(MONTH_2012_07, test.getStart());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEndInclusive());
        assertEquals(YearMonthRange.MAX_YEARMONTH, test.getEnd());
    }

    @Test
    public void test_withEnd_empty() {
        YearMonthRange base = YearMonthRange.of(MONTH_2012_08, MONTH_2012_08);
        YearMonthRange test = base.withEnd(MONTH_2012_08);
        assertEquals(MONTH_2012_08, test.getStart());
        assertEquals(MONTH_2012_07, test.getEndInclusive());
        assertEquals(MONTH_2012_08, test.getEnd());
    }

    @Test
    public void test_withEnd_invalid() {
        YearMonthRange base = YearMonthRange.of(MONTH_2012_08, MONTH_2012_09);
        assertThrows(DateTimeException.class, () -> base.withEnd(MONTH_2012_07));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_contains() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_07, MONTH_2012_09);
        assertEquals(false, test.contains(YearMonthRange.MIN_YEARMONTH));
        assertEquals(true, test.contains(MONTH_2012_07));
        assertEquals(true, test.contains(MONTH_2012_08));
        assertEquals(false, test.contains(MONTH_2012_09));
        assertEquals(false, test.contains(YearMonthRange.MAX_YEARMONTH));
    }

    @Test
    public void test_contains_baseEmpty() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_07, MONTH_2012_07);
        assertEquals(false, test.contains(YearMonthRange.MIN_YEARMONTH));
        assertEquals(false, test.contains(MONTH_2012_07));
        assertEquals(false, test.contains(MONTH_2012_07));
        assertEquals(false, test.contains(MONTH_2012_07));
        assertEquals(false, test.contains(YearMonthRange.MAX_YEARMONTH));
    }

    @Test
    public void test_contains_max() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, YearMonthRange.MAX_YEARMONTH);
        assertEquals(false, test.contains(YearMonthRange.MIN_YEARMONTH));
        assertEquals(false, test.contains(MONTH_2012_07));
        assertEquals(true, test.contains(MONTH_2012_08));
        assertEquals(true, test.contains(MONTH_2012_09));
        assertEquals(true, test.contains(MONTH_2012_10));
        assertEquals(true, test.contains(YearMonthRange.MAX_YEARMONTH));
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_queries() {
        return new Object[][] {
            // before start
            { MONTH_2012_01, MONTH_2012_07, false, false, false, false },
            { MONTH_2012_01, MONTH_2012_08, false, true, true, false },
            // before end
            { MONTH_2012_07, MONTH_2012_10, false, false, true, true },
            { MONTH_2012_08, MONTH_2012_10, true, false, true, true },
            { MONTH_2012_09, MONTH_2012_10, true, false, true, true },
            // same end
            { MONTH_2012_07, MONTH_2012_12, false, false, true, true },
            { MONTH_2012_08, MONTH_2012_12, true, false, true, true },
            { MONTH_2012_09, MONTH_2012_12, true, false, true, true },
            { MONTH_2012_10, MONTH_2012_12, true, false, true , true},
            // past end
            { MONTH_2012_07, MONTH_2013_01, false, false, true, true },
            { MONTH_2012_08, MONTH_2013_01, false, false, true, true },
            { MONTH_2012_09, MONTH_2013_01, false, false, true, true },
            { MONTH_2012_10, MONTH_2013_01, false, false, true, true },
            // start past end
            { MONTH_2012_12, MONTH_2013_01, false, true, true, false },
            { MONTH_2012_12, MONTH_2013_12, false, true, true, false },
            { MONTH_2013_01, MONTH_2013_12, false, false, false, false },
            // empty
            { MONTH_2012_07, MONTH_2012_07, false, false, false, false },
            { MONTH_2012_08, MONTH_2012_08, true, true, true, false },
            { MONTH_2012_09, MONTH_2012_09, true, false, true, true },
            { MONTH_2012_10, MONTH_2012_10, true, false, true, true },
            { MONTH_2012_11, MONTH_2012_11, true, false, true, true },
            { MONTH_2013_12, MONTH_2013_12, false, false, false, false },
            // min
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_07, false, false, false, false },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_08, false, true, true, false },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_09, false, false, true, true },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_10, false, false, true, true },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_11, false, false, true, true },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2013_01, false, false, true, true },
            { YearMonthRange.MIN_YEARMONTH, YearMonthRange.MAX_YEARMONTH, false, false, true, true },
            // max
            { MONTH_2012_07, YearMonthRange.MAX_YEARMONTH, false, false, true, true },
            { MONTH_2012_08, YearMonthRange.MAX_YEARMONTH, false, false, true, true },
            { MONTH_2012_09, YearMonthRange.MAX_YEARMONTH, false, false, true, true },
            { MONTH_2012_10, YearMonthRange.MAX_YEARMONTH, false, false, true, true },
            { MONTH_2012_11, YearMonthRange.MAX_YEARMONTH, false, false, true, true },
            { MONTH_2013_01, YearMonthRange.MAX_YEARMONTH, false, false, false, false },
        };
    }

    @ParameterizedTest
    @UseDataProvider("data_queries")
    public void test_encloses(
            YearMonth start, YearMonth end, boolean isEnclosedBy, boolean abuts, boolean isConnected, boolean overlaps) {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, MONTH_2012_12);
        assertEquals(isEnclosedBy, test.encloses(YearMonthRange.of(start, end)));
    }

    @ParameterizedTest
    @UseDataProvider("data_queries")
    public void test_abuts(
            YearMonth start, YearMonth end, boolean isEnclosedBy, boolean abuts, boolean isConnected, boolean overlaps) {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, MONTH_2012_12);
        assertEquals(abuts, test.abuts(YearMonthRange.of(start, end)));
    }

    @ParameterizedTest
    @UseDataProvider("data_queries")
    public void test_isConnected(
            YearMonth start, YearMonth end, boolean isEnclosedBy, boolean abuts, boolean isConnected, boolean overlaps) {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, MONTH_2012_12);
        assertEquals(isConnected, test.isConnected(YearMonthRange.of(start, end)));
    }

    @ParameterizedTest
    @UseDataProvider("data_queries")
    public void test_overlaps(
            YearMonth start, YearMonth end, boolean isEnclosedBy, boolean abuts, boolean isConnected, boolean overlaps) {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, MONTH_2012_12);
        assertEquals(overlaps, test.overlaps(YearMonthRange.of(start, end)));
    }

    @ParameterizedTest
    @UseDataProvider("data_queries")
    public void test_crossCheck(
            YearMonth start, YearMonth end, boolean isEnclosedBy, boolean abuts, boolean isConnected, boolean overlaps) {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, MONTH_2012_12);
        YearMonthRange input = YearMonthRange.of(start, end);
        assertEquals(test.overlaps(input) || test.abuts(input), test.isConnected(input));
        assertEquals(test.isConnected(input) && !test.abuts(input), test.overlaps(input));
    }

    @Test
    public void test_encloses_max() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_09, YearMonthRange.MAX_YEARMONTH);
        assertEquals(true, test.encloses(YearMonthRange.of(MONTH_2012_09, MONTH_2012_09)));
        assertEquals(true, test.encloses(YearMonthRange.of(MONTH_2012_09, MONTH_2012_10)));
        assertEquals(true, test.encloses(YearMonthRange.of(MONTH_2012_09, YearMonthRange.MAX_YEARMONTH)));
        assertEquals(false, test.encloses(YearMonthRange.of(MONTH_2012_07, MONTH_2012_08)));
        assertEquals(false, test.encloses(YearMonthRange.of(MONTH_2012_08, MONTH_2012_10)));
        assertEquals(false, test.encloses(YearMonthRange.of(MONTH_2012_08, YearMonthRange.MAX_YEARMONTH)));
    }

    @Test
    public void test_encloses_baseEmpty() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, MONTH_2012_08);
        assertEquals(false, test.encloses(YearMonthRange.of(MONTH_2012_07, MONTH_2012_07)));
        assertEquals(true, test.encloses(YearMonthRange.of(MONTH_2012_08, MONTH_2012_08)));
        assertEquals(false, test.encloses(YearMonthRange.of(MONTH_2012_09, MONTH_2012_09)));
        assertEquals(false, test.encloses(YearMonthRange.of(MONTH_2012_07, MONTH_2012_07)));
        assertEquals(false, test.encloses(YearMonthRange.of(MONTH_2012_07, MONTH_2012_08)));
        assertEquals(false, test.encloses(YearMonthRange.of(MONTH_2012_08, MONTH_2012_09)));
        assertEquals(false, test.encloses(YearMonthRange.of(MONTH_2012_07, YearMonthRange.MAX_YEARMONTH)));
        assertEquals(false, test.encloses(YearMonthRange.of(MONTH_2012_07, YearMonthRange.MAX_YEARMONTH)));
    }

    @Test
    public void test_abuts_baseEmpty() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, MONTH_2012_08);
        assertEquals(false, test.abuts(YearMonthRange.of(MONTH_2012_08, MONTH_2012_08)));
        assertEquals(false, test.abuts(YearMonthRange.of(MONTH_2012_07, MONTH_2012_07)));
        assertEquals(true, test.abuts(YearMonthRange.of(MONTH_2012_08, MONTH_2012_09)));
        assertEquals(true, test.abuts(YearMonthRange.of(MONTH_2012_07, MONTH_2012_08)));
        assertEquals(true, test.abuts(YearMonthRange.of(MONTH_2012_08, MONTH_2012_09)));
    }

    @Test
    public void test_isConnected_baseEmpty() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, MONTH_2012_08);
        assertEquals(false, test.isConnected(YearMonthRange.of(MONTH_2012_07, MONTH_2012_07)));
        assertEquals(true, test.isConnected(YearMonthRange.of(MONTH_2012_08, MONTH_2012_08)));
        assertEquals(false, test.isConnected(YearMonthRange.of(MONTH_2012_09, MONTH_2012_09)));
    }

    @Test
    public void test_overlaps_baseEmpty() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, MONTH_2012_08);
        assertEquals(false, test.overlaps(YearMonthRange.of(MONTH_2012_07, MONTH_2012_07)));
        assertEquals(true, test.overlaps(YearMonthRange.of(MONTH_2012_08, MONTH_2012_08)));
        assertEquals(false, test.overlaps(YearMonthRange.of(MONTH_2012_09, MONTH_2012_09)));
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_intersection() {
        return new Object[][] {
            // adjacent
            { MONTH_2012_07, MONTH_2012_07, MONTH_2012_07, MONTH_2012_08, MONTH_2012_07, MONTH_2012_07 },
            // adjacent empty
            { MONTH_2012_07, MONTH_2012_08, MONTH_2012_08, MONTH_2012_08, MONTH_2012_08, MONTH_2012_08 },
            // overlap
            { MONTH_2012_07, MONTH_2012_07, MONTH_2012_07, MONTH_2012_08, MONTH_2012_07, MONTH_2012_07 },
            // encloses
            { MONTH_2012_07, MONTH_2012_08, MONTH_2012_07, MONTH_2012_07, MONTH_2012_07, MONTH_2012_07 },
            // encloses empty
            { MONTH_2012_07, MONTH_2012_08, MONTH_2012_07, MONTH_2012_07, MONTH_2012_07, MONTH_2012_07 },
        };
    }

    @ParameterizedTest
    @UseDataProvider("data_intersection")
    public void test_intersection(
            YearMonth start1, YearMonth end1, YearMonth start2, YearMonth end2, YearMonth expStart, YearMonth expEnd) {

        YearMonthRange test1 = YearMonthRange.of(start1, end1);
        YearMonthRange test2 = YearMonthRange.of(start2, end2);
        YearMonthRange expected = YearMonthRange.of(expStart, expEnd);
        assertTrue(test1.isConnected(test2));
        assertEquals(expected, test1.intersection(test2));
    }

    @ParameterizedTest
    @UseDataProvider("data_intersection")
    public void test_intersection_reverse(
            YearMonth start1, YearMonth end1, YearMonth start2, YearMonth end2, YearMonth expStart, YearMonth expEnd) {

        YearMonthRange test1 = YearMonthRange.of(start1, end1);
        YearMonthRange test2 = YearMonthRange.of(start2, end2);
        YearMonthRange expected = YearMonthRange.of(expStart, expEnd);
        assertTrue(test2.isConnected(test1));
        assertEquals(expected, test2.intersection(test1));
    }

    @Test
    public void test_intersectionBad() {
        YearMonthRange test1 = YearMonthRange.of(MONTH_2012_07, MONTH_2012_08);
        YearMonthRange test2 = YearMonthRange.of(MONTH_2012_09, MONTH_2012_10);
        assertEquals(false, test1.isConnected(test2));
        assertThrows(DateTimeException.class, () -> test1.intersection(test2));
    }

    @Test
    public void test_intersection_same() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_07, MONTH_2012_08);
        assertEquals(test, test.intersection(test));
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_union() {
        return new Object[][] {
            // adjacent
            { MONTH_2012_07, MONTH_2012_07, MONTH_2012_07, MONTH_2012_08, MONTH_2012_07, MONTH_2012_08 },
            // adjacent empty
            { MONTH_2012_07, MONTH_2012_08, MONTH_2012_08, MONTH_2012_08, MONTH_2012_07, MONTH_2012_08 },
            // overlap
            { MONTH_2012_07, MONTH_2012_07, MONTH_2012_07, MONTH_2012_08, MONTH_2012_07, MONTH_2012_08 },
            // encloses
            { MONTH_2012_07, MONTH_2012_08, MONTH_2012_07, MONTH_2012_07, MONTH_2012_07, MONTH_2012_08 },
            // encloses empty
            { MONTH_2012_07, MONTH_2012_08, MONTH_2012_07, MONTH_2012_07, MONTH_2012_07, MONTH_2012_08 },
        };
    }

    @ParameterizedTest
    @UseDataProvider("data_union")
    public void test_unionAndSpan(
            YearMonth start1, YearMonth end1, YearMonth start2, YearMonth end2, YearMonth expStart, YearMonth expEnd) {

        YearMonthRange test1 = YearMonthRange.of(start1, end1);
        YearMonthRange test2 = YearMonthRange.of(start2, end2);
        YearMonthRange expected = YearMonthRange.of(expStart, expEnd);
        assertTrue(test1.isConnected(test2));
        assertEquals(expected, test1.union(test2));
        assertEquals(expected, test1.span(test2));
    }

    @ParameterizedTest
    @UseDataProvider("data_union")
    public void test_unionAndSpan_reverse(
            YearMonth start1, YearMonth end1, YearMonth start2, YearMonth end2, YearMonth expStart, YearMonth expEnd) {

        YearMonthRange test1 = YearMonthRange.of(start1, end1);
        YearMonthRange test2 = YearMonthRange.of(start2, end2);
        YearMonthRange expected = YearMonthRange.of(expStart, expEnd);
        assertTrue(test2.isConnected(test1));
        assertEquals(expected, test2.union(test1));
        assertEquals(expected, test2.span(test1));
    }

    @ParameterizedTest
    @UseDataProvider("data_union")
    public void test_span_enclosesInputs(
            YearMonth start1, YearMonth end1, YearMonth start2, YearMonth end2, YearMonth expStart, YearMonth expEnd) {

        YearMonthRange test1 = YearMonthRange.of(start1, end1);
        YearMonthRange test2 = YearMonthRange.of(start2, end2);
        YearMonthRange expected = YearMonthRange.of(expStart, expEnd);
        assertEquals(true, expected.encloses(test1));
        assertEquals(true, expected.encloses(test2));
    }

    @Test
    public void test_union_disconnected() {
        YearMonthRange test1 = YearMonthRange.of(MONTH_2012_07, MONTH_2012_08);
        YearMonthRange test2 = YearMonthRange.of(MONTH_2012_09, MONTH_2012_10);
        assertFalse(test1.isConnected(test2));
        assertThrows(DateTimeException.class, () -> test1.union(test2));
    }

    @Test
    public void test_span_disconnected() {
        YearMonthRange test1 = YearMonthRange.of(MONTH_2012_07, MONTH_2012_08);
        YearMonthRange test2 = YearMonthRange.of(MONTH_2012_09, MONTH_2012_10);
        assertFalse(test1.isConnected(test2));
        assertEquals(YearMonthRange.of(MONTH_2012_07, MONTH_2012_10), test1.span(test2));
    }

    @Test
    public void test_unionAndSpan_same() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_07, MONTH_2012_08);
        assertEquals(test, test.union(test));
        assertEquals(test, test.span(test));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_stream() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_07, MONTH_2012_10);
        List<YearMonth> result = test.stream().collect(Collectors.toList());
        assertEquals(3, result.size());
        assertEquals(MONTH_2012_07, result.get(0));
        assertEquals(MONTH_2012_08, result.get(1));
        assertEquals(MONTH_2012_09, result.get(2));
    }

    @Test
    public void test_stream_MIN_MINP3() {
        YearMonthRange test = YearMonthRange.of(YearMonthRange.MIN_YEARMONTH, MINP3);
        List<YearMonth> result = test.stream().collect(Collectors.toList());
        assertEquals(3, result.size());
        assertEquals(YearMonthRange.MIN_YEARMONTH, result.get(0));
        assertEquals(MINP1, result.get(1));
        assertEquals(MINP2, result.get(2));
    }

    @Test
    public void test_stream_MAXM2_MAX() {
        YearMonthRange test = YearMonthRange.of(MAXM2, YearMonthRange.MAX_YEARMONTH);
        List<YearMonth> result = test.stream().collect(Collectors.toList());
        assertEquals(3, result.size());
        assertEquals(MAXM2, result.get(0));
        assertEquals(MAXM1, result.get(1));
        assertEquals(YearMonthRange.MAX_YEARMONTH, result.get(2));
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_isBefore() {
        return new Object[][] {
            // before start
            { MONTH_2012_01, MONTH_2012_07, false },
            // before end
            { MONTH_2012_07, MONTH_2012_11, false },
            { MONTH_2012_08, MONTH_2012_11, false },
            { MONTH_2012_09, MONTH_2012_11, false },
            // same end
            { MONTH_2012_07, MONTH_2012_12, false },
            { MONTH_2012_08, MONTH_2012_12, false },
            { MONTH_2012_09, MONTH_2012_12, false },
            { MONTH_2012_10, MONTH_2012_12, false },
            // past end
            { MONTH_2012_07, MONTH_2013_01, false },
            { MONTH_2012_08, MONTH_2013_01, false },
            { MONTH_2012_09, MONTH_2013_01, false },
            { MONTH_2012_10, MONTH_2013_01, false },
            // start past end
            { MONTH_2012_12, MONTH_2013_01, true },
            { MONTH_2012_12, MONTH_2013_12, true },
            // empty
            { MONTH_2012_10, MONTH_2012_10, false },
            { MONTH_2012_11, MONTH_2012_11, true },
            // min
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_07, false },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_08, false },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_09, false },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_10, false },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_11, false },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2013_01, false },
            { YearMonthRange.MIN_YEARMONTH, YearMonthRange.MAX_YEARMONTH, false },
            // max
            { MONTH_2012_07, YearMonthRange.MAX_YEARMONTH, false },
            { MONTH_2012_08, YearMonthRange.MAX_YEARMONTH, false },
            { MONTH_2012_09, YearMonthRange.MAX_YEARMONTH, false },
            { MONTH_2012_10, YearMonthRange.MAX_YEARMONTH, false },
            { MONTH_2012_11, YearMonthRange.MAX_YEARMONTH, true },
            { MONTH_2013_01, YearMonthRange.MAX_YEARMONTH, true },
        };
    }

    @ParameterizedTest
    @UseDataProvider("data_isBefore")
    public void test_isBefore_range(YearMonth start, YearMonth end, boolean before) {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, MONTH_2012_11);
        assertEquals(before, test.isBefore(YearMonthRange.of(start, end)));
    }

    @ParameterizedTest
    @UseDataProvider("data_isBefore")
    public void test_isBefore_date(YearMonth start, YearMonth end, boolean before) {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, MONTH_2012_11);
        assertEquals(before, test.isBefore(start));
    }

    @Test
    public void test_isBefore_range_empty() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_07, MONTH_2012_07);
        assertEquals(false, test.isBefore(YearMonthRange.of(MONTH_2012_07, MONTH_2012_07)));
        assertEquals(false, test.isBefore(YearMonthRange.of(MONTH_2012_07, MONTH_2012_07)));
        assertEquals(false, test.isBefore(YearMonthRange.of(MONTH_2012_07, MONTH_2012_07)));
        assertEquals(true, test.isBefore(YearMonthRange.of(MONTH_2012_07, MONTH_2012_08)));
        assertEquals(true, test.isBefore(YearMonthRange.of(MONTH_2012_08, MONTH_2012_08)));
        assertEquals(true, test.isBefore(YearMonthRange.of(MONTH_2012_08, MONTH_2012_08)));
    }

    @Test
    public void test_isBefore_date_empty() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_07, MONTH_2012_07);
        assertEquals(false, test.isBefore(MONTH_2012_07));
        assertEquals(false, test.isBefore(MONTH_2012_07));
        assertEquals(true, test.isBefore(MONTH_2012_08));
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_isAfter() {
        return new Object[][] {
            // before start
            { MONTH_2012_07, MONTH_2012_08, true },
            // to start
            { MONTH_2012_07, MONTH_2012_09, true },
            // before end
            { MONTH_2012_07, MONTH_2012_10, false },
            { MONTH_2012_08, MONTH_2012_10, false },
            { MONTH_2012_09, MONTH_2012_10, false },
            // same end
            { MONTH_2012_07, MONTH_2012_11, false },
            { MONTH_2012_08, MONTH_2012_11, false },
            { MONTH_2012_09, MONTH_2012_11, false },
            { MONTH_2012_10, MONTH_2012_11, false },
            // past end
            { MONTH_2012_07, MONTH_2012_12, false },
            { MONTH_2012_08, MONTH_2012_12, false },
            { MONTH_2012_09, MONTH_2012_12, false },
            { MONTH_2012_10, MONTH_2012_12, false },
            // start past end
            { MONTH_2012_12, MONTH_2013_01, false },
            { MONTH_2012_12, MONTH_2013_01, false },
            // empty
            { MONTH_2012_09, MONTH_2012_09, true },
            { MONTH_2012_10, MONTH_2012_10, false },
            // min
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_07, true },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_08, true },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_09, true },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_10, false },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_11, false },
            { YearMonthRange.MIN_YEARMONTH, MONTH_2012_12, false },
            { YearMonthRange.MIN_YEARMONTH, YearMonthRange.MAX_YEARMONTH, false },
            // max
            { MONTH_2012_07, YearMonthRange.MAX_YEARMONTH, false },
            { MONTH_2012_08, YearMonthRange.MAX_YEARMONTH, false },
            { MONTH_2012_09, YearMonthRange.MAX_YEARMONTH, false },
            { MONTH_2012_10, YearMonthRange.MAX_YEARMONTH, false },
            { MONTH_2012_11, YearMonthRange.MAX_YEARMONTH, false },
            { MONTH_2012_12, YearMonthRange.MAX_YEARMONTH, false },
        };
    }

    @ParameterizedTest
    @UseDataProvider("data_isAfter")
    public void test_isAfter_range(YearMonth start, YearMonth end, boolean before) {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_09, MONTH_2012_11);
        assertEquals(before, test.isAfter(YearMonthRange.of(start, end)));
    }

    @ParameterizedTest
    @UseDataProvider("data_isAfter")
    public void test_isAfter_date(YearMonth start, YearMonth end, boolean before) {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_09, MONTH_2012_11);
        assertEquals(before, test.isAfter(end.minusMonths(1)));
    }

    @Test
    public void test_isAfter_range_empty() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_09, MONTH_2012_09);
        assertEquals(true, test.isAfter(YearMonthRange.of(MONTH_2012_07, MONTH_2012_08)));
        assertEquals(true, test.isAfter(YearMonthRange.of(MONTH_2012_07, MONTH_2012_09)));
        assertEquals(true, test.isAfter(YearMonthRange.of(MONTH_2012_08, MONTH_2012_08)));
        assertEquals(false, test.isAfter(YearMonthRange.of(MONTH_2012_09, MONTH_2012_09)));
        assertEquals(false, test.isAfter(YearMonthRange.of(MONTH_2012_09, MONTH_2012_10)));
        assertEquals(false, test.isAfter(YearMonthRange.of(MONTH_2012_10, MONTH_2012_10)));
        assertEquals(false, test.isAfter(YearMonthRange.of(MONTH_2012_10, MONTH_2012_11)));
    }

    @Test
    public void test_isAfter_date_empty() {
        YearMonthRange test = YearMonthRange.of(MONTH_2012_08, MONTH_2012_08);
        assertEquals(true, test.isAfter(MONTH_2012_07));
        assertEquals(false, test.isAfter(MONTH_2012_08));
        assertEquals(false, test.isAfter(MONTH_2012_09));
    }

  //-----------------------------------------------------------------------
    @Test
    public void test_lengthInMonths() {
        assertEquals(2, YearMonthRange.of(MONTH_2012_07, MONTH_2012_09).lengthInMonths());
        assertEquals(1, YearMonthRange.of(MONTH_2012_07, MONTH_2012_08).lengthInMonths());
        assertEquals(0, YearMonthRange.of(MONTH_2012_07, MONTH_2012_07).lengthInMonths());
        assertEquals(Integer.MAX_VALUE, YearMonthRange.of(YearMonthRange.MIN_YEARMONTH, MONTH_2012_07).lengthInMonths());
        assertEquals(Integer.MAX_VALUE, YearMonthRange.of(MONTH_2012_07, YearMonthRange.MAX_YEARMONTH).lengthInMonths());
        assertEquals(Integer.MAX_VALUE, YearMonthRange.of(MINP1, MAXM1).lengthInMonths());
    }

    @Test
    public void test_toPeriod() {
        assertEquals(Period.ofMonths(2), YearMonthRange.of(MONTH_2012_07, MONTH_2012_09).toPeriod());
        assertEquals(Period.ofMonths(1), YearMonthRange.of(MONTH_2012_08, MONTH_2012_09).toPeriod());
        assertEquals(Period.ofMonths(0), YearMonthRange.of(MONTH_2012_09, MONTH_2012_09).toPeriod());
    }

    @Test
    public void test_toPeriod_unbounded_MIN() {
        assertThrows(ArithmeticException.class, () -> YearMonthRange.of(YearMonthRange.MIN_YEARMONTH, MONTH_2012_07).toPeriod());
    }

    @Test
    public void test_toPeriod_unbounded_MAX() {
        assertThrows(ArithmeticException.class, () -> YearMonthRange.of(MONTH_2012_07, YearMonthRange.MAX_YEARMONTH).toPeriod());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_equals() {
        YearMonthRange a = YearMonthRange.of(MONTH_2012_07, MONTH_2012_07);
        YearMonthRange a2 = YearMonthRange.of(MONTH_2012_07, MONTH_2012_07);
        YearMonthRange b = YearMonthRange.of(MONTH_2012_07, MONTH_2012_08);
        YearMonthRange c = YearMonthRange.of(MONTH_2012_07, MONTH_2012_09);
        assertEquals(true, a.equals(a));
        assertEquals(true, a.equals(a2));
        assertEquals(false, a.equals(b));
        assertEquals(false, a.equals(c));
        assertEquals(false, a.equals(null));
        assertEquals(false, a.equals((Object) ""));
        assertEquals(true, a.hashCode() == a2.hashCode());
    }

    //-----------------------------------------------------------------------
    @DataProvider
    public static List<List<Object>> data_crossCheckGuava() {
        List<List<Object>> list = new ArrayList<>();
        for (int i1 = 1; i1 < 5; i1++) {
            for (int j1 = i1; j1 < 5; j1++) {
                YearMonth date11 = YearMonth.of(2016, i1);
                YearMonth date12 = YearMonth.of(2016, j1);
                YearMonthRange extraRange1 = YearMonthRange.of(date11, date12);
                Range<YearMonth> guavaRange1 = Range.closedOpen(date11, date12);
                for (int i2 = 1; i2 < 5; i2++) {
                    for (int j2 = i2; j2 < 5; j2++) {
                        YearMonth date21 = YearMonth.of(2016, i2);
                        YearMonth date22 = YearMonth.of(2016, j2);
                        YearMonthRange extraRange2 = YearMonthRange.of(date21, date22);
                        Range<YearMonth> guavaRange2 = Range.closedOpen(date21, date22);
                        list.add(Arrays.asList(extraRange1, extraRange2, guavaRange1, guavaRange2));
                    }
                }
            }
        }
        return list;
    }

    @ParameterizedTest
    @UseDataProvider("data_crossCheckGuava")
    public void crossCheckGuava_encloses(
            YearMonthRange extraRange1,
            YearMonthRange extraRange2, 
            Range<YearMonth> guavaRange1, 
            Range<YearMonth> guavaRange2) {

        boolean extra = extraRange1.encloses(extraRange2);
        boolean guava = guavaRange1.encloses(guavaRange2);
        assertEquals(guava, extra);
    }

    @ParameterizedTest
    @UseDataProvider("data_crossCheckGuava")
    public void crossCheckGuava_isConnected(
            YearMonthRange extraRange1,
            YearMonthRange extraRange2, 
            Range<YearMonth> guavaRange1, 
            Range<YearMonth> guavaRange2) {

        boolean extra = extraRange1.isConnected(extraRange2);
        boolean guava = guavaRange1.isConnected(guavaRange2);
        assertEquals(guava, extra);
    }

    @ParameterizedTest
    @UseDataProvider("data_crossCheckGuava")
    public void crossCheckGuava_intersection(
            YearMonthRange extraRange1,
            YearMonthRange extraRange2, 
            Range<YearMonth> guavaRange1, 
            Range<YearMonth> guavaRange2) {

        YearMonthRange extra = null;
        try {
            extra = extraRange1.intersection(extraRange2);
        } catch (DateTimeException ex) {
            // continue
        }
        Range<YearMonth> guava = null;
        try {
            guava = guavaRange1.intersection(guavaRange2);
        } catch (IllegalArgumentException ex) {
            // continue
        }
        if (extra == null) {
            assertEquals(guava, extra);
        } else if (guava != null) {
            assertEquals(guava.lowerEndpoint(), extra.getStart());
            assertEquals(guava.upperEndpoint(), extra.getEnd());
        }
    }

    @ParameterizedTest
    @UseDataProvider("data_crossCheckGuava")
    public void crossCheckGuava_span(
            YearMonthRange extraRange1,
            YearMonthRange extraRange2, 
            Range<YearMonth> guavaRange1, 
            Range<YearMonth> guavaRange2) {

        YearMonthRange extra = extraRange1.span(extraRange2);
        Range<YearMonth> guava = guavaRange1.span(guavaRange2);
        assertEquals(guava.lowerEndpoint(), extra.getStart());
        assertEquals(guava.upperEndpoint(), extra.getEnd());
    }

}
