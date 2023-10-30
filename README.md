# gradle-relative-path-perf

How to run: Go through the benchmarks in `lib/src/jmh` and comment or uncomment the `@Benchmark` annotations depending on which ones
you want to run (each one takes ~8 minutes), then run `./gradlew jmh`.

* RelativePath1: The implementation of RelativePath prior to adding canonicalization in https://github.com/gradle/gradle/pull/24943
  * RelativePath1B: As in RelativePath1, but manually inline the functions calling `System.arraycopy`
  * RelativePath1C: As in RelativePath1, but copy arrays with `for` loops rather than `System.arraycopy`
* RelativePath2: The new implementation of RelativePath with canonicalization in https://github.com/gradle/gradle/pull/24943
  * RelativePath2B: Like RelativePath2 behavior-wise, but without the use of `Iterables.concat`.
* RelativePath3: A proposed faster implementation of RelativePath with canonicalization that does not check whether
  segments in a parent path (which have already been canonicalized) are `.` or `..` (except as needed when applying
  `..` in a child path)
  * Like RelativePath3, but with a use of `System.arraycopy` replaced with a `for` loop

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

EDIT 2: Additional testing suggests that a lot of the benefit of RelativePath3 comes from removing the use of
`Iterables.concat` (look at 2 vs. 2B in particular):

```
Benchmark                                    Mode  Cnt         Score         Error  Units
RelativePath1Benchmark.testRelativePath1    thrpt   25   9764661.421 ±  242810.865  ops/s
RelativePath1CBenchmark.testRelativePath1C  thrpt   25  28031284.837 ± 1187453.452  ops/s
RelativePath2BBenchmark.testRelativePath2B  thrpt   25  10795011.567 ± 2891877.888  ops/s
RelativePath2Benchmark.testRelativePath2    thrpt   25   2867507.837 ±  103353.121  ops/s
RelativePath3BBenchmark.testRelativePath3B  thrpt   25  12840665.177 ± 4181567.440  ops/s
RelativePath3Benchmark.testRelativePath3    thrpt   25  14509922.255 ±  319056.571  ops/s
```

Note on the above run of testing: One of the five forks of 3B gave results in the 24M ops/s range, while the rest gave
results of about 10M ops/s. Presumably these reflect two different choices the JIT compiler made in its optimizations.

Note that code here is largely copied from the Gradle project; that code is licensed under the Apache 2.0 license.
