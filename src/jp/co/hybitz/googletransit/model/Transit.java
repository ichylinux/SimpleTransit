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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class Transit implements Serializable {

	private String timeAndFee;
	private List<TransitDetail> details = new ArrayList<TransitDetail>();

	public String getTimeAndFee() {
		return timeAndFee;
	}
	public void setTimeAndFee(String title) {
		this.timeAndFee = title;
	}
	
	public List<TransitDetail> getDetails() {
		return details;
	}
	
	public void addDetail(TransitDetail detail) {
		details.add(detail);
	}
	
	public int getTransferCount() {
		int ret = 0;
		boolean isFirst = true;
		for (Iterator<TransitDetail> it = details.iterator(); it.hasNext();) {
			TransitDetail detail = it.next();
			if (detail.isWalking()) {
				continue;
			}
			
			// 最初の交通機関の乗車は乗り換え回数には含めない
			if (isFirst) {
			    isFirst = false;
			} else {
			    ret ++;
			}
		}
		
		return ret;
	}
}
