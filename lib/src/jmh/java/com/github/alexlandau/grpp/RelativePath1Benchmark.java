package com.github.alexlandau.grpp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class RelativePath1Benchmark {
    @Benchmark
    public void testRelativePath1(Blackhole bh) {
        RelativePath1 root1 = new RelativePath1(false, "some", "dir");
        bh.consume(new RelativePath1(false, root1, "another"));
        bh.consume(new RelativePath1(true, root1, "file"));
        bh.consume(new RelativePath1(true, root1, "longer", "path", "to", "a", "file"));
    }
}