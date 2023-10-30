package com.github.alexlandau.grpp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class RelativePath3BBenchmark {
    @Benchmark
    public void testRelativePath3B(Blackhole bh) {
        RelativePath3B root1 = new RelativePath3B(false, "some", "dir");
        bh.consume(new RelativePath3B(false, root1, "another"));
        bh.consume(new RelativePath3B(true, root1, "file"));
        bh.consume(new RelativePath3B(true, root1, "longer", "path", "to", "a", "file"));
    }
}