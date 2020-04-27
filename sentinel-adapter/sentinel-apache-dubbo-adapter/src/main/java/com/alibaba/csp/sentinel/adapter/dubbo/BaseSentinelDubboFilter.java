/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.adapter.dubbo;


import java.util.function.BiConsumer;

import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Result;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;

/**
 * Base Class of the {@link SentinelDubboProviderFilter} and {@link SentinelDubboConsumerFilter}.
 *
 * @author Zechao Zheng
 */

public abstract class BaseSentinelDubboFilter implements Filter {
    
    protected Result wrapResult(Result result, final Entry interfaceEntry, final Entry methodEntry, final Boolean shouldExitContext) {
        result.whenCompleteWithContext(new BiConsumer<Result, Throwable>() {
            
            @Override
            public void accept(Result t, Throwable u) {
                // XXX how to deal with DubboConfig.getDubboBizExceptionTraceEnabled() ?
                if (methodEntry != null) {
                    Tracer.traceEntry(u, methodEntry);
                    methodEntry.exit();
                }
                if (interfaceEntry != null) {
                    Tracer.traceEntry(u, interfaceEntry);
                    interfaceEntry.exit();
                }
                if (shouldExitContext) {
                    ContextUtil.exit();
                }
            }
        });
        return result;
    }
}
