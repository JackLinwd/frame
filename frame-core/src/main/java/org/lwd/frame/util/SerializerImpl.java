package org.lwd.frame.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author lwd
 */
@Component("frame.util.serializer")
public class SerializerImpl implements Serializer {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    private KryoPool kryoPool = new KryoPool.Builder(Kryo::new).build();

    @Override
    public byte[] serialize(Object object) {
        if (object == null)
            return null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serialize(object, outputStream);

        return outputStream.toByteArray();
    }

    @Override
    public void serialize(Object object, OutputStream outputStream) {
        if (object == null || outputStream == null)
            return;

        Output output = new Output(outputStream);
        Kryo kryo = kryoPool.borrow();
        kryo.writeClass(output, object.getClass());
        kryo.writeObject(output, object);
        kryoPool.release(kryo);
        output.close();
    }

    @Override
    public <T> T unserialize(byte[] bytes) {
        return validator.isEmpty(bytes) ? null : unserialize(new ByteArrayInputStream(bytes));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unserialize(InputStream inputStream) {
        Input input = new Input(inputStream);
        Kryo kryo = kryoPool.borrow();
        T object = (T) kryo.readObject(input, kryo.readClass(input).getType());
        kryoPool.release(kryo);
        input.close();

        return object;
    }
}
