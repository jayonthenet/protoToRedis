
package de.nnd.protoToRedis;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import de.nnd.protoToRedis.Protos.Testy;
import io.lettuce.core.codec.RedisCodec;
import io.netty.buffer.Unpooled;


/**
 * A {@link RedisCodec} that provides typesafe operations for Testy protobufs.
 */
public class TestyCodec implements RedisCodec<String, Testy> {

    private static final Charset charset = Charset.defaultCharset();
    private static final Logger log = Logger.getLogger(TestyCodec.class.getName());

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return Unpooled.wrappedBuffer(bytes).toString(charset);
    }

    @Override
    public Testy decodeValue(ByteBuffer bytes) {
        try {
            return Testy.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            log.warning(e.toString());
            return null;
        }
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return ByteBuffer.wrap(key.getBytes(charset));
    }

    @Override
    public ByteBuffer encodeValue(Testy value) {
        return ByteBuffer.wrap(value.toByteArray());
    }

    

}
