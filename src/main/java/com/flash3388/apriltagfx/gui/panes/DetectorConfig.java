package com.flash3388.apriltagfx.gui.panes;

import com.flash3388.apriltagfx.gui.ProcessingControl;
import com.flash3388.apriltags4j.DetectorConfiguration;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DetectorConfig {

    private final ProcessingControl mProcessingControl;
    private final DetectorConfiguration mConfiguration;

    public DetectorConfig(ProcessingControl processingControl) {
        mProcessingControl = processingControl;
        mConfiguration = processingControl.getDetectorConfig();
    }

    public Object getValue(Function<DetectorConfiguration, Object> getter) {
        return getter.apply(mConfiguration);
    }

    public void setValue(BiConsumer<DetectorConfiguration, Object> setter, Object value) {
        setter.accept(mConfiguration, value);
        onConfigurationUpdated();
    }

    private void onConfigurationUpdated() {
        mProcessingControl.setDetectorConfig(mConfiguration);
    }
}
