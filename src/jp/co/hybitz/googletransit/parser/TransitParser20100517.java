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
package jp.co.hybitz.googletransit.parser;

import java.io.IOException;
import java.io.InputStream;

import jp.co.hybitz.googletransit.model.TimeAndPlace;
import jp.co.hybitz.googletransit.model.Transit;
import jp.co.hybitz.googletransit.model.TransitDetail;
import jp.co.hybitz.googletransit.model.TransitResult;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TransitParser20100517 implements TransitParser {
    private TransitResult result = new TransitResult();
	private Transit transit;
	private TransitDetail transitDetail;

	public TransitResult parse(InputStream in) throws XmlPullParserException, IOException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(in, null);
		
		int eventType = parser.getEventType();
		do {
			switch (eventType) {
			case XmlPullParser.START_TAG :
				break;
			case XmlPullParser.END_TAG :
				break;
			case XmlPullParser.TEXT :
				String text = parser.getText().trim();
				handleText(text);
				break;
			}
		} while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT);

		return result;
	}
	
	private void handleText(String text) {
	    if (text.matches(".*～.* [0-9]{1,2}:[0-9]{2}(発|着)")) {
	        result.setTitle(text);
	    }
	    else if (text.matches(".*[0-9]*円.*")) {
            if (transit != null) {
                if (transitDetail != null) {
                    transit.addDetail(transitDetail);
                    transitDetail = null;
                }

                result.addTransit(transit);
            }
            transit = new Transit();
            transit.setTimeAndFee(text);
        }
        else if ("逆方向の経路を表示".equals(text)) {
            if (transit != null) {
                if (transitDetail != null) {
                    transit.addDetail(transitDetail);
                    transitDetail = null;
                }
                
                result.addTransit(transit);
            }
            transit = null;
        } else if (text.length() > 0) {
            if (text.matches("[0-9]{1,2}:[0-9]{2}発 .*")) {
                handleDeparture(text);            }
            else if (text.matches("[0-9]{1,2}:[0-9]{2}着 .*")) {
                handleArrival(text);
            } else {
                handleRoute(text);
            }
        }
	}
	
	private void handleDeparture(String text) {
        String[] split = text.split("発 ");
        transitDetail.setDeparture(new TimeAndPlace(split[0], split[1]));
	}
	
	private void handleArrival(String text) {
        String[] split = text.split("着 ");
        transitDetail.setArrival(new TimeAndPlace(split[0], split[1]));
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
