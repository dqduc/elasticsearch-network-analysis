Network Address Analysis for ElasticSearch
=========================================


    --------------------------------------------------
    | NetworkAddress Analysis Plugin | ElasticSearch |
    --------------------------------------------------
    | 5.6							 | 5.6.3		 |
	| 5.3                            | 5.3.3         |
	| 2.0							 | 2.3.4		 |
    --------------------------------------------------


The plugin includes a `path_keywords` analyzer, a `network_address` analyzer , a `partial_network_address` analyzer, a `full_network_address` analyzer 
and an `incremental_capture_group_token_filter` token-filter.


1. Build
	
	    In the plugin folder,
	    Run `mvn test` to run the tests.
	    Run `mvn package` to build the jar.

2. Install
	
	    - Extract the zip file from the `target/releases` directory to:
	        `/usr/share/elasticsearch/plugins/elasticsearch-network-analysis/`
    	- Restart the ElasticSearch service
	
3. Use

	To test the plugin, the `_analyze` endpoint can be used:

		curl -XPOST http://localhost:9200/_analyze?pretty=1&analyzer=network_address -d '
		computer_name = "ABC"                                                   
		mac_address = AA:BB:CC:DD:EE:FF                                         
		SMBInfo for 192.168.0.1:                                               
			os = Windows Server (R) 2008 Enterprise 6002 Service Pack 2         
			lanman = Windows Server (R) 2008 Enterprise 6.0                     
			domain = WORKGROUP                                                  
			server_time = Tue Apr 24 10:35:57 2012                              
		smb_port = 445                                                          
		'

	 response:

		{
		  "tokens":[
		  {
			"token":"AA",
			"start_offset":103,
			"end_offset":120,
			"type":"word",
			"position":2
		  },
		  {
			"token":"BB",
			...
		  }
		}

	The next step is to update the mapping of your index to use the analyzer:

		mapping = {
			'[your_document_type]': {
				'properties': {
					...,
					'data': {
						"type": "multi_field",
						"fields": {
							# Normal string analysis. 
							"data": {
								'type': 'string',
								'term_vector': 'with_positions_offsets',
							},
							# Our networkaddress custom analysis.
							"network_address": {
								"type": "string",
								"analyzer": "network_address"
							}
						}
					},
					...
				}
			}
		}

		es.put_mapping('[your_index]', '[your_document_type]', mapping)

	Then, documents can be queried using the new analyzed field:

		{
		  "query": {
			"match": {
			  "data.network_address": {
				"query": "AA:BB:CC:DD:EE:FF",
				# 'phrase' will only match exact address (not just parts of it)
				"type": "phrase"
			  }
			}
		  }
		}

	The `partial_network_address` can be used at query time, to search for partial addresses (note that this only affacts the *query* anlysis, not the *document's*):

		{
		  "query": {
			"match": {
			  "data.network_address": {
				"query": "AA:BB:CC",
				"analyzer": "partial_network_address",
				"type": "phrase"
			  }
			}
		  }
		}
		
	The `full_network_address` can be used as an analyzer, to search for all the network addresses inside a given document.

		network_addresses = self._es.indices.analyze(index=index_name, analyzer='full_network_address', body=document).get('tokens')

For any more questions or issues, please [e-mail me](mailto:ofirbrukner@gmail.com).

Have fun :)
