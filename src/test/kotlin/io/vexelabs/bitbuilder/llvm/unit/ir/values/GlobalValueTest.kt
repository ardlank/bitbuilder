package io.vexelabs.bitbuilder.llvm.unit.ir.values

import io.vexelabs.bitbuilder.llvm.ir.DLLStorageClass
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.UnnamedAddress
import io.vexelabs.bitbuilder.llvm.ir.Visibility
import io.vexelabs.bitbuilder.llvm.ir.types.IntType
import io.vexelabs.bitbuilder.llvm.ir.values.GlobalValue
import io.vexelabs.bitbuilder.llvm.setup
import io.vexelabs.bitbuilder.rtti.cast
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.spekframework.spek2.Spek

internal class GlobalValueTest : Spek({
    setup()

    val module: Module by memoized()

    group("global value flags") {
        test("modifying the binary section") {
            val global = module.addGlobal("test", IntType(1))

            assertNull(global.getSection())

            global.setSection("data")

            assertEquals("data", global.getSection())
        }

        test("use the symbol visibility") {
            val global = module.addGlobal("test", IntType(1))

            for (it in Visibility.values()) {
                global.setVisibility(it)

                assertEquals(it, global.getVisibility())
            }
        }

        test("use the storage class") {
            val global = module.addGlobal("test", IntType(1))

            for (it in DLLStorageClass.values()) {
                global.setStorageClass(it)

                assertEquals(it, global.getStorageClass())
            }
        }

        test("use unnamed address importance") {
            val global = module.addGlobal("test", IntType(1))

            for (it in UnnamedAddress.values()) {
                global.setUnnamedAddress(it)

                assertEquals(it, global.getUnnamedAddress())
            }
        }

        test("defining alignment of value") {
            val global = module.addGlobal("test", IntType(1)).apply {
                setAlignment(16)
            }
            val ir = global.getIR().toString()

            assertEquals(16, global.getAlignment())
            assertTrue { ir.contains("align 16") }
        }
    }

    test("forwards declaration of global values") {
        val global = module.addGlobal("my_external", IntType(32)).apply {
            setExternallyInitialized(true)
        }

        assertTrue { global.isDeclaration() }
    }

    test("using the value type") {
        val global = module.addGlobal("test", IntType(32))
        val ptrType = global.getType()

        assertEquals(TypeKind.Pointer, ptrType.getTypeKind())

        val subject = global.getValueType()

        assertEquals(TypeKind.Integer, subject.getTypeKind())
    }

    test("pulling the module from a global value") {
        module.setModuleIdentifier("basic")

        val global = module.addGlobal("my_int", IntType(32))
        val globalModule = cast<GlobalValue>(global).getModule()

        assertEquals(
            module.getModuleIdentifier(), globalModule.getModuleIdentifier()
        )
    }
})
