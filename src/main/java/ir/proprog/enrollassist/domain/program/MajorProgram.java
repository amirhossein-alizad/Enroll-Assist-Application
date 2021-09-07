package ir.proprog.enrollassist.domain.program;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.major.Major;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MajorProgram extends Program {
    public MajorProgram(Major major, String graduateLevel, int minimum, int maximum) throws ExceptionList {
        super(major, graduateLevel, minimum, maximum);
    }
}
