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
package jp.takawitter.iptalk4j.udp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EventListenerList<T>{
	public EventListenerList(Class<T> clazz){
		this.fireProxy = clazz.cast(Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class<?>[]{clazz},
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						for(T l : getListeners()){
							method.invoke(l, args);
						}
						return null;
					}
				}));
	}

	public void add(T listener){
		listeners.add(listener);
	}

	public void remove(T listener){
		listeners.remove(listener);
	}

	public Collection<T> getListeners(){
		return listeners;
	}

	public T fire(){
		return fireProxy;
	}

	private List<T> listeners = new ArrayList<>();
	private transient T fireProxy;
}
