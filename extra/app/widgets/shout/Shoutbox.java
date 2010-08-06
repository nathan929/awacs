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

package widgets.shout;

import java.util.Date;
import java.util.List;

import lib.containers.Feed;
import play.mvc.Scope.Params;
import widgets.BaseWidget;
import widgets.shout.models.Shout;

public class Shoutbox extends BaseWidget {

	public String describeConfig() {
		return "channel";
	}

	public String describeWidget() {
		return "Shows latest shouts";
	}

	public Object endpoint(String name, Params params) throws Exception {
		if(params._contains("create")) {
			Shout s = new Shout(params.get("author"),params.get("text"));
			s.save();			
			return true;
		} else {
			Feed out = new Feed();
			List<Shout> shouts = Shout.find("order by posted desc").fetch(5);
			long limit = 1000 * 60 * 60 * 24 * 5; // 5 days in ms
			for(Shout s : shouts) {
				if( new Date().getTime() - s.posted.getTime() > limit)
					s.delete();
				else
					out.add(s.author, s.body, "");
			}
			return out;	
			
		}
	}

}
