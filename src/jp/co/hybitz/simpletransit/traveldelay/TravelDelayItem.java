package jp.co.hybitz.simpletransit.traveldelay;

import java.io.Serializable;
import java.util.Iterator;

import jp.co.hybitz.traveldelay.model.Category;
import jp.co.hybitz.traveldelay.model.TravelDelay;

class TravelDelayItem implements Serializable {
    private Category category;
    private TravelDelay delay;

    public TravelDelayItem(Category category) {
        this.category = category;
    }
    
    public TravelDelayItem(TravelDelay delay) {
        this.delay = delay;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public String getTitle() {
        if (category != null) {
            return category.getName();
        }
        else if (delay != null) {
            if (delay.getLine() != null) {
                return delay.getDate() + "　" + delay.getLine();
            }
            else {
                return delay.getDate() + "　" + delay.getAirline() + "　" + delay.getPlace();
            }
        }
        
        return null;
    }
    
    public String getInformation() {
        if (category != null) {
            StringBuilder sb = new StringBuilder();
            for (Iterator<TravelDelay> it = category.getOperationCompanies().get(0).getTravelDelays().iterator(); it.hasNext();) {
                TravelDelay delay = it.next();
                sb.append(delay.getCondition());
                if (it.hasNext()) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
        else if (delay != null) {
            return delay.getCondition();
        }
        
        return null;
    }
}
