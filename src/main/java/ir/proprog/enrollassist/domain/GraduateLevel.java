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

    public int getMinValidTermCredit() {
        if (this.equals(Undergraduate))
            return 12;
        else if (this.equals(Masters))
            return 8;
        else
            return 6;
    }

    public int getMaxValidCredits() {
        if (this.equals(Undergraduate))
            return 24;
        else
            return 12;
    }

}
