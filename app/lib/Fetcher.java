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

package lib;

import org.w3c.dom.Document;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import play.cache.Cache;
import play.libs.WS;
import play.libs.WS.HttpResponse;

/**
 * Caching wrapper to fetch XML, JSON or simple strings from remote hosts
 * Wow, forgot how much copy/paste code happens in non-functional languages
 * 
 * @author dtaylor
 * 
 */
public class Fetcher {

	static final String cacheTime = "60s";

	public static Document xml(String url) {
		String key = url + "xml";
		Document resp = Cache.get(key, Document.class);
		if (resp == null) {
			HttpResponse r = WS.url(url).get();
			if (r.getStatus() < 400) {
				resp = r.getXml();
				Cache.set(key, resp, cacheTime);
			}
		}
		return resp;
	}

	public static JsonElement json(String url) {
		return json(url, true);
	}
	
	public static JsonElement json(String url, boolean cache) {
		return json(url, cache, cacheTime);
	}
	
	public static JsonElement json(String url, boolean cache, String cacheTime) {
		String json = string(url, cache, cacheTime);
		if (json == null) return null;
		return new JsonParser().parse(json);
	}

	public static String string(String url) {
		return string(url, true);
	}
	
	public static String string(String url, boolean cache) {
		return string(url, cache, cacheTime);
	}
	
	public static String string(String url, boolean cache, String cacheTime) {
		String resp = null;
		
		String key = url + "string";
		if(cache)
			resp = Cache.get(key, String.class);
		
		if (resp == null) {
			HttpResponse r = WS.url(url).get();
			if (r.getStatus() < 400) {
				resp = r.getString();
				if(cache)
					Cache.set(key, resp, cacheTime);
			}
		}
		
		return resp;
	}

}
