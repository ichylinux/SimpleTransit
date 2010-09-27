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
package jp.co.hybitz.simpletransit;

import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.simpletransit.model.TransitQueryEx;
import jp.co.hybitz.test.TestUtils;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;


/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransitTest extends ActivityInstrumentationTestCase2<SimpleTransit> {

    private EditText from;
    private EditText to;
    private Button search;
    
    public SimpleTransitTest() {
        super("jp.co.hybitz.simpletransit", SimpleTransit.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        from = (EditText) getActivity().findViewById(R.id.from);
        to = (EditText) getActivity().findViewById(R.id.to);
        search = (Button) getActivity().findViewById(R.id.search);
    }

    public void testInitView() {
        TransitQueryEx query = (TransitQueryEx) TestUtils.getFieldValue(getActivity(), "query");
        assertNotNull(query);
        assertEquals(TimeType.DEPARTURE, query.getTimeType());
    }
    
    public void testSearch() {

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                from.setText("東京");
                to.setText("名古屋");
                search.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
    }
}
