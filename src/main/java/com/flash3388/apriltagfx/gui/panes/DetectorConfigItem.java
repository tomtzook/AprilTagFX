package com.flash3388.apriltagfx.gui.panes;

import com.flash3388.apriltags4j.DetectorConfiguration;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DetectorConfigItem implements PropertySheet.Item {

    private final String mCategory;
    private final String mName;
    private final String mDescription;
    private final Class<?> mType;
    private final DetectorConfig mConfiguration;
    private final Function<DetectorConfiguration, Object> mGetter;
    private final BiConsumer<DetectorConfiguration, Object> mSetter;

    public DetectorConfigItem(String category, String name, String description, Class<?> type,
                              DetectorConfig configuration,
                              Function<DetectorConfiguration, Object> getter,
                              BiConsumer<DetectorConfiguration, Object> setter) {
        mCategory = category;
        mName = name;
        mDescription = description;
        mType = type;
        mConfiguration = configuration;
        mGetter = getter;
        mSetter = setter;
    }

    @Override
    public Class<?> getType() {
        return mType;
    }

    @Override
    public String getCategory() {
        return mCategory;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getDescription() {
        return mDescription;
    }

    @Override
    public Object getValue() {
        return mConfiguration.getValue(mGetter);
    }

    @Override
    public void setValue(Object value) {
        mConfiguration.setValue(mSetter, mType.cast(value));
    }

    @Override
    public Optional<ObservableValue<? extends Object>> getObservableValue() {
        return Optional.empty();
    }
}
