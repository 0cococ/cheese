/*
 * Copyright (c) 2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package coco.cheese.core.engine.javet

import com.caoccao.javet.enums.V8AwaitMode
import com.caoccao.javet.interop.V8Runtime
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean


class DaemonThread(
    private val v8Runtime: V8Runtime,
    private val running: AtomicBoolean,
    private val gcScheduled: AtomicBoolean,
) : Runnable {
    override fun run() {
        running.set(true)
        gcScheduled.set(false)
        while (running.get()) {
            v8Runtime.await(V8AwaitMode.RunOnce)
            if (gcScheduled.get()) {
                System.gc()
                System.runFinalization()
                v8Runtime.lowMemoryNotification()
                gcScheduled.set(false)
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(100L)
                } catch (_: Throwable) {
                }
            }
        }
        System.gc()
        System.runFinalization()
        v8Runtime.lowMemoryNotification()
    }
}