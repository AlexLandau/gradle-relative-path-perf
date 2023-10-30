package com.github.alexlandau.grpp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class RelativePath2BBenchmark {
    @Benchmark
    public void testRelativePath2B(Blackhole bh) {
        RelativePath2B root1 = new RelativePath2B(false, "some", "dir");
        bh.consume(new RelativePath2B(false, root1, "another"));
        bh.consume(new RelativePath2B(true, root1, "file"));
        bh.consume(new RelativePath2B(true, root1, "longer", "path", "to", "a", "file"));
    }
}