package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class PresentationSchedule {
    private String dayOfWeek;
    private String startTime;
    private String endTime;

    public PresentationSchedule(String dayOfWeek, String start, String end) throws ExceptionList {
        this.dayOfWeek = dayOfWeek;
        this.startTime = start;
        this.endTime = end;
        ExceptionList exceptionList = new ExceptionList();
        try {
           this.validateFields();
        } catch (Exception exception) {
            exceptionList.addNewException(exception);
        }
        if (exceptionList.hasException())
            throw exceptionList;


    }

    private void validateTime(String start, String end) throws Exception {
        SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm");
        try {
            dataFormat.parse(start);
            dataFormat.parse(end);
        }
        catch (Exception exception) {
            throw new Exception("Time format is not valid");
        }
        if (dataFormat.parse(start).before(dataFormat.parse(end)))
            throw new Exception("End time can not be before start time.");
    }


    private void validateDayOfWeek(String dayOfWeek) throws Exception {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        List<String> week = Arrays.asList(dateFormatSymbols.getWeekdays());
        if (!week.contains(dayOfWeek))
            throw new Exception(String.format("%s is not valid week day.", dayOfWeek));
    }

    public boolean hasConflict(PresentationSchedule otherPresentationSchedule) {
        SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm");

        if (!this.dayOfWeek.equals(otherPresentationSchedule.dayOfWeek))
            return false;
        else {
            try {
                if (dataFormat.parse(otherPresentationSchedule.startTime).after(dataFormat.parse(this.endTime))
                        || dataFormat.parse(otherPresentationSchedule.startTime).equals(dataFormat.parse(this.endTime)))
                    return false;
                else return !dataFormat.parse(otherPresentationSchedule.endTime).before(dataFormat.parse(this.startTime))
                        && !dataFormat.parse(otherPresentationSchedule.endTime).equals(dataFormat.parse(this.startTime));
            } catch (ParseException ignored) {

            }
        }
        return false;
    }


    public void validateFields() throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        try {
            this.validateDayOfWeek(dayOfWeek);
        } catch (Exception exception) {
            exceptionList.addNewException(exception);
        }
        try {
            validateTime(startTime, endTime);
        }catch (Exception e) {
            exceptionList.addNewException(e);
        }
        if (exceptionList.hasException())
            throw exceptionList;
    }
    @Override
    public String toString() { return dayOfWeek + " " + startTime + " - " + endTime; }
}
