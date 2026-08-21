package vn.edu.ktpm.minishop.unit;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Dong ho gia lap cho phep "tua thoi gian" trong test.
 *
 * <p>Nho lop nay, test case kiem tra viec <b>tu dong mo khoa sau 15 phut</b> (FR-01) chay trong
 * vai mili-giay thay vi phai cho that 15 phut - day la ly do {@code LoginService} nhan
 * {@link Clock} qua constructor.</p>
 */
public class MutableClock extends Clock {

    private Instant instant;
    private final ZoneId zone;

    public MutableClock(Instant start) {
        this(start, ZoneId.systemDefault());
    }

    private MutableClock(Instant start, ZoneId zone) {
        this.instant = start;
        this.zone = zone;
    }

    public static MutableClock at(String isoInstant) {
        return new MutableClock(Instant.parse(isoInstant));
    }

    /** Tua thoi gian toi truoc. */
    public void advanceMinutes(long minutes) {
        instant = instant.plus(Duration.ofMinutes(minutes));
    }

    public void advanceSeconds(long seconds) {
        instant = instant.plus(Duration.ofSeconds(seconds));
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new MutableClock(instant, zone);
    }

    @Override
    public Instant instant() {
        return instant;
    }
}
