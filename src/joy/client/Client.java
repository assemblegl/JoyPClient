package joy.client;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.SSLException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class Client {
	private boolean ssl;
    private String ip ;
    private int port ;   

    public Client(String ip,int port,boolean ssl){
    	this.ip = ip;
    	this.port = port;
    	this.ssl = ssl;
    }
    
    public Client(String ip,int port){
    	this.ip = ip;
    	this.port = port;
    	this.ssl = false;    	
    }
    
    public  String run(String sql) {
        // Configure SSL.
        SslContext sslCtx = null;
        if (ssl) {
            try {
				sslCtx = SslContextBuilder.forClient()
				    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			} catch (SSLException e) {				
				e.printStackTrace();
			}
        } else {
            sslCtx = null;
        }
                
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ClientInitializer(sslCtx,ip,port,sql));

            // Make a new connection.
            ChannelFuture f;
			try {
				f = b.connect(ip, port).sync();
				
				ClientHandler handler = (ClientHandler) f.channel().pipeline().last();
				String msg = handler.getBmsg();
				
				f.channel().closeFuture().sync();
				
				return msg;
			} catch (InterruptedException e) {
				return e.getMessage();
			}

        } finally {
            group.shutdownGracefully();
        }
    }

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static void main(String[] args) {
		if(args.length < 4){
			print();
			System.exit(1);
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");						
		
		String ip = args[0];
		int port = Integer.valueOf(args[1]);
		boolean ssl = false;
		
		if("1".equals(args[2])){
			ssl = true;
		}else{
			ssl = false;
		}
		
		String sql = args[3];
		
		System.out.println("start time:"+sdf.format(new Date())+",ip:"+ip+",port:"+port+",ssl:"+ssl+",sql:"+sql+".");						
		
		Client c = new Client(ip,port,ssl);
		System.out.println(c.run(sql));
		System.out.println("end time:"+sdf.format(new Date()));
		
	}
	
	private static void print(){
		System.out.println("useage: ip port ssl sql");
	}
    

}
