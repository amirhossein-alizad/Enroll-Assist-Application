package ir.proprog.enrollassist.controller.section;

import lombok.Getter;

@Getter
public class ConflictView {
    private int numberOfConflict;

    public ConflictView() {
    }

    public ConflictView(int numberOfConflict) {
        this.numberOfConflict = numberOfConflict;
    }
}
