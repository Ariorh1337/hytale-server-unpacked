package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageDecoder<I> extends ChannelInboundHandlerAdapter {
   private final TypeParameterMatcher matcher;
   private boolean decodeCalled;
   private boolean messageProduced;

   protected MessageToMessageDecoder() {
      this.matcher = TypeParameterMatcher.find(this, MessageToMessageDecoder.class, "I");
   }

   protected MessageToMessageDecoder(Class<? extends I> inboundMessageType) {
      this.matcher = TypeParameterMatcher.get(inboundMessageType);
   }

   public boolean acceptInboundMessage(Object msg) throws Exception {
      return this.matcher.match(msg);
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      this.decodeCalled = true;
      CodecOutputList out = CodecOutputList.newInstance();

      try {
         if (this.acceptInboundMessage(msg)) {
            I cast = (I)msg;

            try {
               this.decode(ctx, cast, out);
            } finally {
               ReferenceCountUtil.release(cast);
            }
         } else {
            out.add(msg);
         }
      } catch (DecoderException e) {
         throw e;
      } catch (Exception e) {
         throw new DecoderException(e);
      } finally {
         try {
            int size = out.size();
            this.messageProduced |= size > 0;

            for (int i = 0; i < size; i++) {
               ctx.fireChannelRead(out.getUnsafe(i));
            }
         } finally {
            out.recycle();
         }
      }
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      if (!this.isSharable()) {
         if (this.decodeCalled && !this.messageProduced && !ctx.channel().config().isAutoRead()) {
            ctx.read();
         }

         this.decodeCalled = false;
         this.messageProduced = false;
      }

      ctx.fireChannelReadComplete();
   }

   protected abstract void decode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}
