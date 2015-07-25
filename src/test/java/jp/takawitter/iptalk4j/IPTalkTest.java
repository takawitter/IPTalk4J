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
package jp.takawitter.iptalk4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.Assert;
import org.junit.Test;

public class IPTalkTest {
	@Test
	public void test() throws Throwable{
		final StringBuilder b = new StringBuilder();
		IPTalk.registerMainWindowDisplayListener(1, new TextListener() {
			@Override
			public void onReceive(InetSocketAddress sender, String text) {
				b.append(text);
				synchronized(b){
					b.notify();
				}
			}
		});
		IPTalk.sendMainWindowDisplay(InetAddress.getLocalHost(), 1, "hello");
		synchronized(b){
			b.wait();
		}
		Assert.assertEquals("hello", b.toString());
		IPTalk.shutdown();
	}
}
