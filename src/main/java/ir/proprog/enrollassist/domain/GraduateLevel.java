package ir.proprog.enrollassist.domain;

public enum GraduateLevel {
    Undergraduate {
        @Override
        public double getMinValidGrade() {
            return 10.0;
        }
        @Override
        public int getMinValidTermCredit() {
            return 12;
        }
        @Override
        public int getMaxValidCredits() {
            return 24;
        }
    },
    Masters {
        @Override
        public double getMinValidGrade() {
            return 12.0;
        }
        @Override
        public int getMinValidTermCredit() {
            return 8;
        }
        @Override
        public int getMaxValidCredits() {
            return 12;
        }
    },
    PHD {
        @Override
        public double getMinValidGrade() {
            return 14.0;
        }
        @Override
        public int getMinValidTermCredit() {
            return 6;
        }
        @Override
        public int getMaxValidCredits() {
            return 12;
        }
    };

    public abstract double getMinValidGrade();
    public abstract int getMinValidTermCredit();
    public abstract int getMaxValidCredits();
}
