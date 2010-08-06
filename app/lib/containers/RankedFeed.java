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

package lib.containers;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

import lib.containers.Feed.Item;



public class RankedFeed extends Feed {

	public Comparator<Item> sorter = new Ranker();
	
	public class Ranker implements Comparator<Item> {
		public int compare(Item a, Item b) {
			String me = (a.content == null || a.content.isEmpty()) ? a.title : a.content;
			String them = (b.content == null || b.content.isEmpty()) ? b.title : b.content;
			try {
				NumberFormat numF = NumberFormat.getInstance();
				Integer x = numF.parse(me).intValue();
				Integer y = numF.parse(them).intValue();
				return x.compareTo(y);
			} catch (IndexOutOfBoundsException e) {
			} catch (ParseException e) {
			}
			return me.compareTo(them);
		}
	}

}
