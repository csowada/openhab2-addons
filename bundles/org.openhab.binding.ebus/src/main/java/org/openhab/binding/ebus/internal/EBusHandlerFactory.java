/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
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
package org.openhab.binding.ebus.internal;

import static org.openhab.binding.ebus.internal.EBusBindingConstants.BINDING_ID;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.ebus.internal.handler.EBusBridgeHandler;
import org.openhab.binding.ebus.internal.handler.EBusHandler;
import org.openhab.binding.ebus.internal.services.EBusDiscoveryService;
import org.openhab.binding.ebus.internal.things.IEBusTypeProvider;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.configuration.EBusConfigurationVersion;
import de.csdev.ebus.core.EBusVersion;

/**
 * The {@link EBusHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Christian Sowada - Initial contribution
 */
@NonNullByDefault
@Component(service = { ThingHandlerFactory.class }, immediate = true)
public class EBusHandlerFactory extends BaseThingHandlerFactory {

    private final Logger logger = LoggerFactory.getLogger(EBusHandlerFactory.class);

    private Map<ThingUID, @Nullable ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();

    @Nullable
    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private volatile IEBusTypeProvider typeProvider;

    @Nullable
    public IEBusTypeProvider getEBusTypeProvider() {
        return typeProvider;
    }

    @Override
    // @Activate
    protected void activate(ComponentContext componentContext) {

        super.activate(componentContext);

        logger.info("Use eBUS binding {} [eBUS core: {}, eBUS configuration: {}]", EbusBindingVersion.getVersion(),
                EBusVersion.getVersion(), EBusConfigurationVersion.getVersion());

        logger.info("eBUS core -> timestamp {}, commit: #{}, build-no: #{}", EBusVersion.getBuildTimestamp(),
                EBusVersion.getBuildCommit(), EBusVersion.getBuildNumber());

        logger.info("eBUS configuration -> timestamp {}, commit: #{}, build-no: #{}",
                EBusConfigurationVersion.getBuildTimestamp(), EBusConfigurationVersion.getBuildCommit(),
                EBusConfigurationVersion.getBuildNumber());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory#createHandler(org.eclipse.smarthome.core.thing.
     * Thing)
     */
    @Override
    @Nullable
    protected ThingHandler createHandler(Thing thing) {

        if (EBusBridgeHandler.SUPPORTED_THING_TYPES.contains(thing.getThingTypeUID())) {
            return new EBusBridgeHandler((Bridge) thing, typeProvider, this);

        } else if (BINDING_ID.equals(thing.getUID().getBindingId())) {
            return new EBusHandler(thing);

        }
        throw new RuntimeException("Unable to create a Handler for " + thing.getThingTypeUID());
    }

    /**
     * @param bridgeHandler
     */
    public synchronized void registerDiscoveryService(EBusBridgeHandler bridgeHandler) {
        EBusDiscoveryService discoveryService = new EBusDiscoveryService(bridgeHandler);

        Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
        hashtable.put("service.pid", "discovery.ebus");

        ServiceRegistration<?> service = bundleContext.registerService(DiscoveryService.class.getName(),
                discoveryService, hashtable);

        this.discoveryServiceRegs.put(bridgeHandler.getThing().getUID(), service);

        discoveryService.activate(null);
    }

    public synchronized void disposeDiscoveryService(EBusBridgeHandler bridgeHandler) {

        ServiceRegistration<?> serviceReg = this.discoveryServiceRegs.get(bridgeHandler.getThing().getUID());

        if (serviceReg != null) {

            // remove discovery service
            EBusDiscoveryService service = (EBusDiscoveryService) bundleContext.getService(serviceReg.getReference());

            if (service != null) {
                service.deactivate();
            }

            serviceReg.unregister();
            discoveryServiceRegs.remove(bridgeHandler.getThing().getUID());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory#removeHandler(org.eclipse.smarthome.core.thing.
     * binding.ThingHandler)
     */
    @Override
    protected synchronized void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof EBusBridgeHandler) {
            disposeDiscoveryService((EBusBridgeHandler) thingHandler);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory#supportsThingType(org.eclipse.smarthome.core.thing.
     * ThingTypeUID)
     */
    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return BINDING_ID.equals(thingTypeUID.getBindingId());
    }
}
