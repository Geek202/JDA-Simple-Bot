package me.geek.tom.jdabots.internal.extensions;

import me.geek.tom.jdabots.api.extension.IExtension;
import me.geek.tom.jdabots.internal.commands.CommandScanner;
import me.geek.tom.jdabots.internal.commands.CommandWrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ExtensionLoader {

    private static final CommandScanner scanner = new CommandScanner();

    public static List<CommandWrapper> readCommands(IExtension ext) {
        try {
            return scanner.scanExtension(ext);
        } catch (IllegalAccessException | IOException | InstantiationException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
