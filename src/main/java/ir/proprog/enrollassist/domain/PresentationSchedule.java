package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // as required by JPA, don't use it in your code
@Getter
public class PresentationSchedule {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String dayOfWeek;
    private Date startTime = new Date();
    private Date endTime = new Date();
    @ManyToOne
    private Section section;

    public PresentationSchedule(String dayOfWeek, String time) throws ExceptionList {
        ExceptionList exceptionList = new ExceptionList();
        try {
            this.validateDayOfWeek(dayOfWeek);
        } catch (Exception exception) {
            exceptionList.addNewException(exception);
        }
        try {
            this.validateTime(time);
        } catch (Exception e) {
            exceptionList.addNewException(e);
        }
        if (exceptionList.hasException())
            throw exceptionList;

    }

    private void validateTime(String time) throws Exception {
        if (time.equals(""))
            throw new Exception("Time can not be empty");
        else {
            List<String> timeString = Arrays.asList(time.split("-"));
            if (timeString.size() != 2)
                throw new Exception(String.format("%s is not valid time.", time));
            else {
                SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm");
                try {
                    this.startTime = dataFormat.parse(timeString.get(0));
                    this.endTime = dataFormat.parse(timeString.get(1));
                }
                catch (Exception exception) {
                    throw new Exception(String.format("%s is not valid time.", time));
                }
                if (this.endTime.before(this.startTime))
                    throw new Exception(String.format("End time can not be before start time.(%s)", time));
            }
        }
    }


    private void validateDayOfWeek(String dayOfWeek) throws Exception {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        List<String> week = Arrays.asList(dateFormatSymbols.getWeekdays());
        if (!week.contains(dayOfWeek))
            throw new Exception(String.format("%s is not valid week day.", dayOfWeek));
        this.dayOfWeek = dayOfWeek;
    }

    public boolean hasConflict(PresentationSchedule otherPresentationSchedule) {
        if (this.dayOfWeek != otherPresentationSchedule.dayOfWeek)
            return false;
        else if (otherPresentationSchedule.startTime.after(this.endTime) || otherPresentationSchedule.startTime.equals(this.endTime))
            return false;
        else if (otherPresentationSchedule.endTime.before(this.startTime) || otherPresentationSchedule.endTime.equals(this.startTime))
            return false;
        return true;
    }
}
