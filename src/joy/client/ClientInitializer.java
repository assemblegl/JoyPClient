package joy.client;

import joy.netty.MsgDecoder;
import joy.netty.MsgEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
	
	private final SslContext sslCtx;
	private String sql;
	private String ip;
	private int port;
	
    public ClientInitializer(SslContext sslCtx,String ip,int port,String sql) {
        this.sslCtx = sslCtx;
        this.sql = sql;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc(),ip,port));
        }

        // Enable stream compression (you can remove these two if unnecessary)
        //pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        //pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

        // Add the number codec first,
        pipeline.addLast(new MsgDecoder());
        pipeline.addLast(new MsgEncoder());
        
        // and then business logic.
        pipeline.addLast(new ClientHandler(sql));
    }

}
