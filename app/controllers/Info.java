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

package controllers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lib.WidgetManager;
import models.Dashboard;
import models.Setting;
import models.WidgetInstance;
import play.mvc.Controller;
import widgets.InvalidWidgetException;
import widgets.Widget;

public class Info extends Controller {
	public static void index() {
		String title = Setting.getSystemName();
		List<Dashboard> dashboards = Dashboard.findAll();
		render(title, dashboards);
	}

	public static void widgets() throws InvalidWidgetException {
		List<String> deleted = null;
		if(params._contains("repair")) {
			deleted = WidgetManager.deleteInvalidInstances();
		}
		
		Set<Widget> widgets = WidgetManager.getAllWidgets(params._contains("rescan") || params._contains("repair"));

		Map<String, List<WidgetInstance>> instances = WidgetInstance
				.getAllByProvider();
		
		render(widgets, instances, deleted);
	}

	public static void descConfig(String className) {
		Widget widget = null;
		try {
			widget = WidgetManager.getWidget(className);
		} catch (Exception e) {
			notFound(className);
		}

		renderText(widget.describeConfig());
	}

	public static void sandbox(String className, String config, String wparams)
			throws InvalidWidgetException {
		if (params._contains("object.provider")) {
			className = params.get("object.provider");
			config = params.get("object.config");
			wparams = params.get("wparams");
		}

		String title = Setting.getSystemName();

		if (className == null)
			className = "debug.Debug";
		if (config == null)
			config = "";
		if (wparams == null)
			wparams = "";

		Long wid = 999L;

		WidgetInstance widget = new WidgetInstance("Demo Widget Instance",
				"sandbox1", className, config, "");
		WidgetManager wm = new WidgetManager();
		wm.add(widget.getProvider());

		Set<String> cssIncludes = wm.getCSSIncludeList();
		Set<String> jsIncludes = wm.getJSIncludeList();
		boolean interactive = true;

		render(title, widget, wid, wparams, cssIncludes, jsIncludes,
				interactive);
	}
}
