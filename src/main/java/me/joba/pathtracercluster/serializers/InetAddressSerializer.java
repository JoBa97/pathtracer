package me.joba.pathtracercluster.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author balsfull
 */
public class InetAddressSerializer extends Serializer<InetAddress> {

    @Override
    public void write(Kryo kryo, Output output, InetAddress t) {
        byte[] addr = t.getAddress();
        output.writeInt(addr.length);
        for (int i = 0; i < addr.length; i++) {
            output.writeByte(addr[i]);
        }
    }

    @Override
    public InetAddress read(Kryo kryo, Input input, Class<? extends InetAddress> type) {
        int length = input.readInt();
        byte[] addr = new byte[length];
        for (int i = 0; i < length; i++) {
            addr[i] = input.readByte();
        }
        try {
            return InetAddress.getByAddress(addr);
        } catch (UnknownHostException ex) {
            return null;
        }
    }
}
