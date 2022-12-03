package com.flash3388.apriltagfx.vision.runner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Result {

    private final Map<Integer, TagInfo> mTagInfo;

    public Result() {
        mTagInfo = new HashMap<>();
    }

    public void put(TagInfo info) {
        mTagInfo.put(info.getId(), info);
    }

    public Collection<TagInfo> getValues() {
        return mTagInfo.values();
    }
}
