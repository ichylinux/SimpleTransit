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
package jp.co.hybitz.simpletransit.history;

import jp.co.hybitz.simpletransit.db.TransitQueryDao;
import jp.co.hybitz.simpletransit.model.TransitQueryEx;
import android.content.Context;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class QueryHistoryWorker implements Runnable {

    private Context context;
    private TransitQueryEx query;
    
    public QueryHistoryWorker(Context context, TransitQueryEx query) {
        this.context = context;
        this.query = query;
    }
    
    public void run() {
        TransitQueryDao dao = new TransitQueryDao(context);
        TransitQueryEx stq = dao.getTransitQuery(query.getFrom(), query.getTo(), query.getStopOver());
        if (stq == null) {
            dao.createTransitQuery(query);
        }
        else {
            dao.updateUseCount(stq.getId(), stq.getUseCount() + 1);
        }
    }

}
