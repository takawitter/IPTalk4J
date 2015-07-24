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

import java.util.EventListener;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

public class EventLIstenerListTest {
	interface Listener extends EventListener{
		void onEvent(int value);
	}
	@Test
	public void test(){
		EventListenerList<Listener> listeners = new EventListenerList<>(Listener.class);
		final AtomicInteger val = new AtomicInteger(0);
		listeners.add(new Listener(){
			@Override
			public void onEvent(int value) {
				val.set(value);
			}
		});
		listeners.fire().onEvent(100);
		Assert.assertEquals(100, val.get());
	}
}
