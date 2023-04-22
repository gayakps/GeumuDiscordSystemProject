package gaya.pe.kr.network.connection.decoder;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketStartDirection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    PacketStartDirection packetStartDirection;

    public PacketDecoder(PacketStartDirection packetStartDirection) {
        this.packetStartDirection = packetStartDirection;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 최소 패킷 길이는 패킷 ID(1바이트) + 데이터 길이(2바이트)입니다.
        if (in.readableBytes() < 3) {
            return;
        }

        // 현재 readerIndex를 기억합니다.
        in.markReaderIndex();

//        System.out.printf("SIZE : %d :: IN : %d\n" , buf.readableBytes(), in.readableBytes());

        // 패킷 ID를 읽습니다.
        byte packetId = in.readByte();

        // 데이터 길이를 읽습니다.
        short dataLength = in.readShort();

//        System.out.printf("2개 읽어버림 SIZE : %d :: IN : %d [ PACKET ID : %d LENGTH : %d ]\n" , buf.readableBytes(), in.readableBytes(), packetId, dataLength);

        // 데이터가 모두 도착하지 않았으면 처리를 중지합니다.
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            System.out.println("데이터 모두 도착 X");
            return;
        }

        // 패킷 데이터를 읽습니다.
        ByteBuf data = in.readBytes(dataLength);

//        System.out.printf("%s -> [PACKET DECODER] Packet Id : %d Data Length : %d DATA SIZE : %d\n",packetStartDirection.name() , packetId, dataLength, data.readableBytes());

        // 패킷 객체를 생성합니다.
        AbstractMinecraftPacket packet = AbstractMinecraftPacket.fromData(packetId, data);

        if (in.readerIndex() != in.writerIndex()) {
            throw new DecoderException("Unused bytes exist in the end of packet: " + (in.writerIndex() - in.readerIndex()) + " bytes");
        }

        // 리스트에 패킷을 추가합니다.
        out.add(packet);



    }

}

