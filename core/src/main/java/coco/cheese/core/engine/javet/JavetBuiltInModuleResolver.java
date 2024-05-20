package coco.cheese.core.engine.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IV8ModuleResolver;
import com.caoccao.javet.node.modules.NodeModuleAny;
import com.caoccao.javet.values.reference.IV8Module;
import com.caoccao.javet.values.reference.V8ValueObject;

/**
 * The type Javet built in module resolver is for resolving the Node.js built-in modules.
 *
 * @since 3.0.1
 */
public class JavetBuiltInModuleResolver implements IV8ModuleResolver {

    /**
     * The constant PREFIX_NODE.
     *
     * @since 3.0.1
     */
    public static final String PREFIX_NODE = "";
    /**
     * The constant DEFAULT.
     *
     * @since 3.1.0
     */
    public static final String DEFAULT = "default";

    @Override
    public IV8Module resolve(V8Runtime v8Runtime, String resourceName, IV8Module v8ModuleReferrer)
            throws JavetException {
        IV8Module iV8Module = null;
        // It only works for Node.js runtime and module names starting with "node:".
        if (v8Runtime.getJSRuntimeType().isNode() && resourceName != null && resourceName.startsWith(PREFIX_NODE)) {
            String moduleName = resourceName.substring(PREFIX_NODE.length());
            NodeRuntime nodeRuntime = (NodeRuntime) v8Runtime;
            NodeModuleAny nodeModuleAny = nodeRuntime.getNodeModule(moduleName, NodeModuleAny.class);
            V8ValueObject v8ValueObject = nodeModuleAny.getModuleObject();
            if (!v8ValueObject.has(DEFAULT)) {
                v8ValueObject.set(DEFAULT, v8ValueObject);
            }
            iV8Module = v8Runtime.createV8Module(resourceName, v8ValueObject);
        }
        return iV8Module;
    }
}
