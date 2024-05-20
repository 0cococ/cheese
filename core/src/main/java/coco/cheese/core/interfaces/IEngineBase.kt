package coco.cheese.core.interfaces

import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.interop.V8Runtime
import java.util.concurrent.ExecutorService

interface IEngineBase {
    val executorService: ExecutorService
    var nodeRuntime: NodeRuntime
    fun setNodeRuntime(key: String = "main")

}