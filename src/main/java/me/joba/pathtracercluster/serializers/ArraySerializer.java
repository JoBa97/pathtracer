package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.lang.reflect.Array;

/**
 *
 * @author balsfull
 */
public class ArraySerializer<T> extends Serializer<T[]> {

    private final Class<T> c;
    
    public ArraySerializer(Class<T> c) {
        this.c = c;
    }
    
    @Override
    public void write(Kryo kryo, Output output, T[] t) {
        output.writeInt(t.length);
        for (int i = 0; i < t.length; i++) {
            kryo.writeObject(output, t[i]);
        }
    }

    @Override
    public T[] read(Kryo kryo, Input input, Class<? extends T[]> type) {
        int count = input.readInt();
        T[] arr = (T[])Array.newInstance(c, count);
        for (int i = 0; i < count; i++) {
            arr[i] = kryo.readObject(input, c);
        }
        return arr;
    }   
}
