package io.vexelabs.bitbuilder.llvm.target

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import io.vexelabs.bitbuilder.llvm.internal.util.toLLVMBool
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.support.MemoryBuffer
import java.io.File
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef
import org.bytedeco.llvm.LLVM.LLVMTargetMachineRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::TargetMachine
 *
 * A target machine is a target triple which may own multiple [Target]s which
 * it will be capable of compiling to.
 *
 * @see LLVMTargetMachineRef
 */
public class TargetMachine internal constructor() :
    ContainsReference<LLVMTargetMachineRef>, Disposable {
    public override lateinit var ref: LLVMTargetMachineRef
        internal set
    public override var valid: Boolean = true

    public constructor(llvmRef: LLVMTargetMachineRef) : this() {
        ref = llvmRef
    }

    /**
     * Create a new Target Machine for a specific Target
     *
     * @see LLVM.LLVMCreateTargetMachine
     */
    public constructor(
        target: Target,
        triple: String,
        cpu: String,
        features: String,
        optimizationLevel: CodeGenOptimizationLevel,
        relocMode: RelocMode,
        codeModel: CodeModel
    ) : this() {
        ref = LLVM.LLVMCreateTargetMachine(
            target.ref,
            BytePointer(triple),
            BytePointer(cpu),
            BytePointer(features),
            optimizationLevel.value,
            relocMode.value,
            codeModel.value
        )
    }

    /**
     * Get the start of the target iterator
     *
     * @see LLVM.LLVMGetFirstTarget
     */
    public fun getTargetIterator(): Target.Iterator? {
        val target = LLVM.LLVMGetFirstTarget()

        return target?.let { Target.Iterator(it) }
    }

    /**
     * Get the target for this target machine
     *
     * @see LLVM.LLVMGetTargetMachineTarget
     */
    public fun getTarget(): Target {
        val target = LLVM.LLVMGetTargetMachineTarget(ref)

        return Target(target)
    }

    /**
     * Get the target triple
     *
     * @see LLVM.LLVMGetTargetMachineTriple
     */
    public fun getTriple(): String {
        return LLVM.LLVMGetTargetMachineTriple(ref).string
    }

    /**
     * Get the target cpu
     *
     * @see LLVM.LLVMGetTargetMachineCPU
     */
    public fun getCPU(): String {
        return LLVM.LLVMGetTargetMachineCPU(ref).string
    }

    /**
     * Get the target features
     *
     * @see LLVM.LLVMGetTargetMachineFeatureString
     */
    public fun getFeatures(): String {
        return LLVM.LLVMGetTargetMachineFeatureString(ref).string
    }

    /**
     * Set whether target uses verbose asm
     *
     * @see LLVM.LLVMSetTargetMachineAsmVerbosity
     */
    public fun setAsmVerbosity(verboseAsm: Boolean) {
        LLVM.LLVMSetTargetMachineAsmVerbosity(ref, verboseAsm.toLLVMBool())
    }

    /**
     * Emit the given [module] to the target [file]
     *
     * This emits the given [module] into either an assembly file or an
     * object file
     *
     * @see LLVM.LLVMTargetMachineEmitToFile
     */
    public fun emitToFile(
        module: Module,
        file: File,
        fileType: CodeGenFileType
    ) {
        val err = BytePointer(0L)
        val result = LLVM.LLVMTargetMachineEmitToFile(
            ref,
            module.ref,
            BytePointer(file.absolutePath),
            fileType.value,
            err
        )

        if (result != 0) {
            throw RuntimeException(err.string)
        }
    }

    /**
     * Emit the given [module] to a memory buffer
     *
     * This compiles the given [module] into either an assembly file or an
     * object file
     *
     * @see LLVM.LLVMTargetMachineEmitToMemoryBuffer
     */
    public fun emitToMemoryBuffer(
        module: Module,
        fileType: CodeGenFileType
    ): MemoryBuffer {
        val err = BytePointer(0L)
        val out = LLVMMemoryBufferRef()
        val result = LLVM.LLVMTargetMachineEmitToMemoryBuffer(
            ref,
            module.ref,
            fileType.value,
            err,
            out
        )

        return if (result == 0) {
            MemoryBuffer(out)
        } else {
            throw RuntimeException(err.string)
        }
    }

    public override fun dispose() {
        require(valid) { "This target machine has already been disposed." }

        valid = false

        LLVM.LLVMDisposeTargetMachine(ref)
    }
}
