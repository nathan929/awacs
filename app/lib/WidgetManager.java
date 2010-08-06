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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import models.WidgetInstance;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.vfs.VirtualFile;

import widgets.InvalidWidgetException;
import widgets.Widget;
import widgets.Widget.DefaultInstance;

public class WidgetManager {

	protected static String basePath = "widgets.";
	protected static String defaultHtmlFile = "widget.html";
	protected static String defaultCssFile = "styles.css";
	protected static String defaultJsFile = "behavior.js";
	protected static String defaultInstanceDefs = "default.instances";

	protected Map<String, Widget> widgets = new HashMap<String, Widget>();

	public static VirtualFile findCSS(Widget widget) {
		return findCSS(widget.packageName());
	}

	public static VirtualFile findCSS(String packageName) {
		String path = widgetPath(packageName) + defaultCssFile;
		return VirtualFile.search(Play.javaPath, path);
	}

	public static VirtualFile findJS(Widget widget) {
		return findJS(widget.packageName());
	}

	public static VirtualFile findJS(String packageName) {
		String path = widgetPath(packageName) + defaultJsFile;
		return VirtualFile.search(Play.javaPath, path);
	}

	public static VirtualFile findDefaultInstanceDefs(Widget widget) {
		String path = widgetPath(widget.packageName()) + defaultInstanceDefs;
		return VirtualFile.search(Play.javaPath, path);
	}

	public static VirtualFile findHTML(String packageName, String className) {
		String path = widgetPath(packageName);
		String specificPath = File.separator + path + className + ".html";
		String genericPath = File.separator + path + defaultHtmlFile;

		VirtualFile widgetHTML = VirtualFile
				.search(Play.javaPath, specificPath);

		if (widgetHTML != null)
			return widgetHTML;
		return VirtualFile.search(Play.javaPath, genericPath);
	}

	public static Widget getWidget(String name) throws InvalidWidgetException {
		try {
			Widget w = (Widget) Class.forName(basePath + name).newInstance();
			checkForDefaultInstances(w);
			return w;
		} catch (ClassNotFoundException e) {
			throw new InvalidWidgetException(basePath + name);
		} catch (InstantiationException e) {
			throw new InvalidWidgetException(basePath + name);
		} catch (IllegalAccessException e) {
			throw new InvalidWidgetException(basePath + name);
		}
	}

	private static void checkForDefaultInstances(Widget w) {
		for (DefaultInstance di : w.getDefaultInstances()) {
			if (!defaultExists(di)) {
				WidgetInstance.createFromDefault(di);
			}
		}
	}

	public static String widgetPath(String packageName) {
		return (basePath + packageName + ".").replace(".", File.separator);
	}

	public static String widgetPath(Widget w) {
		return (basePath + w.packageName() + ".").replace(".", File.separator);
	}

	public void add(String widgetName) throws InvalidWidgetException {
		Widget w = getWidget(widgetName);
		widgets.put(w.fullName(), w);
	}

	public Set<String> getJSIncludeList() {
		Set<String> jsFiles = new HashSet<String>();
		for (Entry<String, Widget> widget : widgets.entrySet()) {
			VirtualFile js = findJS(widget.getValue());
			if (js != null) {
				jsFiles.add(widget.getValue().packageName());
			}
		}
		return jsFiles;
	}

	public Set<String> getCSSIncludeList() {
		Set<String> cssFiles = new HashSet<String>();
		for (Entry<String, Widget> widget : widgets.entrySet()) {
			VirtualFile css = findCSS(widget.getValue());
			if (css != null) {
				cssFiles.add(widget.getValue().packageName());
			}
		}
		return cssFiles;
	}

	public static boolean isWidget(String name) {
		try {
			getWidget(name);
			return true;
		} catch (ClassCastException e) {
			return false;
		} catch (InvalidWidgetException e) {
			return false;
		}
	}

	public static SortedSet<String> getAllWidgetNames() {
		SortedSet<String> names = new TreeSet<String>();
		for (Widget w : getAllWidgets())
			names.add(w.fullName());
		return names;
	}

	public static SortedSet<Widget> getAllWidgets() {
		return getAllWidgets(false);
	}

	public static SortedSet<Widget> getAllWidgets(boolean force) {
		String key = "WidgetManager-allWidgets";
		SortedSet<Widget> _widgets = Cache.get(key, SortedSet.class);
		if (force)
			Logger.info("Forced scan for widgets");

		if (_widgets == null || force) {
			_widgets = findAllWidgets();
			Cache.set(key, _widgets, "1h");
		}
		return _widgets;
	}

	public static boolean defaultExists(DefaultInstance i) {
		String key = "WidgetManager-default-" + i.generator + "-exists";
		if (Cache.get(key) != null)
			return true;

		WidgetInstance wi = WidgetInstance.find("byGenerator", i.generator)
				.first();

		if (wi != null)
			Cache.set(key, true, "1h");

		return (wi != null);
	}

	protected static SortedSet<Widget> findAllWidgets() {
		String ext = ".java";
		Logger.debug("Scanning for widgets...");

		SortedSet<Widget> all = new TreeSet<Widget>();

		for (VirtualFile f : Play.javaPath) {
			VirtualFile widgets = f.child("widgets");
			if (widgets.exists()) {
				Logger.debug("Searching " + widgets.getName());
				for (VirtualFile pkg : widgets.list()) {
					if (pkg != null && pkg.isDirectory()) {
						for (VirtualFile w : pkg.list()) {
							if (w.getName().endsWith(ext)) {
								String className = w.getName().substring(0,
										w.getName().length() - ext.length());
								String widgetName = pkg.getName() + "."
										+ className;
								if (isWidget(widgetName))
									try {
										all.add(getWidget(widgetName));
										Logger.debug("Found " + widgetName);
									} catch (InvalidWidgetException e) {
										Logger.error(e, widgetName
												+ " is not a widget?!");
									}
								else
									Logger.warn(widgetName
											+ " is not a widget.");
							}
						}
					}
				}
			}
		}
		Logger.info("Found " + all.size() + " widgets");
		return all;
	}

	public static List<String> deleteInvalidInstances() {
		List<WidgetInstance> all = WidgetInstance.findAll();
		List<String> deleted = new ArrayList();
		for (WidgetInstance w : all) {
			try {
				w.getWidget();
			} catch (InvalidWidgetException e) {
				deleted.add(w.name+" ("+w.getProvider()+")");
				Logger.info("Removing instance of invalid widget:"+w.getProvider());
				w.delete();
			}
		}
		return deleted;
	}
}
