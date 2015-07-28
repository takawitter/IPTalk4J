/*
 * Copyright 2015 Takao Nakaguchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.takawitter.iptalk4j.util.java.net;

import java.net.DatagramPacket;
import java.nio.charset.Charset;

public class DatagramPacketUtil {
	public static String getDataAsWindows31JString(DatagramPacket packet){
		return new String(
				packet.getData(), packet.getOffset(), packet.getLength(),
				windows31j
				);
	}

	public static String getDataAsUTF8String(DatagramPacket packet){
		return new String(
				packet.getData(), packet.getOffset(), packet.getLength(),
				utf8
				);
	}

	public static DatagramPacket createFromWindows31JString(String str){
		byte[] b = str.getBytes(windows31j);
		return new DatagramPacket(b, b.length);
	}

	public static DatagramPacket createFromUTF8String(DatagramPacket packet, String str){
		byte[] b = str.getBytes(utf8);
		return new DatagramPacket(b, b.length);
	}

	private static Charset windows31j = Charset.forName("Windows-31J");
	private static Charset utf8 = Charset.forName("UTF-8");
}
