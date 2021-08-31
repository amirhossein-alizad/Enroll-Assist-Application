package ir.proprog.enrollassist.domain.section;

import ir.proprog.enrollassist.Exception.ExceptionList;
import lombok.Getter;
import lombok.Value;
import javax.persistence.*;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@Embeddable
@Value
public class PresentationSchedule {

    String dayOfWeek;
    String startTime;
    String endTime;
    public static final PresentationSchedule DEFAULT = new PresentationSchedule();

    public PresentationSchedule(){
        dayOfWeek = "Saturday";
        startTime = "09:00";
        endTime = "10:30";
    }

    public PresentationSchedule(String dayOfWeek, String start, String end) throws ExceptionList {
        this.dayOfWeek = dayOfWeek;
        this.startTime = start;
        this.endTime = end;
        ExceptionList exceptionList = new ExceptionList();
        try {
            this.validateDayOfWeek(dayOfWeek);
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
        SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm");
        try {
            dataFormat.parse(start);
            dataFormat.parse(end);
        }
        catch (Exception exception) {
            throw new Exception("Time format is not valid");
        }
        if (dataFormat.parse(end).before(dataFormat.parse(start)))
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

    @Override
    public int hashCode() { return Objects.hash(dayOfWeek, startTime, endTime); }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PresentationSchedule presentationSchedule = (PresentationSchedule) o;
        return dayOfWeek.equals(presentationSchedule.dayOfWeek) && startTime.equals(presentationSchedule.startTime)
                && endTime.equals(presentationSchedule.endTime);
    }
}
