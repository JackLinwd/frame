package org.lwd.frame.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private Json json;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    private byte[] jsonObject = "json->object::".getBytes();
    private byte[] jsonArray = "json->array::".getBytes();

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

        if (object instanceof JSONObject) {
            serializeJson(jsonObject, object, outputStream);

            return;
        }

        if (object instanceof JSONArray) {
            serializeJson(jsonArray, object, outputStream);

            return;
        }

        Output output = new Output(outputStream);
        kryo().writeClassAndObject(output, object);
        output.close();
    }

    private void serializeJson(byte[] bytes, Object object, OutputStream outputStream) {
        try {
            outputStream.write(bytes);
            outputStream.write(json.toBytes(object));
            outputStream.close();
        } catch (IOException e) {
            logger.warn(e, "序列化JSON数据[{}]时发生异常！", object);
        }
    }

    @Override
    public <T> T unserialize(byte[] bytes) {
        if (validator.isEmpty(bytes))
            return null;

        T object = unserializeJson(bytes);

        return object == null ? unserializeByKryo(bytes) : object;
    }

    @Override
    public <T> T unserialize(InputStream inputStream) {
        if (inputStream == null)
            return null;

        return unserialize(io.read(inputStream));
    }

    @SuppressWarnings("unchecked")
    private <T> T unserializeJson(byte[] bytes) {
        if (bytes.length <= jsonArray.length)
            return null;

        if (validator.startsWith(bytes, jsonObject))
            return (T) json.toObject(new String(bytes).substring(jsonObject.length), false);

        if (validator.startsWith(bytes, jsonArray))
            return (T) json.toArray(new String(bytes).substring(jsonArray.length), false);

        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T unserializeByKryo(byte[] bytes) {
        Input input = new Input(bytes);
        T object = (T) kryo().readClassAndObject(input);
        input.close();

        return object;
    }

    private Kryo kryo() {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);

        return kryo;
    }
}
