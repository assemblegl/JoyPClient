package joy.netty;

import joy.global.Context;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.math.BigInteger;
import java.util.List;


public class MsgDecoder extends ByteToMessageDecoder {

	
	 @Override
	 protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		// Wait until the length prefix is available.
		 if (in.readableBytes() < 12) {
	            return;
	        }

	        in.markReaderIndex();

	        // Check the head number.
	        int headNumber = in.readInt();
	        if (headNumber != Context.headNum) {
	            in.resetReaderIndex();
	            throw new CorruptedFrameException("Invalid head number: " + headNumber);
	        }
	        
	        // check version
	        int msgVersion = in.readInt();
	        if(msgVersion != Context.msgVersion){
	        	throw new CorruptedFrameException("Invalid head msgVersion: " + msgVersion+",myVersion:"+Context.msgVersion);	        	
	        }
	        //printInfo("get version:"+msgVersion+",myVersion:"+Context.msgVersion);
	        
	        // Wait until the whole data is available.
	        int dataLength = in.readInt();
	        if (in.readableBytes() < dataLength) {
	            in.resetReaderIndex();
	            return;
	        }

	        // Convert the received data into a new BigInteger.
	        byte[] decoded = new byte[dataLength];
	        in.readBytes(decoded);

	        out.add(new String(decoded));       
		 
	    }   
	 
	 public void printError(String Content){		
			String ec=this.getClass().getName()+","+Content;				
			System.out.println(ec);
			
		}
		
	 public void printInfo(String Content){
			String ec=this.getClass().getName()+","+Content;		
			System.out.println(ec);
		
		}	

}
