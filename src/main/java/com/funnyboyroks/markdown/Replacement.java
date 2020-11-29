package com.funnyboyroks.markdown;

import java.util.regex.Matcher;

public interface Replacement {
    String replacement(Matcher m);
}
