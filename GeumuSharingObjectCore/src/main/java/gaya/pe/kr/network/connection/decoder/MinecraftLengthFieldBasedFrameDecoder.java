package gaya.pe.kr.network.connection.decoder;

import gaya.pe.kr.network.packet.global.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class MinecraftLengthFieldBasedFrameDecoder extends io.netty.handler.codec.LengthFieldBasedFrameDecoder {

    public MinecraftLengthFieldBasedFrameDecoder() {
        super(1048576, 0, 2, 0, 2); // 최대 1MB, 길이 필드는 2바이트
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);

        if (frame == null) {
            return null;
        }

        return MinecraftPacket.fromData(frame.readByte(), frame);
    }
}
