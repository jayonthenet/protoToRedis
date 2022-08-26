package de.nnd;

import de.nnd.protoToRedis.Protos.Testy;
import io.lettuce.core.dynamic.Commands;
import io.lettuce.core.dynamic.annotation.Command;

public interface ProtoCommands extends Commands {
    @Command("GET :key")
    Testy get(String key);
    
    @Command("SET :id :value")
    String set(String id, Testy value);
}
