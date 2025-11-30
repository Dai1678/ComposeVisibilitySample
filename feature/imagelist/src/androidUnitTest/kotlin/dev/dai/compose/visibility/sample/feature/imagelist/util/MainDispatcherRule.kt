package dev.dai.compose.visibility.sample.feature.imagelist.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit TestRule that sets the Main dispatcher to a TestDispatcher for testing coroutines.
 *
 * This rule automatically:
 * - Sets the Main dispatcher before each test
 * - Resets the Main dispatcher after each test
 *
 * Usage:
 * ```
 * class MyViewModelTest {
 *     @get:Rule
 *     val mainDispatcherRule = MainDispatcherRule()
 *
 *     @Test
 *     fun myTest() = runTest {
 *         // Test code here
 *     }
 * }
 * ```
 */
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
