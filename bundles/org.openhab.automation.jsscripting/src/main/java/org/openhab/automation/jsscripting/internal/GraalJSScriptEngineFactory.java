/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.automation.jsscripting.internal;

import java.util.*;
import java.io.File;
import javax.script.ScriptEngine;

import org.openhab.core.OpenHAB;
import org.openhab.core.automation.module.script.ScriptEngineFactory;
import org.osgi.service.component.annotations.Component;

import com.oracle.truffle.js.scriptengine.GraalJSEngineFactory;

/**
 * An implementation of {@link ScriptEngineFactory} with customizations for GraalJS ScriptEngines.
 *
 * @author Jonathan Gilbert - Initial contribution
 */
@Component(service = ScriptEngineFactory.class)
public final class GraalJSScriptEngineFactory implements ScriptEngineFactory {

    @Override
    public List<String> getScriptTypes() {
        List<String> scriptTypes = new ArrayList<>();
        GraalJSEngineFactory graalJSEngineFactory = new GraalJSEngineFactory();

        scriptTypes.addAll(graalJSEngineFactory.getMimeTypes());
        scriptTypes.addAll(graalJSEngineFactory.getExtensions());

        return Collections.unmodifiableList(scriptTypes);
    }

    @Override
    public void scopeValues(ScriptEngine scriptEngine, Map<String, Object> scopeValues) {
        // noop; the are retrieved via modules, not injected
    }

    @Override
    public ScriptEngine createScriptEngine(String scriptType) {

        // final String MODULE_DIR = String.join(File.separator, OpenHAB.getConfigFolder(), "automation", "lib",
        //         "javascript", "personal");

        final String MODULE_DIR = String.join(File.separator, OpenHAB.getConfigFolder(), "automation", "jsr223-ts");

        OpenhabGraalJSScriptEngine engine = new OpenhabGraalJSScriptEngine(MODULE_DIR);
        return new DebuggingGraalScriptEngine<>(engine);
    }
}