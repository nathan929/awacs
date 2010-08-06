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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.Helper;
import lib.WidgetManager;
import models.WidgetInstance;
import play.cache.Cache;
import play.mvc.Scope.Params;
import play.vfs.VirtualFile;
import widgets.Widget.DefaultInstance;

public abstract class BaseWidget implements Widget {

	protected String basePath = "widgets.";

	public String render(String id, String name, String config, String params, boolean forceReload)
			throws Exception {
		String html = readRawHTML(forceReload);
		
		if (params == null) {
			params = "";
		}
		if (config == null) {
			config = "";
		}
		html = html.replace("{%WIDGET_CONFIG%}", config);
		html = html.replace("{%WIDGET_PARAMS%}", params);
		html = html.replace("{%WIDGET_NAME%}", name);
		html = html.replace("{%WIDGET_ID%}", id);
		html = html.replace("{%WIDGET_CLASS%}", fullName());
		return html;
	}

	public String packageName() {
		String fullName = this.getClass().getName();
		int cut = fullName.length() - widgetName().length();
		String name = fullName.substring(0, cut - 1);
		return name.substring(basePath.length());
	}

	public String widgetName() {
		return this.getClass().getSimpleName();
	}

	public String fullName() {
		return packageName() + "." + widgetName();
	}

	protected String readRawHTML(boolean forceReload) throws IOException {
		String key = "BaseWidget-html-" + packageName() + widgetName();
		String html = Cache.get(key, String.class);
		if (!forceReload && html != null)
			return html;

		VirtualFile htmlFile = WidgetManager.findHTML(packageName(),
				widgetName());
		if (htmlFile != null) {
			html = htmlFile.contentAsString();
			Cache.set(key, html, "5mn");
			return html;
		}
		return defaultWidgetHTML();
	}

	private String defaultWidgetHTML() {
		return "<h2>Widget!</h2> Create a file called <code>widget.html</code> in "
				+ WidgetManager.widgetPath(this)
				+ " with your HTML to be displayed here";
	}

	public int compareTo(Widget other) {
		return fullName().compareTo(other.fullName());
	}

	public List<DefaultInstance> getDefaultInstances() {
		List<DefaultInstance> all = getPackageDefaultInstances();
		List<DefaultInstance> list = new ArrayList<DefaultInstance>();

		for (DefaultInstance it : all)
			if (it.provider.equals(fullName()))
				list.add(it);

		return list;
	}

	public List<DefaultInstance> getPackageDefaultInstances() {
		List<DefaultInstance> list = new ArrayList();

		VirtualFile defs = WidgetManager.findDefaultInstanceDefs(this);
		if (defs == null)
			return list;

		try {
			String line = null;
			String[] values = new String[5];
			int i = -1;

			BufferedReader reader = new BufferedReader(new FileReader(defs
					.getRealFile()));
			while ((line = reader.readLine()) != null) {

				if (line.startsWith("#"))
					continue;

				if (i == values.length - 1) {
					list.add(makeInstance(values));
					i = -1;
				}

				if (line.startsWith("INSTANCE"))
					i = 0;

				if (i > -1)
					values[i++] = line;

			}
		} catch (IOException e) {
			return list;
		}

		return list;
	}

	public static DefaultInstance makeInstance(String[] values) {
		DefaultInstance it = new DefaultInstance();
		it.generator = values[1]+"_"+values[0];
		it.provider = values[1];
		it.name = values[2];
		it.config = values[3];
		it.tags = values[4];
		return it;
	}
}
