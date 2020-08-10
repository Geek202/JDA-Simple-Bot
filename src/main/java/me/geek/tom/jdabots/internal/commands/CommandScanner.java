package me.geek.tom.jdabots.internal.commands;

import com.google.common.reflect.ClassPath;
import me.geek.tom.jdabots.api.command.Command;
import me.geek.tom.jdabots.api.command.ICommand;
import me.geek.tom.jdabots.api.extension.IExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CommandScanner {

    public List<CommandWrapper> scanExtension(IExtension extension) throws IllegalAccessException, IOException, InstantiationException {
        return scanPackage(extension.getClass().getClassLoader(), extension.getCommandsPackage());
    }

    private List<CommandWrapper> scanPackage(ClassLoader loader, String packageName) throws IOException, IllegalAccessException, InstantiationException {
        ClassPath classPath = ClassPath.from(loader);

        List<CommandWrapper> commands = new ArrayList<>();
        for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(packageName)) {
            Class<?> cls = info.load();
            if (cls.isAnnotationPresent(Command.class)) {
                ICommand cmd = (ICommand) cls.newInstance();
                Command annotation = cls.getAnnotation(Command.class);
                commands.add(new CommandWrapper(cmd, annotation.name(), annotation.description()));
            }
        }

        return commands;
    }

}
