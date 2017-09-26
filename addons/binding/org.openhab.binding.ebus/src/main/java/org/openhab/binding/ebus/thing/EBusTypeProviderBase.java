/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ebus.thing;

import static org.openhab.binding.ebus.EBusBindingConstants.THING_TYPE_EBUS_BRIDGE;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.ThingTypeProvider;
import org.eclipse.smarthome.core.thing.type.ChannelGroupType;
import org.eclipse.smarthome.core.thing.type.ChannelGroupTypeUID;
import org.eclipse.smarthome.core.thing.type.ChannelType;
import org.eclipse.smarthome.core.thing.type.ChannelTypeProvider;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.thing.type.ThingType;

/**
 *
 * @author Christian Sowada - Initial contribution
 */
public class EBusTypeProviderBase implements ThingTypeProvider, ChannelTypeProvider {

    protected Map<ChannelGroupTypeUID, ChannelGroupType> channelGroupTypes = new HashMap<>();

    protected Map<ChannelTypeUID, ChannelType> channelTypes = new HashMap<>();

    protected final List<String> supportedBridgeTypeUIDs = Arrays.asList(THING_TYPE_EBUS_BRIDGE.getAsString());

    protected Map<ThingTypeUID, ThingType> thingTypes = new HashMap<>();

    @Override
    public ChannelGroupType getChannelGroupType(ChannelGroupTypeUID channelGroupTypeUID, Locale locale) {
        return channelGroupTypes.get(channelGroupTypeUID);
    }

    @Override
    public Collection<ChannelGroupType> getChannelGroupTypes(Locale locale) {
        return channelGroupTypes.values();
    }

    @Override
    public ChannelType getChannelType(ChannelTypeUID channelTypeUID, Locale locale) {
        return channelTypes.get(channelTypeUID);
    }

    @Override
    public Collection<ChannelType> getChannelTypes(Locale locale) {
        return channelTypes.values();
    }

    @Override
    public ThingType getThingType(ThingTypeUID thingTypeUID, Locale locale) {
        return thingTypes.get(thingTypeUID);
    }

    @Override
    public Collection<ThingType> getThingTypes(Locale locale) {
        return thingTypes.values();
    }
}
