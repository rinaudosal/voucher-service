package com.docomodigital.delorean.voucher;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.TimeZone;

/**
 * 2020/01/24
 *
 * @author Sirius
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseUnitTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    protected Clock clock;

    protected void setupClockMock(Instant instant) {
        BDDMockito.when(clock.instant()).thenReturn(instant);
        BDDMockito.when(clock.getZone()).thenReturn(TimeZone.getTimeZone(ZoneOffset.UTC).toZoneId());
    }
}
