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

package widgets.rss;

import java.io.IOException;
import java.net.URL;

import java.util.Iterator;

import lib.containers.Feed;
import models.Setting;


import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.*;

import play.cache.Cache;
import play.mvc.Scope.Params;
import widgets.*;

public class RSS extends BaseWidget {
	public int limit = 5;

	public Object endpoint(String name, Params params) throws Exception {
		String url = params.get("url");
		String cacheKey = this.getClass().getName() + url;
		Feed out = Cache.get(cacheKey, Feed.class);
		if (out != null)
			return out;
		out = new Feed();
	    URL source = new URL(url);
		SyndFeedInput raw = new SyndFeedInput();
		SyndFeed feed = raw.build(new XmlReader(source));
		Iterator<SyndEntry> itr = feed.getEntries().iterator();
		int i = 0;
		while( itr.hasNext() && i++ < limit) {
			SyndEntry s = itr.next();
			out.add(null, s.getTitle(), s.getLink());
		}
		Cache.set(cacheKey, out, "60s");
		return out;
	}

     public String describeConfig() {
		return "URL of RSS Feed";
	}
 	
 	public String describeWidget() {
 		return "RSS Feed Widget";
 	}
}
