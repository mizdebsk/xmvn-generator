/*-
 * Copyright (c) 2023-2024 Red Hat, Inc.
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
package org.fedoraproject.xmvn.generator.stub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fedoraproject.xmvn.generator.BuildContext;
import org.fedoraproject.xmvn.generator.Hook;
import org.fedoraproject.xmvn.generator.HookFactory;
import org.fedoraproject.xmvn.generator.callback.Callback;
import org.fedoraproject.xmvn.generator.logging.Logger;

class CompoundHook {
    private final List<Hook> hooks = new ArrayList<>();

    public CompoundHook(BuildContext buildContext) {
        if (!buildContext.eval("%{?__xmvngen_debug}").isEmpty()) {
            Logger.enableDebug();
        }
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            for (String cn : buildContext.eval("%{?__xmvngen_post_install_hooks}").split("\\s+")) {
                if (!cn.isEmpty()) {
                    HookFactory factory = (HookFactory) cl.loadClass(cn).getDeclaredConstructor().newInstance();
                    hooks.add(factory.createHook(buildContext));
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        if (hooks.isEmpty()) {
            buildContext.eval("%{warn:xmvn-generator: no post-install hooks were specified}");
        }
    }

    void runHook() {
        Logger.startLogging();
        Logger.debug("Post-install hooks");
        for (Hook hook : hooks) {
            Logger.startNewSection();
            Logger.debug("Running " + hook + " (" + hook.getClass().getCanonicalName() + ")");
            hook.run();
        }
        Logger.finishLogging();
    }

    public String setUpHook() throws IOException {
        Callback cb = Callback.setUp(this::runHook);
        return String.join(" ", cb.getCommand());
    }
}
