package com.baselineprofile.baselineprofile

import android.os.Build
import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * This test class benchmarks the speed of app startup.
 * Run this benchmark to verify how effective a Baseline Profile is.
 * It does this by comparing [CompilationMode.None], which represents the app with no Baseline
 * Profiles optimizations, and [CompilationMode.Partial], which uses Baseline Profiles.
 *
 * Run this benchmark to see startup measurements and captured system traces for verifying
 * the effectiveness of your Baseline Profiles. You can run it directly from Android
 * Studio as an instrumentation test, or run all benchmarks with this Gradle task:
 * ```
 * ./gradlew :app:baselineprofile:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=Macrobenchmark
 * ```
 *
 * You should run the benchmarks on a physical device, not an Android emulator, because the
 * emulator doesn't represent real world performance and shares system resources with its host.
 *
 * For more information, see the [Macrobenchmark documentation](https://d.android.com/macrobenchmark#create-macrobenchmark)
 * and the [instrumentation arguments documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args).
 **/
@RunWith(JUnit4::class)
@LargeTest
class StartupBenchmarks {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startupCompilationNone() =
        benchmark(CompilationMode.None())

    @Test
    fun startupCompilationBaselineProfiles() =
        benchmark(CompilationMode.Partial(BaselineProfileMode.Require))

    @OptIn(ExperimentalMetricApi::class)
    private fun benchmark(compilationMode : CompilationMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            rule.measureRepeated(
                packageName = "com.niyaj.popos",
                metrics = listOf(
                    StartupTimingMetric(),
                    FrameTimingMetric(),
                    TraceSectionMetric("RV CreateView"),
                    TraceSectionMetric("RV OnBindView"),
//                    PowerMetric(PowerMetric.Battery())
                ),
                compilationMode = compilationMode,
                startupMode = StartupMode.COLD,
                iterations = 10,
                setupBlock = {
                    pressHome()
                },
                measureBlock = {
                    startActivityAndWait()


                    // TODO Add interactions to wait for when your app is fully drawn.
                    // The app is fully drawn when Activity.reportFullyDrawn is called.
                    // For Jetpack Compose, you can use ReportDrawn, ReportDrawnWhen and ReportDrawnAfter
                    // from the AndroidX Activity library.

                    // Check the UiAutomator documentation for more information on how to
                    // interact with the app.
                    // https://d.android.com/training/testing/other-components/ui-automator

    //                StartupBenchmarks_startupCompilationBaselineProfiles
    //                timeToInitialDisplayMs   min   596.8,   median   660.0,   max 1,032.0
    //                Traces: Iteration 0 1 2 3 4 5 6 7 8 9
    //                StartupBenchmarks_startupCompilationNone
    //                timeToInitialDisplayMs   min 771.3,   median 789.4,   max 979.8
    //                Traces: Iteration 0 1 2 3 4 5 6 7 8 9
                }
            )
        }
    }
}