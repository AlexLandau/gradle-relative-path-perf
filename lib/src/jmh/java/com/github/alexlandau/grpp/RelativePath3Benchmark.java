package com.github.alexlandau.grpp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class RelativePath3Benchmark {
    @Benchmark
    public void testRelativePath3(Blackhole bh) {
        RelativePath3 root1 = new RelativePath3(false, "some", "dir");
        bh.consume(new RelativePath3(false, root1, "another"));
        bh.consume(new RelativePath3(true, root1, "file"));
        bh.consume(new RelativePath3(true, root1, "longer", "path", "to", "a", "file"));
    }
}