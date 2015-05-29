package joy.netty;

import joy.global.Context;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.math.BigInteger;

public class MsgEncoder extends MessageToByteEncoder<String> {	
	
	@Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) {

        // Convert the number into a byte array.
        byte[] data = msg.getBytes();
        int dataLength = data.length;
               
        // Write a message.              
        out.writeInt(Context.headNum);  //data head
        out.writeInt(Context.msgVersion);  // data version
        out.writeInt(dataLength);  // data length
        out.writeBytes(data);      // data
        
    }

}
