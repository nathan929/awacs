/**
* Copyright (c) 2010, Digg, Inc.
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
*
*    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
*    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
*    * Neither the name of the Digg, Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
* @author David Taylor <david@tinystatemachine.com>
*/

package widgets;

import java.io.InputStream;
import java.util.List;

import play.mvc.Scope.Params;


public interface Widget extends Comparable<Widget> {
	public String render(String id, String name, String config, String params, boolean forceReload) throws Exception;
	public Object endpoint(String name, Params params) throws Exception;
	
	public String packageName();
	public String widgetName();
	public String fullName();

	public String describeWidget();
	public String describeConfig();
	
	public List<DefaultInstance> getDefaultInstances();

	public static class DefaultInstance {
		public String name;
		public String generator;
		public String provider;
		public String config;
		public String tags;
	}
	
}
