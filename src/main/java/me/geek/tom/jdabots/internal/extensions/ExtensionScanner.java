package me.geek.tom.jdabots.internal.extensions;

import me.geek.tom.jdabots.api.extension.IExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class ExtensionScanner {
    public List<IExtension> scan() {
        ServiceLoader<IExtension> loader = ServiceLoader.load(IExtension.class);

        List<IExtension> ret = new ArrayList<>();
        for (IExtension ext : loader)
            ret.add(ext);
        return ret;
    }
}
