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

import java.net.InetSocketAddress;

public class MemberSearchResponder implements MemberSearchListener{
	public MemberSearchResponder(String name) {
		this.name = name;
	}
	public MemberSearchResponder(int group, String name) {
		this.group = group;
		this.name = name;
	}
	@Override
	public MemberSearchResponse onReceive(InetSocketAddress from, String name, int group) {
		if(this.group == 0 || group == this.group){
			return new MemberSearchResponse(true, this.name);
		}
		return new MemberSearchResponse();
	}

	private int group;
	private String name;
}
