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
package jp.co.hybitz.simpletransit.model;

import jp.co.hybitz.transit.model.Transit;
import jp.co.hybitz.transit.model.TransitDetail;
import jp.co.hybitz.transit.model.TransitResult;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TransitItem {
    private TransitResult transitResult;
    private Transit transit;

    public TransitItem(TransitResult transitResult, Transit transit) {
        this.transitResult = transitResult;
        this.transit = transit;
    }

    public TransitResult getTransitResult() {
        return transitResult;
    }

    public Transit getTransit() {
        return transit;
    }

    @Override
    public String toString() {
        try {
            String prefectureWithParen = transitResult.getPrefecture() == null ? "" : "（" + transitResult.getPrefecture() + "）";
            
            StringBuilder sb = new StringBuilder();
    
            sb.append(transit.getDurationAndFare());
            if (transit.getTransferCount() > 0) {
                sb.append(" - 乗り換え" + transit.getTransferCount() + "回");
            }
            sb.append("\n");
    
            for (int i = 0; i < transit.getDetails().size(); i ++) {
                TransitDetail detail = transit.getDetails().get(i);
                
                sb.append(detail.getRoute());
    
                if (!detail.isWalking()) {
                    sb.append("\n");
                    sb.append(detail.getDeparture().getTime()).append("発　");
                    sb.append(detail.getDeparture().getPlace().replaceAll(prefectureWithParen, "")).append("\n");
                    sb.append(detail.getArrival().getTime()).append("着　");
                    sb.append(detail.getArrival().getPlace().replaceAll(prefectureWithParen, ""));
                }
    
                if (i < transit.getDetails().size() - 1) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
        catch (RuntimeException e) {
            throw new IllegalStateException(transitResult.getFrom() + " => " + transitResult.getTo(), e);
        }
    }
}
