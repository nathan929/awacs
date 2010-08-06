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

package models;

import java.io.Serializable;
import java.util.*;
import javax.persistence.*;

import org.w3c.dom.Document;

import play.cache.Cache;
import play.db.jpa.*;
import play.mvc.Scope.Params;

@Entity
public class Setting extends Model  {
	public String name;
	public String value;
	
	public Setting(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public static String get(String name, String defaultValue) {
		return get(name, defaultValue, false);
	}
		
	public static String get(String name, String defaultValue, boolean noCache) {
		Setting s = Cache.get(Setting.class.getName()+name, Setting.class );
		
		if(noCache || s == null) {
			s = Setting.find("byName",name).first();
			if(s == null) {
				s = new Setting(name, defaultValue).save();
				s.em().getTransaction().commit();
				s.em().getTransaction().begin();
			}
			Cache.set(Setting.class.getName()+name, s, "10s" );
		} 
		return s.value;
	}
	
	public String toString() {
		return name;
	}

	public static String getSystemName() {
		return get("system-name", "AWACS Dashboard");
	}
}