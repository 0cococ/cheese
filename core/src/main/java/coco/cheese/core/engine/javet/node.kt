package coco.cheese.core.engine.javet

//import com.caoccao.javet.interop.proxy.JavetReflectionObjectFactory
//import com.caoccao.javet.javenode.JNEventLoop
//import com.caoccao.javet.javenode.enums.JNModuleType
import coco.cheese.core.Env
import coco.cheese.core.utils.AssetsUtils
import coco.cheese.core.utils.WORKING_DIRECTORY
import com.caoccao.javet.enums.JSRuntimeType
import com.caoccao.javet.exceptions.JavetException
import com.caoccao.javet.interception.jvm.JavetJVMInterceptor
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.interop.callback.JavetBuiltInModuleResolver
import com.caoccao.javet.interop.converters.JavetProxyConverter
import com.caoccao.javet.interop.engine.IJavetEngine
import com.caoccao.javet.interop.engine.IJavetEnginePool
import com.caoccao.javet.interop.engine.JavetEnginePool
import com.caoccao.javet.node.modules.NodeModuleModule
import com.caoccao.javet.utils.JavetOSUtils
import com.caoccao.javet.values.V8Value
import com.caoccao.javet.values.reference.IV8Module
import com.elvishew.xlog.XLog
import org.koin.java.KoinJavaComponent
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean


class node(val v8name: String = "main", val js: String = "") {
    lateinit var nodeRuntime: NodeRuntime
//    var jnEventLoop: JNEventLoop? = null
    var logger: ConsoleLogger? = null
    var daemonRunning = AtomicBoolean(false)
    var gcScheduled = AtomicBoolean(false)
    var daemonThread: Thread? = null
    var env = KoinJavaComponent.get<Env>(Env::class.java)
    fun createNodeRuntime(): NodeRuntime? {
        return try {
            logger = ConsoleLogger()
            val javetEnginePool: IJavetEnginePool<NodeRuntime> = JavetEnginePool()
            javetEnginePool.config.setJSRuntimeType(JSRuntimeType.Node)
            val javetEngine: IJavetEngine<NodeRuntime> = javetEnginePool.engine
            nodeRuntime = javetEngine.v8Runtime
            nodeRuntime.logger = logger
            val converter = JavetProxyConverter()
            converter.config.setProxyArrayEnabled(true)
            converter.config.setProxyListEnabled(true)
            converter.config.setProxyMapEnabled(true)
            converter.config.setProxySetEnabled(true)
            nodeRuntime.converter = converter
            nodeRuntime.setPromiseRejectCallback { _, _, value ->
                logger?.warn("\n${value}")
            }
            val javetJVMInterceptor = JavetJVMInterceptor(nodeRuntime)
            javetJVMInterceptor.register(nodeRuntime.globalObject)
            daemonThread = Thread(
                DaemonThread(
                    nodeRuntime,
                    daemonRunning,
                    gcScheduled
                )
            )
            daemonThread!!.start()
            env.nodeRuntime[v8name] = Env.NodeRuntimeNest(
                nodeRuntime,
                logger!!,
                daemonRunning,
                gcScheduled,
                daemonThread!!
            )

            val workingDirectory = File(
                JavetOSUtils.WORKING_DIRECTORY,
                WORKING_DIRECTORY().path + "/main/js"
            )
            nodeRuntime.getNodeModule(NodeModuleModule::class.java)
                .setRequireRootDirectory(workingDirectory)
            nodeRuntime.v8ModuleResolver = CBuiltInModuleResolver(
                File(
                    JavetOSUtils.WORKING_DIRECTORY,
                    WORKING_DIRECTORY().path + "/main/js"
                ).path
            )
            nodeRuntime.globalObject["Env"] = Env::class.java
//            nodeRuntime.globalObject["AppApi"] = AppApi::class.java
//            env.forEachClass("coco.cheese.core.api") { clz, result ->
//                nodeRuntime.createV8ValueObject().use { v8ValueObject ->
//                    nodeRuntime.globalObject.set(clz.simpleName, v8ValueObject)
//                    v8ValueObject.bind(result)
//                }
//            }
            env.forEachClass("coco.cheese.core.api",false) { clz, result ->
                nodeRuntime.globalObject[clz.simpleName] = result
            }
            val javetStandardConsoleInterceptor =JavetStandardConsoleInterceptor(nodeRuntime)
            javetStandardConsoleInterceptor.register(nodeRuntime.globalObject)
            env.settings["nodeName"]=v8name
            nodeRuntime.globalObject["env"] = env
            nodeRuntime.globalObject["settings"] = env.settings
//            nodeRuntime.getExecutor("require('process').on('uncaughtException', (err) => {\n" +
//                    "                console.error( err.stack);\n" +
//                    "            })").executeVoid()
//            nodeRuntime.getExecutor("let java =javet.package.java").executeVoid()
            //            nodeRuntime.getExecutor("let env =Env.Companion.get()").executeVoid()
//            nodeRuntime.getExecutor("let v8name =${v8name}").executeVoid()


            nodeRuntime.getExecutor(env.context.assets.open("init.js").use { inputStream ->
                inputStream.readBytes().toString(Charsets.UTF_8)
            }).executeVoid()

            XLog.i(nodeRuntime.version)
            nodeRuntime.getExecutor(js).executeVoid()
            nodeRuntime
        } catch (e: JavetException) {
            throw RuntimeException(e)
        }
    }

