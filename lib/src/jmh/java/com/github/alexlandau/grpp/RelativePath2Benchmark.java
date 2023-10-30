package com.github.alexlandau.grpp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class RelativePath2Benchmark {
    @Benchmark
    public void testRelativePath2(Blackhole bh) {
        RelativePath2 root1 = new RelativePath2(false, "some", "dir");
        bh.consume(new RelativePath2(false, root1, "another"));
        bh.consume(new RelativePath2(true, root1, "file"));
        bh.consume(new RelativePath2(true, root1, "longer", "path", "to", "a", "file"));
    }
}