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
package jp.co.hybitz.googletransit.model;

import java.io.Serializable;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TransitDetail implements Serializable {

	private String route;
	private TimeAndPlace departure;
	private TimeAndPlace arrival;

	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public TimeAndPlace getDeparture() {
		return departure;
	}
	public void setDeparture(TimeAndPlace departure) {
		this.departure = departure;
	}
	public TimeAndPlace getArrival() {
		return arrival;
	}
	public void setArrival(TimeAndPlace arrival) {
		this.arrival = arrival;
	}
	
	public boolean isWalking() {
		return route.startsWith("徒歩");
	}
}
