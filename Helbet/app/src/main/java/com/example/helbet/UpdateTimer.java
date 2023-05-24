package com.example.helbet;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class UpdateTimer extends DBModel {
    private long time;

    public UpdateTimer() {}

    public UpdateTimer(Date dateTime) {
        this(dateTime.getTime());
    }

    public UpdateTimer(long time) {
        this.time = time;
    }

    public boolean hasTimeGapPassed(int hours, int minutes) {
        Date now = new Date();
        long timeGap = now.getTime() - this.time;
        int timeGapHours = (int) (timeGap / (60 * 60 * 1000));
        int timeGapMinutes = (int) ((timeGap / (60 * 1000)) % 60);

        if (timeGapHours > hours) return true;
        else if (timeGapHours == hours) {
            if (timeGapMinutes > minutes) return true;
        }

        return false;
    }

    public boolean isYesterday() {
        // Obtenir la date actuelle
        Calendar now = Calendar.getInstance();

        // Obtenir la date du timestamp
        Calendar lastUpdate = Calendar.getInstance();
        lastUpdate.setTimeInMillis(time);

        // Comparaison des années, mois et jours
        return now.get(Calendar.YEAR) > lastUpdate.get(Calendar.YEAR)
                || now.get(Calendar.MONTH) > lastUpdate.get(Calendar.MONTH)
                || now.get(Calendar.DAY_OF_MONTH) - lastUpdate.get(Calendar.DAY_OF_MONTH) >= 1;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "UpdateTimer{" +
                "time=" + time +
                "formattedTime=" + new Date(time) +
                "} " + super.toString();
    }
}
