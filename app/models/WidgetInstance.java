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

import java.util.*;

import javax.persistence.*;

import lib.WidgetManager;

import play.data.validation.*;
import play.db.jpa.*;
import widgets.InvalidWidgetException;
import widgets.Widget;
import widgets.Widget.DefaultInstance;

@Entity
public class WidgetInstance extends Model implements Comparable<WidgetInstance> {

	@Required 
	public String name;
	
	public String generator;
	
	public String provider;

	@MaxSize(10000)
	@Lob
	public String config;
	
	public String tags;
	
	// min length of <pkg>.<Class> = 3 (eg a.X)
	private static final int minProviderLength = 3;

	@OneToMany(mappedBy="widget", cascade=CascadeType.ALL)
	public Set<Assignment> assignments;
	
	public WidgetInstance(String name, String generator, String provider, String config, String tags) {
		this.name = name;
		this.generator = generator;
		this.provider = provider;
		this.config = config;
		this.tags = tags;
	}
	
	public String getProvider() {
		if(provider == null || provider.length() < minProviderLength) {
			return "raw.HTML";
		}
		return provider;
	}
	
	public Widget getWidget() throws InvalidWidgetException {
		return WidgetManager.getWidget(this.provider);
	}
	
	public String render(Long id, String params) throws InvalidWidgetException, Exception {
		return render(id, params, false);
	}
	
	public String render(Long id, String params, boolean forceReload) throws InvalidWidgetException, Exception {
		return getWidget().render(id.toString(), name, config, params, forceReload);
	}
	
	public String toString() {
		return name;
	}
	
	public static List<WidgetInstance> sorted() {
		List<WidgetInstance> all = findAll();
		Collections.sort(all);
		return all;
	}

	public int compareTo(WidgetInstance o) {
		int hasMore = ((Integer) assignments.size()).compareTo(o.assignments.size()) / -1;
		if(hasMore == 0) {
			return name.compareTo(o.name);
		}
		return hasMore;
	}

	public static Map<String, List<WidgetInstance>> getAllByProvider() throws InvalidWidgetException {
		Map<String, List<WidgetInstance>> all = new HashMap<String, List<WidgetInstance>>();
		
		List<WidgetInstance> instances = WidgetInstance.all().fetch();
		for( WidgetInstance w : instances) {
			String name = w.getWidget().fullName();
			if(!all.containsKey(name))
					all.put(name, new ArrayList());
			all.get(name).add(w);
		}
		
		for( String name : WidgetManager.getAllWidgetNames()) {
			if(!all.containsKey(name))
					all.put(name, new ArrayList());
		}
		
		return all;
	}

	public static void createFromDefault(DefaultInstance di) {
		WidgetInstance wi = new WidgetInstance(di.name, di.generator, di.provider, di.config, di.tags);
		wi.save();
	}

}
