package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class PresentationSchedule {
    private String dayOfWeek;
    private Date startTime = new Date();
    private Date endTime = new Date();

    public PresentationSchedule(String dayOfWeek, String start, String end) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        try {
            this.validateDayOfWeek(dayOfWeek);
            this.dayOfWeek = dayOfWeek;
        } catch (Exception exception) {
            exceptionList.addNewException(exception);
        }
        try {
            validateTime(start, end);
        }catch (Exception e) {
            exceptionList.addNewException(e);
        }
        if (exceptionList.hasException())
            throw exceptionList;
    }

    private void validateTime(String start, String end) throws Exception {
        try {
            SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm");
            this.startTime = dataFormat.parse(start);
            this.endTime = dataFormat.parse(end);
        }
        catch (Exception exception) {
            throw new Exception("Time format is not valid");
        }
        if (this.endTime.before(this.startTime))
            throw new Exception("End time can not be before start time.");
    }


    private void validateDayOfWeek(String dayOfWeek) throws Exception {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        List<String> week = Arrays.asList(dateFormatSymbols.getWeekdays());
        if (!week.contains(dayOfWeek))
            throw new Exception(String.format("%s is not valid week day.", dayOfWeek));
    }

    public boolean hasConflict(PresentationSchedule otherPresentationSchedule) {
        if (!this.dayOfWeek.equals(otherPresentationSchedule.dayOfWeek))
            return false;
        else if (otherPresentationSchedule.startTime.after(this.endTime) || otherPresentationSchedule.startTime.equals(this.endTime))
            return false;
        else if (otherPresentationSchedule.endTime.before(this.startTime) || otherPresentationSchedule.endTime.equals(this.startTime))
            return false;
        return true;
    }
}
