package dev.supergrecko.kllvm.unit.ir.types

import dev.supergrecko.kllvm.unit.ir.Context
import dev.supergrecko.kllvm.unit.ir.Type
import dev.supergrecko.kllvm.unit.ir.TypeKind
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class X86MMXType public constructor(
    context: Context = Context.getGlobalContext()
) : Type() {
    init {
        ref = LLVM.LLVMX86MMXTypeInContext(context.ref)
    }

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
        requireKind(TypeKind.X86_MMX)
    }
}