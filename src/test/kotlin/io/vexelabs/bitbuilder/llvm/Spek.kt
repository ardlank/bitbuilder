// Eat all warnings about unused memoized variables
@file:Suppress("UNUSED_VARIABLE")

package io.vexelabs.bitbuilder.llvm

import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Module
import org.spekframework.spek2.dsl.Root

internal fun Root.setup() {
    val context by memoized(
        factory = { Context() },
        destructor = { it.dispose() }
    )

    val module by memoized(
        factory = { Module("test") },
        destructor = { it.dispose() }
    )

    val builder by memoized(
        factory = { Builder() },
        destructor = { it.dispose() }
    )

    val utils by memoized(
        factory = { TestUtils() },
        destructor = { it.destruct() }
    )
}
