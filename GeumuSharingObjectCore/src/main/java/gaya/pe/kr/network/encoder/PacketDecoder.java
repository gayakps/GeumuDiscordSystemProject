package gaya.pe.kr.network.encoder;

import gaya.pe.kr.network.packet.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 최소 패킷 길이는 패킷 ID(1바이트) + 데이터 길이(2바이트)입니다.
        if (in.readableBytes() < 3) {
            return;
        }

        // 현재 readerIndex를 기억합니다.
        in.markReaderIndex();

        // 패킷 ID를 읽습니다.
        byte packetId = in.readByte();

        // 데이터 길이를 읽습니다.
        short dataLength = in.readShort();

        // 데이터가 모두 도착하지 않았으면 처리를 중지합니다.
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        // 패킷 데이터를 읽습니다.
        ByteBuf data = in.readBytes(dataLength);

        // 패킷 객체를 생성합니다.
        MinecraftPacket packet = MinecraftPacket.fromData(packetId, data);

        // 리스트에 패킷을 추가합니다.
        out.add(packet);
    }

}

