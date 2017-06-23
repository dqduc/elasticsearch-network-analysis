package org.elasticsearch.index.analysis;

import org.elasticsearch.test.ESTestCase;


public class FullNetworkAddressAnalyzerTests extends ESTestCase {

    public void testFullNetworkAddressAnalyzer() throws Exception {
		AnalysisTestUtils.testAnalyzer("full_network_address", 
				"aa 1.2.3.4 bb acw 22.44.33.42.1 22:4e:b2:53:d7:93 acDD.cccc.AAaa kn", 
        		"1.2.3.4", "22.44.33.42", "22:4e:b2:53:d7:93", "acdd.cccc.aaaa");
    }
	
}
