package dev.supergrecko.vexe.llvm.unit.ir

import dev.supergrecko.vexe.llvm.ir.ValueKind
import dev.supergrecko.vexe.llvm.ir.types.IntType
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantInt
import dev.supergrecko.vexe.test.TestSuite
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ValueTest : TestSuite({
    describe("Creation of ConstAllOne type") {
        val type = IntType(32)
        val value = type.getConstantAllOnes()

        assertEquals(ValueKind.ConstantInt, value.getValueKind())
    }

    describe("Creation of ConstNull type") {
        val type = IntType(32)
        val value = type.getConstantNull()

        assertEquals(ValueKind.ConstantInt, value.getValueKind())
        assertTrue { value.isNull() }
    }

    describe("Creation of nullptr type") {
        val type = IntType(32)
        val nullptr = type.getConstantNullPointer()

        assertEquals(ValueKind.ConstantPointerNull, nullptr.getValueKind())
        assertTrue { nullptr.isNull() }
    }

    describe("Creation of undefined type") {
        val type = IntType(1032)
        val undef = type.getConstantUndef()

        assertEquals(ValueKind.UndefValue, undef.getValueKind())
        assertTrue { undef.isUndef() }
    }

    describe("Value's pulled type matches input type") {
        val type = IntType(32)
        val value = ConstantInt(type, 1L, true)

        val valueType = value.getType()

        assertEquals(type.getTypeKind(), valueType.getTypeKind())
        assertEquals(type.getTypeWidth(), IntType(valueType.ref).getTypeWidth())
        assertTrue { value.isConstant() }
    }
})