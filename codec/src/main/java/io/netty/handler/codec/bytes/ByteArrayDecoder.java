/*
 * Copyright 2011 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.bytes;

import io.netty.buffer.ChannelBuffer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.MessageEvent;
import io.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.frame.LengthFieldPrepender;
import io.netty.handler.codec.oneone.OneToOneDecoder;

/**
 * Decodes a received {@link ChannelBuffer} into an array of bytes.
 * A typical setup for TCP/IP would be:
 * <pre>
 * {@link ChannelPipeline} pipeline = ...;
 *
 * // Decoders
 * pipeline.addLast("frameDecoder",
 *                  new {@link LengthFieldBasedFrameDecoder}(1048576, 0, 4, 0, 4));
 * pipeline.addLast("bytesDecoder",
 *                  new {@link ByteArrayDecoder}());
 *
 * // Encoder
 * pipeline.addLast("frameEncoder", new {@link LengthFieldPrepender}(4));
 * pipeline.addLast("bytesEncoder", new {@link ByteArrayEncoder}());
 * </pre>
 * and then you can use an array of bytes instead of a {@link ChannelBuffer}
 * as a message:
 * <pre>
 * void messageReceived({@link ChannelHandlerContext} ctx, {@link MessageEvent} e) {
 *     byte[] bytes = (byte[]) e.getMessage();
 *     ...
 * }
 * </pre>
 */
public class ByteArrayDecoder extends OneToOneDecoder {

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer)) {
            return msg;
        }
        ChannelBuffer buf = (ChannelBuffer) msg;
        byte[] array;
        if (buf.hasArray()) {
            if (buf.arrayOffset() == 0 && buf.readableBytes() == buf.capacity()) {
                // we have no offset and the length is the same as the capacity. Its safe to reuse the array without copy it first
                array = buf.array();
            } else {
                // copy the ChannelBuffer to a byte array
                array = new byte[buf.readableBytes()];
                buf.getBytes(0, array);
            }
        } else {
            // copy the ChannelBuffer to a byte array

            array = new byte[buf.readableBytes()];
            buf.getBytes(0, array);
        }
        return array;
    }

}
