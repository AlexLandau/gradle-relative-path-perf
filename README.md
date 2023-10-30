# gradle-relative-path-perf

* RelativePath1: The implementation of RelativePath prior to adding canonicalization in https://github.com/gradle/gradle/pull/24943
  * RelativePath1B: As in RelativePath1, but manually inline the functions calling `System.arraycopy`
  * RelativePath1C: As in RelativePath1, but copy arrays with `for` loops rather than `System.arraycopy`
* RelativePath2: The new implementation of RelativePath with canonicalization in https://github.com/gradle/gradle/pull/24943
* RelativePath3: A proposed faster implementation of RelativePath with canonicalization that does not check whether
  segments in a parent path (which have already been canonicalized) are `.` or `..` (except as needed when applying
  `..` in a child path)

```
Benchmark                                    Mode  Cnt         Score        Error  Units
RelativePath1BBenchmark.testRelativePath1B  thrpt   25   9757023.075 ± 239258.310  ops/s
RelativePath1Benchmark.testRelativePath1    thrpt   25   9699841.254 ± 237311.719  ops/s
RelativePath2Benchmark.testRelativePath2    thrpt   25   2876108.564 ± 110458.781  ops/s
RelativePath3Benchmark.testRelativePath3    thrpt   25  14549687.945 ± 315704.997  ops/s
```

I don't understand why 3 outperforms 1 -- either I've made a mistake in my benchmarking setup (entirely possible),
or maybe array iteration just outperforms `System.arraycopy` for the short arrays used in my test cases.

EDIT: Yes, it looks like array iteration outperforms `System.arraycopy` here:

```
Benchmark                                    Mode  Cnt         Score        Error  Units
RelativePath1Benchmark.testRelativePath1    thrpt   25   9578653.104 ± 225023.457  ops/s
RelativePath1CBenchmark.testRelativePath1C  thrpt   25  28060250.279 ± 554192.667  ops/s
RelativePath3Benchmark.testRelativePath3    thrpt   25  14202226.844 ± 483012.062  ops/s
```

Note that code here is largely copied from the Gradle project; that code is licensed under the Apache 2.0 license.
