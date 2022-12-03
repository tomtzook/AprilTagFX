package com.flash3388.apriltagfx.gui.stream.selectors;


import com.flash3388.apriltagfx.gui.stream.ImageStream;

import java.util.Optional;

public interface StreamSelector {

    Optional<ImageStream> selectNew() throws Exception;
}