    fun run(name: String = "main.js", js: String = "") {
        val stringBuilder: StringBuilder = StringBuilder()
        val executeCode = {
            try {
                if (env.runTime.runMode){
                    ConsoleLogger.i("***Task (${name}) 运行开始***")
                }else{
                    ConsoleLogger.i("***Task (${ name.substringAfterLast("/js/")}) 运行开始***")
                }
                val field = com.caoccao.javet.utils.JavetOSUtils::class.java.getField("IS_ANDROID")
                field.isAccessible = true;
                field.set(null, false)
                nodeRuntime
                    .getExecutor(js)
                    ?.setResourceName(name)
                    ?.setModule(true)
                    ?.execute<V8Value>()
                    .use { v8Value ->
                        stringBuilder.append("\n$v8Value")
                    }
                if (env.runTime.runMode){
                    ConsoleLogger.i("***Task (${name}) 运行结束***")
                }else{
                    ConsoleLogger.i("***Task (${ name.substringAfterLast("/js/")}) 运行结束***")
                }
                field.set(null, true)
            } catch (t: Throwable) {
                stringBuilder.append("\n${t.message}")
                ConsoleLogger.e(t.toString(),t)
            } finally {
                gcScheduled.set(true)
            }

        }
        executeCode()
//        close()
    }

    fun close() {
        nodeRuntime.resetContext()
        nodeRuntime.terminateExecution()
        daemonRunning.set(false)
        daemonThread?.join()
        daemonThread = null
        nodeRuntime.close()
//        nodeRuntime
    }

    class CBuiltInModuleResolver(private val path: String) : JavetBuiltInModuleResolver() {
        fun findNodeModulesDirectory(startPath: String): String? {
            var currentPath = File(startPath).canonicalPath // 获取规范化的绝对路径
            while (currentPath.isNotEmpty()) {
                val nodeModulesDir = File(currentPath, "node_modules")
                if (nodeModulesDir.exists() && nodeModulesDir.isDirectory) {
                    return nodeModulesDir.canonicalPath
                }
                currentPath = File(currentPath).parent ?: break // 获取上级目录的绝对路径
            }
            return null // 没有找到 node_modules 目录
        }

        override fun resolve(
            v8Runtime: V8Runtime?,
            resourceName: String?,
            v8ModuleReferrer: IV8Module?
        ): IV8Module {
            val mod = File(
                findNodeModulesDirectory(path),
                resourceName!!
            )
//            XLog.i("导入名:${resourceName}")
            lateinit var jsFile: File
            jsFile = if (resourceName.contains(".js")) {
                if (v8ModuleReferrer!!.resourceName != "undefined") {
                    val parentDirectory = File(v8ModuleReferrer.resourceName).parentFile

                    File(parentDirectory, resourceName)
                } else {
                    File(path, resourceName)
                }
            } else {
                File(mod, "index.js")
            }
            if (jsFile.exists()) {
                return v8Runtime!!.getExecutor(jsFile).compileV8Module();
            } else {
                System.err.println(jsFile.absolutePath + " is not exists")
            }
            return super.resolve(v8Runtime, resourceName, v8ModuleReferrer)
        }

    }


}