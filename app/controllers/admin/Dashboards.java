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

package controllers.admin;

import java.util.*;

import controllers.CRUD;
import controllers.CRUD.ObjectType;

import lib.WidgetManager;
import lib.containers.*;
import models.*;


import play.*;
import play.db.jpa.JPASupport;
import play.mvc.*;
import widgets.InvalidWidgetException;

public class Dashboards extends CRUD {
	
	static final String c1 = "column-1-content";

	public static void save(String id) throws Exception {

		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		
		Dashboard dash = (Dashboard) type.findById(id);

		if (params.allSimple().containsKey(c1)
				&& !params.allSimple().get(c1).equals("-1")
				&& dash.assignments != null) {
			Assignment[] assignments = new Assignment[dash.assignments.size()];
			dash.assignments.toArray(assignments);
			
			Map<WidgetInstance,String> saveParams = new HashMap<WidgetInstance,String>();
			
			for (Assignment a : assignments) {
				saveParams.put(a.widget, a.params);
				dash.assignments.remove(a);
				a.delete();
			}

			for (String k : params.all().keySet()) {
				if (k.startsWith("column-")) {
					int col = Integer.parseInt(k.substring(7, k
							.lastIndexOf("-")));
					String ids = params.allSimple().get(k);
					String[] idStrings = ids.split(",");
					int i = 0;
					for (String wid : idStrings) {
						if (wid.length() > 0) {
							WidgetInstance w = WidgetInstance.findById(Long.parseLong(wid));
							String params = saveParams.get(w);
							if(params == null) 
								params = "";
							Assignment a = new Assignment(dash, w, col, i++, params);
							a.save();
						}
					}
				}
			}
		}
		
		parent();
	}

}
