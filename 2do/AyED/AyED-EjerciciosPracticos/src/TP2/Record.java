package TP2;

public class Record {
        private int suma;
        private int diff;

        public Record (int suma, int diff) {
            this.suma = suma;
            this.diff = diff;
        }

    @Override
    public String toString() {
        return "{" + suma + " | " + diff + "}";
    }
}
