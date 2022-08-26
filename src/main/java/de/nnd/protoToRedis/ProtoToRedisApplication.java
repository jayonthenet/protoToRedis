package de.nnd.protoToRedis;

import java.util.Arrays;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.protobuf.ByteString;

import de.nnd.ProtoCommands;
import de.nnd.protoToRedis.Protos.Testy;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.dynamic.RedisCommandFactory;
import io.lettuce.core.support.ConnectionPoolSupport;

@SpringBootApplication
public class ProtoToRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProtoToRedisApplication.class, args);
		Testy testy = Testy.newBuilder().setId(1).setName("Herbert").setGreeting("Moin!").build();

		RedisClient client = RedisClient.create(RedisURI.create("localhost", 6379));

		try (GenericObjectPool<StatefulRedisConnection<String, String>> pool = ConnectionPoolSupport
					.createGenericObjectPool(() -> client.connect(), new GenericObjectPoolConfig())) {
			try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
				RedisCommands<String, String> commands = connection.sync();
				commands.multi();
				commands.set("testy", "so");
				commands.set("testy2", "cool");
				commands.set("proto", testy.toByteString().toStringUtf8());
				commands.exec();
				System.out.println("Try to get -> " + Testy.parseFrom(ByteString.copyFromUtf8(commands.get("proto"))).getGreeting());
			} catch(Exception e) {
				System.out.println(e.toString());
			}
			pool.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		StatefulRedisConnection conn = client.connect();
		RedisCommandFactory fac = new RedisCommandFactory(conn, Arrays.asList(new TestyCodec()));
		ProtoCommands pcom = fac.getCommands(ProtoCommands.class);

		pcom.set("pcomTest", testy);

		System.out.println(pcom.get("pcomTest").toString());

		client.shutdown();

		System.out.println("This works...");
	}

}
