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

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import lib.*;
import models.Dashboard;
import models.Setting;
import models.WidgetInstance;
import widgets.InvalidWidgetException;
import widgets.Widget;

import play.*;
import play.mvc.*;
import play.vfs.VirtualFile;

public class Dynamic extends Controller {
	public static void styles(String pkgName) throws Exception {
		VirtualFile css = WidgetManager.findCSS(pkgName);
		if (css == null)
			notFound();
		else {
			response.contentType = "text/css";
			renderText(css.contentAsString());
		}
	}

	public static void scripts(String pkgName) throws Exception {
		VirtualFile js = WidgetManager.findJS(pkgName);
		if (js == null)
			notFound();
		else {
			response.contentType = "text/javascript";
			renderText(js.contentAsString());
		}
	}

	public static void json(String className, String endPointName)
			throws Exception {
		Widget widget = null;
		try {
			widget = WidgetManager.getWidget(className);
			Object o = widget.endpoint(endPointName, params);
			response.contentType = "text/plain";
			renderJSON(o);
		} catch (Exception e) {
			notFound(className);
		}

	}


}
