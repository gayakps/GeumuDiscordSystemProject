package gaya.pe.kr.network.connection.encoder;

import gaya.pe.kr.network.packet.global.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<MinecraftPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MinecraftPacket packet, ByteBuf out) throws Exception {
        // 패킷 ID를 쓰고
        ByteBuf data = packet.getData();

        out.writeByte(packet.getType().getId()); // 첫 번째 바이트에 패킷의 타입 삽입
        out.writeShort(data.readableBytes()); // 두 번째, 세 번째 바이트에 패킷 길이 삽입
        out.writeBytes(data); // 패킷 데이터를 삽입
    }

}
