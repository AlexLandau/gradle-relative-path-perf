package com.github.alexlandau.grpp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class RelativePath1BBenchmark {
    @Benchmark
    public void testRelativePath1B(Blackhole bh) {
        RelativePath1B root1 = new RelativePath1B(false, "some", "dir");
        bh.consume(new RelativePath1B(false, root1, "another"));
        bh.consume(new RelativePath1B(true, root1, "file"));
        bh.consume(new RelativePath1B(true, root1, "longer", "path", "to", "a", "file"));
    }
}