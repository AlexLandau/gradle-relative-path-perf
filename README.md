# gradle-relative-path-perf

```
Benchmark                                    Mode  Cnt         Score        Error  Units
RelativePath1BBenchmark.testRelativePath1B  thrpt   25   9757023.075 ± 239258.310  ops/s
RelativePath1Benchmark.testRelativePath1    thrpt   25   9699841.254 ± 237311.719  ops/s
RelativePath2Benchmark.testRelativePath2    thrpt   25   2876108.564 ± 110458.781  ops/s
RelativePath3Benchmark.testRelativePath3    thrpt   25  14549687.945 ± 315704.997  ops/s
```

I don't understand why 3 outperforms 1 -- either I've made a mistake in my benchmarking setup (entirely possible),
or maybe array iteration just outperforms `System.arraycopy` for short arrays.
