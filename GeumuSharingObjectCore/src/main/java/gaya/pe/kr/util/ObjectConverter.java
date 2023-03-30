package gaya.pe.kr.util;

import gaya.pe.kr.network.packet.global.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.*;
import java.util.Base64;

public class ObjectConverter {

    public static byte[] getByteFromObject(Object object) {
        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(io);
            os.writeObject(object);
            os.flush();
            return io.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ByteBuf getByteBufFromObject(Object object) {
        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(io);
            os.writeObject(object);
            os.flush();
            byte[] data = io.toByteArray();
            return Unpooled.wrappedBuffer(data);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Object getObject(byte[] serializedObject) {
        // byte[] -> object
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(serializedObject);
            ObjectInputStream is = new ObjectInputStream(in);
            return is.readObject();
        } catch ( Exception e) {
            return null;
        }
    }

    public static <T extends MinecraftPacket> T getMinecraftPacket(ByteBuf byteBuf, Class<T> packetClass) {
        try {
            byte[] data = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(data);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            return packetClass.cast(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    public static String getObjectAsString(byte[] serializedObject) {
        // byte[] -> object
        try {
            return Base64.getEncoder().encodeToString(serializedObject);
        } catch ( Exception e) {
            return null;
        }
    }
    public static byte[] getStringAsByte(String serializedObject) {
        // byte[] -> object
        try {
            return Base64.getDecoder().decode(serializedObject);
        } catch ( Exception e) {
            return null;
        }
    }




}