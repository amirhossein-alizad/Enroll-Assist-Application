package ir.proprog.enrollassist.domain;

import antlr.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.Objects;
import java.util.Optional;

import static org.hibernate.query.criteria.internal.ValueHandlerFactory.isNumeric;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // as required by JPA, don't use it in your code
@Getter
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String sectionNo;
    @ManyToOne
    private ExamTime examTime;
    @ManyToOne
    private Course course;

    public Section(@NonNull Course course, String sectionNo) {
        this.validateSectionNo(sectionNo);
        this.sectionNo = sectionNo;
        this.course = course;
    }

    private void validateSectionNo(String sectionNo) {
        if (sectionNo.equals(""))
            throw new IllegalArgumentException("Section number cannot be empty");
        try {
            Integer.parseInt(sectionNo);
        } catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException("Section number must be number");
        }
    }

    public void setExamTime(ExamTime examTime) {
        this.examTime = examTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return course.equals(section.course) && sectionNo.equals(section.sectionNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, sectionNo);
    }

}
