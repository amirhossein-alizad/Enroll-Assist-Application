package ir.proprog.enrollassist.domain;

public enum GraduateLevel {
    Undergraduate,
    Masters,
    PHD;

    public double getMinValidGrade() {
        if (this.equals(Undergraduate))
            return 10.0;
        else if (this.equals(Masters))
            return 12.0;
        else
            return 14.0;
    }

}
