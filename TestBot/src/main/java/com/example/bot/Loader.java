package com.example.bot;

import me.geek.tom.jdabots.api.startup.BotLoader;

import javax.security.auth.login.LoginException;
import java.util.Collections;

public class Loader {
    public static void main(String[] args) throws LoginException {
        BotLoader.main(args, ";", Collections.emptyList());
    }
}
