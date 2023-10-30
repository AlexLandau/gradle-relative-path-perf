package com.github.alexlandau.grpp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class RelativePath1CBenchmark {
    @Benchmark
    public void testRelativePath1C(Blackhole bh) {
        RelativePath1C root1 = new RelativePath1C(false, "some", "dir");
        bh.consume(new RelativePath1C(false, root1, "another"));
        bh.consume(new RelativePath1C(true, root1, "file"));
        bh.consume(new RelativePath1C(true, root1, "longer", "path", "to", "a", "file"));
    }
}