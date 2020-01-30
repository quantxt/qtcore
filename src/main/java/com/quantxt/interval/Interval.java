package com.quantxt.interval;

import lombok.Getter;

@Getter
public class Interval {

    final protected int start;
    final protected int end;

    public Interval(){
        this.start = -1;
        this.end = Integer.MAX_VALUE;
    }

    public Interval(int start, int end){
        this.start = start;
        this.end = end;
    }

    public int size() {
        return end - start + 1;
    }

    public boolean overlapsWith(final Interval other) {
        return this.start <= other.getEnd() &&
                this.end >= other.getStart();
    }

    public boolean overlapsWith(int point) {
        return this.start <= point && point <= this.end;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Interval)) {
            return false;
        }
        Interval other = (Interval) o;
        return this.start == other.getStart() &&
                this.end == other.getEnd();
    }

    @Override
    public int hashCode() {
        return this.start % 100 + this.end % 100;
    }

    public int compareTo(Object o) {
        if (!(o instanceof Interval)) {
            return -1;
        }
        Interval other = (Interval) o;
        int comparison = this.start - other.getStart();
        return comparison != 0 ? comparison : this.end - other.getEnd();
    }
}
