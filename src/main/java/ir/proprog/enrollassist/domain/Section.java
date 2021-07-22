package ir.proprog.enrollassist.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // as required by JPA, don't use it in your code
@Getter
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String sectionNo;
    @ManyToOne
    private Course course;

    public Section(@NonNull Course course, String sectionNo) {
        if (sectionNo == "")
            throw new IllegalArgumentException("Section number cannot be empty");
        this.sectionNo = sectionNo;
        this.course = course;
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
