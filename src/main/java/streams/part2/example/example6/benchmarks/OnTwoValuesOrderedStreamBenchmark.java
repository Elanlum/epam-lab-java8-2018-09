package streams.part2.example.example6.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import streams.part2.example.example6.IntegerSummaryStatistics;
import streams.part2.example.example6.IntegerSummaryStatisticsCollector;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Benchmark)
@SuppressWarnings("unused")
public abstract class OnTwoValuesOrderedStreamBenchmark {

    @Param({"100000000", "10000000", "1000000", "100000"})
    public int dataLength;

    private int[] data;
    private IntegerSummaryStatisticsCollector collector;

    public abstract Supplier<IntegerSummaryStatisticsCollector> statisticsCollectorFactory();

    @Setup
    public void setup() {
        collector = statisticsCollectorFactory().get();
        data = IntStream.iterate(0, value -> value ^ 1)
                        .limit(dataLength)
                        .toArray();
    }

    @Benchmark
    public IntegerSummaryStatistics compute() {
        return Arrays.stream(data)
                     .parallel()
                     .boxed()
                     .collect(collector);
    }
}
