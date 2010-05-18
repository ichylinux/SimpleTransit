/**
 * Copyright (C) 2010 Hybitz.co.ltd
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * 
 */
package jp.co.hybitz.simpletransit.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.co.hybitz.simpletransit.model.TimeAndPlace;
import jp.co.hybitz.simpletransit.model.Transit;
import jp.co.hybitz.simpletransit.model.TransitDetail;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TransitParser20100517 implements TransitParser {
	private Transit transit;
	private TransitDetail transitDetail;

	public List<Transit> parse(InputStream in) throws XmlPullParserException, IOException {
		List<Transit> ret = new ArrayList<Transit>();
		
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(in, null);
		
		int eventType = parser.getEventType();
		do {

			switch (eventType) {
			case XmlPullParser.START_TAG :
//				String tag = parser.getName();
//				sb.append("<" + tag + ">");
				break;
			case XmlPullParser.END_TAG :
				break;
			case XmlPullParser.TEXT :
				String text = parser.getText().trim();
			
				if (text.matches(".*[0-9]*円.*")) {
					if (transit != null) {
						if (transitDetail != null) {
							transit.addDetail(transitDetail);
							transitDetail = null;
						}

						ret.add(transit);
					}
					transit = new Transit();
					transit.setTitle(text);
				}
				else if ("逆方向の経路を表示".equals(text)) {
					if (transit != null) {
						if (transitDetail != null) {
							transit.addDetail(transitDetail);
							transitDetail = null;
						}
						
						ret.add(transit);
					}
					transit = null;
				} else if (text.length() > 0) {
					if (text.matches("[0-9]{1,2}:[0-9]{2}発 .*")) {
					    String[] split = text.split("発 ");
						transitDetail.setDeparture(new TimeAndPlace(split[0], split[1]));
					}
					else if (text.matches("[0-9]{1,2}:[0-9]{2}着 .*")) {
                        String[] split = text.split("着 ");
						transitDetail.setArrival(new TimeAndPlace(split[0], split[1]));
					} else {
						handleRoute(text);
					}
				}
				break;
			}
		} while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT);

		return ret;
	}
	
	private void handleRoute(String text) {
		if (transit == null) {
			return;
		}
		
		if (transitDetail != null) {
			transit.addDetail(transitDetail);
		}
		
		transitDetail = new TransitDetail();
		transitDetail.setRoute(text);
	}
}
